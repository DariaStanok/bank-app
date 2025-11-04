package ru.practicum.cash.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.cash.integration.AccountsClient;
import ru.practicum.cash.model.CashOperation;
import ru.practicum.cash.repository.CashOperationRepository;
import ru.practicum.client.NotificationsClient;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;
import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;
import ru.practicum.platform.contracts.enums.CashOpStatus;
import ru.practicum.platform.contracts.enums.NotificationEvent;
import ru.practicum.platform.contracts.enums.OperationType;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.web.exception.BadRequestException;
import ru.practicum.web.exception.ConflictException;
import ru.practicum.web.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final CashOperationRepository operations;
    private final AccountsClient accounts;
    private final NotificationsClient notifications;
    private final ModelMapper mapper;

    @Override 
    @Transactional
    public CashOperationViewDto deposit(CashChangeDto dto, String idempotencyKey) {
        return process(OperationType.DEPOSIT, dto, idempotencyKey);
    }

    @Override 
    @Transactional
    public CashOperationViewDto withdraw(CashChangeDto dto, String idempotencyKey) {
        return process(OperationType.WITHDRAW, dto, idempotencyKey);
    }

    private CashOperationViewDto process(OperationType type, CashChangeDto dto, String rawIdemKey) {
        validateInput(dto);
        String idemKey = normalizeIdempotencyKey(rawIdemKey);
        CashOperation existing = findSucceededByIdempotency(idemKey, dto.accountId(), type);
        if (existing != null) {
        	return toView(existing);
        }

        AccountView account = loadAndValidateAccount(dto);
        CashOperation op = createPendingOperation(type, dto, idemKey);
        NewBalanceDto newBal = applyBalanceChangeInAccounts(dto, type);
        finalizeSuccess(op, newBal.newBalance(), newBal.at());
        notifyUser(type, account,op);
        return toView(op);
    }

    private void validateInput(CashChangeDto dto) {
        if (dto == null || dto.accountId() == null || dto.currency() == null) {
            throw new BadRequestException("VALIDATION_ERROR");
        }
        if (dto.amount() == null || dto.amount().signum() <= 0) {
            throw new BadRequestException("Amount must be > 0");
        }
    }

    private String normalizeIdempotencyKey(String key) {
        return (key == null || key.isBlank()) ? "idem_" + UUID.randomUUID() : key;
    }

    private CashOperation findSucceededByIdempotency(String idemKey, Long accountId, OperationType type) {
        Optional<CashOperation> existing = operations
                .findByIdempotencyKeyAndTypeAndAccountId(idemKey, type, accountId); 
        return existing.filter(op -> op.getStatus() == CashOpStatus.SUCCEEDED).orElse(null);
    }

    private AccountView loadAndValidateAccount(CashChangeDto dto) {
      AccountView acc = accounts.getAccount(dto.accountId());
        if (acc == null) throw new NotFoundException("ACCOUNT_NOT_FOUND");
        if (!acc.currency().equals(dto.currency())) {
            throw new ConflictException("CURRENCY_MISMATCH");
        }
        return acc;
    }

    private CashOperation createPendingOperation(OperationType type, CashChangeDto dto, String idemKey) {
        CashOperation op = new CashOperation();
        op.setId("csh_" + UUID.randomUUID());
        op.setType(type);
        op.setAccountId(dto.accountId());
        op.setCurrency(dto.currency());
        op.setAmount(dto.amount());
        op.setStatus(CashOpStatus.PENDING);
        op.setIdempotencyKey(idemKey);
        op.setCreatedAt(Instant.now());
        return operations.save(op);
    }

    private NewBalanceDto applyBalanceChangeInAccounts(CashChangeDto dto, OperationType type) {
      BalanceChangeDto body = new BalanceChangeDto(type, dto.currency(), dto.amount());
        return accounts.changeBalance(dto.accountId(), body);
    }

    private void finalizeSuccess(CashOperation op, BigDecimal newBalance, Instant completedAt) {
        op.setStatus(CashOpStatus.SUCCEEDED);
        op.setNewBalance(newBalance);
        op.setCompletedAt(completedAt != null ? completedAt : Instant.now());
        operations.save(op);
    }

    private void notifyUser(OperationType type, AccountView acc, CashOperation op) {
        NotificationEvent event = (type == OperationType.DEPOSIT)
                ? NotificationEvent.CASH_DEPOSITED
                : NotificationEvent.CASH_WITHDRAWN;

        String msg = switch (type) {
        case DEPOSIT -> "Deposited " + op.getAmount() + " " + acc.currency() +
                " to account " + acc.id();
        case WITHDRAW -> "Withdrew " + op.getAmount() + " " + acc.currency() +
                " from account " + acc.id();
        default -> "Cash operation: " + op.getId();
    };
    notifications.send(new SendNotificationRequest(
    		event,
    		op.getId(),
    		null,
    		msg,
    		Instant.now()
    ));
  
    }

    private CashOperationViewDto toView(CashOperation op) {
        return mapper.map(op, CashOperationViewDto.class);
    }
}
