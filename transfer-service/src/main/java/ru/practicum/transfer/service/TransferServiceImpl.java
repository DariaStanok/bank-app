package ru.practicum.transfer.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.client.NotificationsClient;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.blocker.BlockerCheckRequest;
import ru.practicum.platform.contracts.blocker.BlockerCheckResponse;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.NotificationEvent;
import ru.practicum.platform.contracts.enums.OperationType;
import ru.practicum.platform.contracts.enums.TransferStatus;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;
import ru.practicum.transfer.config.TransferSettings;
import ru.practicum.transfer.integration.AccountsClient;
import ru.practicum.transfer.integration.BlockerClient;
import ru.practicum.transfer.integration.ExchangeClient;
import ru.practicum.transfer.model.Transfer;
import ru.practicum.transfer.repository.TransferRepository;
import ru.practicum.web.exception.BadRequestException;
import ru.practicum.web.exception.ConflictException;
import ru.practicum.web.exception.ForbiddenException;
import ru.practicum.web.exception.NotFoundException;
import ru.practicum.web.exception.ServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

	private final TransferRepository repository;
	private final AccountsClient accounts;
	private final NotificationsClient notifications;
	private final ModelMapper mapper;

	private final TransferSettings settings;
	private final ExchangeClient exchangeClient;
	private final BlockerClient blockerClient;

	@Override
	@Transactional(readOnly = true)
	public TransferViewDto getById(Long id) {
		Transfer entity = repository.findById(id).orElseThrow(() -> new NotFoundException("TRANSFER_NOT_FOUND"));
		return mapper.map(entity, TransferViewDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public TransferViewDto getByOperationId(String operationId) {
		Transfer entity = repository.findByOperationId(operationId)
				.orElseThrow(() -> new NotFoundException("TRANSFER_NOT_FOUND"));
		return mapper.map(entity, TransferViewDto.class);
	}

	@Override
	@Transactional
	public TransferViewDto create(TransferDto dto) {
		validate(dto);

		Transfer tx = repository.findByOperationId(dto.operationId()).orElseGet(() -> {
			AccountView from = requireAccount(dto.fromAccountId(), "ACCOUNT_FROM_NOT_FOUND");
			AccountView to = requireAccount(dto.toAccountId(), "ACCOUNT_TO_NOT_FOUND");

			if (Boolean.TRUE.equals(settings.blockerEnabled())) {
				BlockerCheckResponse resp = blockerClient
						.check(new BlockerCheckRequest(from.id(), to.id(), dto.amount()));
				if (resp == null || !resp.allowed())
					throw new ForbiddenException("TRANSFER_BLOCKED");
			}

			Transfer pending = createPendingTransfer(dto.operationId(), dto.amount(), dto.currency(), from,
					to);
			return executeAndFinalize(pending, from, to);
		});

		return mapper.map(tx, TransferViewDto.class);
	}

	private Transfer createPendingTransfer(String operationId, BigDecimal inputAmount, Currency inputCur,
			AccountView from, AccountView to) {
		Currency fromCur = from.currency();
		Currency toCur = to.currency();

		boolean sameFromTo = fromCur == toCur;
		boolean inputIsFromCur = inputCur == fromCur;
		boolean inputIsToCur = inputCur == toCur;

		BigDecimal rate;
		BigDecimal debitAmount;
		BigDecimal creditAmount;

		if (sameFromTo && inputIsFromCur) {
			rate = BigDecimal.ONE;
			debitAmount = inputAmount;
			creditAmount = inputAmount;
		} else {
			if (!Boolean.TRUE.equals(settings.exchangeRequired())) {
				throw new ServiceUnavailableException("CROSS_CURRENCY_DISABLED");
			}
			rate = sameFromTo ? BigDecimal.ONE : exchangeClient.getRate(fromCur, toCur);
			debitAmount = inputIsFromCur ? inputAmount
					: exchangeClient.getRate(inputCur, fromCur).multiply(inputAmount);
			creditAmount = inputIsToCur ? inputAmount : exchangeClient.getRate(inputCur, toCur).multiply(inputAmount);
		}

		Transfer tx = Transfer.builder().operationId(operationId).fromAccountId(from.id()).toAccountId(to.id())
				.amount(inputAmount).currency(inputCur).rate(rate).debitAmount(debitAmount).creditAmount(creditAmount)
				.status(TransferStatus.PENDING).build();

		return repository.save(tx);
	}

	private Transfer executeAndFinalize(Transfer tx, AccountView from, AccountView to) {
		Long fromId = from.id();
		Long toId = to.id();

		try {
			accounts.changeBalance(fromId,
					new BalanceChangeDto(OperationType.WITHDRAW, from.currency(), tx.getDebitAmount()));
		} catch (BadRequestException | ConflictException | NotFoundException | ServiceUnavailableException e) {
			tx.setStatus(TransferStatus.FAILED);
			repository.save(tx);
			notifyTransfer(tx, fromId, toId);
			throw new ConflictException("TRANSFER_FAILED");
		} catch (RuntimeException e) {
			tx.setStatus(TransferStatus.FAILED);
			repository.save(tx);
			notifyTransfer(tx, fromId, toId);
			throw e;
		}
		
		try {
			accounts.changeBalance(toId,
					new BalanceChangeDto(OperationType.DEPOSIT, to.currency(), tx.getCreditAmount()));

			tx.setStatus(TransferStatus.COMPLETED);
			repository.save(tx);
			notifyTransfer(tx, fromId, toId);
			return tx;

		} catch (BadRequestException | ConflictException | NotFoundException | ServiceUnavailableException e) {
			rollbackDebit(tx, fromId, from.currency());
			repository.save(tx);
			notifyTransfer(tx, fromId, toId);
			throw new ConflictException("TRANSFER_" + tx.getStatus().name());
		} catch (RuntimeException e) {
			rollbackDebit(tx, fromId, from.currency());
			repository.save(tx);
			notifyTransfer( tx, fromId, toId);
			throw e;
		}
	}

	private void rollbackDebit(Transfer tx, Long fromId, Currency fromCur) {
		try {
			accounts.changeBalance(fromId, new BalanceChangeDto(OperationType.DEPOSIT, fromCur, tx.getDebitAmount()));
			tx.setStatus(TransferStatus.REVERSED);
		} catch (RuntimeException ignore) {
			tx.setStatus(TransferStatus.FAILED);
		}
	}

	private AccountView requireAccount(Long id, String code) {
		AccountView acc = accounts.getAccount(id);
		if (acc == null)
			throw new NotFoundException(code);
		return acc;
	}

	private void validate(TransferDto dto) {
		if (dto == null)
			throw new BadRequestException("VALIDATION_ERROR");
		if (dto.operationId() == null || dto.operationId().isBlank())
			throw new BadRequestException("OPERATION_ID_EMPTY");
		if (dto.fromAccountId() == null || dto.toAccountId() == null)
			throw new BadRequestException("ACCOUNT_IDS_REQUIRED");
		if (dto.currency() == null)
			throw new BadRequestException("CURRENCY_REQUIRED");
		if (dto.amount() == null || dto.amount().signum() <= 0)
			throw new BadRequestException("AMOUNT_MUST_BE_POSITIVE");
	}

	private void notifyTransfer(Transfer tx, Long fromId, Long toId) {
	    NotificationEvent event = (tx.getStatus() == TransferStatus.COMPLETED)
	            ? NotificationEvent.TRANSFER_COMPLETED
	            : NotificationEvent.TRANSFER_FAILED;

	    String msg = switch (tx.getStatus()) {
	        case COMPLETED -> "Transferred " + tx.getAmount() + " " + tx.getCurrency()
	                + " from account " + fromId + " to account " + toId
	                + " (operation " + tx.getOperationId() + ")";
	        case FAILED -> "Transfer failed: " + tx.getAmount() + " " + tx.getCurrency()
	                + " from account " + fromId + " to account " + toId
	                + " (operation " + tx.getOperationId() + "), status=" + tx.getStatus();
	        default -> "Transfer operation: " + tx.getOperationId();
	    };

	    notifications.send(new SendNotificationRequest(
	            event,
	            tx.getOperationId(),  
	            null,                 
	            msg,                  
	            Instant.now()        
	    ));
	} 
}
