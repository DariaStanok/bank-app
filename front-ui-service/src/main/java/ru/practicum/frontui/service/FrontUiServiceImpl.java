package ru.practicum.frontui.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.dto.RegisterRequestDto;
import ru.practicum.frontui.integration.AccountsClient;
import ru.practicum.frontui.integration.AuthClient;
import ru.practicum.frontui.integration.CashClient;
import ru.practicum.frontui.integration.NotificationsQueryClient;
import ru.practicum.frontui.integration.TransferClient;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;
import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.OperationType;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;
import ru.practicum.web.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class FrontUiServiceImpl implements FrontUiService {
	
	    private final AuthClient authClient;
	    private final AccountsClient accountsClient;
	    private final CashClient cashClient;
	    private final TransferClient transferClient;
	    private final NotificationsQueryClient notifications;

	    @Override
	    public void register(RegisterRequestDto dto) {
	        if (dto == null) throw new BadRequestException("REGISTER_PAYLOAD_REQUIRED");
	        if (isBlank(dto.getUsername())) throw new BadRequestException("USERNAME_REQUIRED");
	        if (isBlank(dto.getPassword())) throw new BadRequestException("PASSWORD_REQUIRED");
	        if (isBlank(dto.getEmail())) throw new BadRequestException("EMAIL_REQUIRED");
	        authClient.register(dto);
	    }

	    @Override
	    public UserViewDto getUserSnapshot(Long userId) {
	        requireUserId(userId);
	        return accountsClient.getUserSnapshot(userId);
	    }
	    
		@Override
		public AccountView getAccount(Long userId, Long accountId) {
			requireUserId(userId);
			requireAccountId(accountId);
			return accountsClient.getAccount(userId, accountId);
		}

	    @Override
	    public List<AccountView> getAccounts(Long userId) {
	        requireUserId(userId);
	        return accountsClient.getAccounts(userId);
	    }

	    @Override
	    public AccountView createAccount(Long userId, UpdateExternalAccount request) {
	        requireUserId(userId);
	        if (request == null) throw new BadRequestException("ACCOUNT_PAYLOAD_REQUIRED");
	        return accountsClient.createAccount(userId, request);
	    }

	    @Override
	    public AccountView updateAccount(Long userId, Long accountId, UpdateExternalAccount request) {
	        requireUserId(userId);
	        requireAccountId(accountId);
	        if (request == null) throw new BadRequestException("ACCOUNT_PAYLOAD_REQUIRED");
	        return accountsClient.updateAccount(userId, accountId, request);
	    }

	    @Override
	    public Boolean deleteAccount(Long userId, Long accountId) {
	        requireUserId(userId);
	        requireAccountId(accountId);
	        return accountsClient.deleteAccount(userId, accountId);
	    }


	    @Override
	    public CashOperationViewDto deposit(CashChangeDto dto, String idempotencyKey) {
	        return executeCash(OperationType.DEPOSIT, dto, idempotencyKey);
	    }

	    @Override
	    public CashOperationViewDto withdraw(CashChangeDto dto, String idempotencyKey) {
	        return executeCash(OperationType.WITHDRAW, dto, idempotencyKey);
	    }


	    @Override
	    public TransferViewDto transfer(TransferDto dto) {
	    	if (dto == null) throw new BadRequestException("TRANSFER_PAYLOAD_REQUIRED");
	        requireAccountId(dto.fromAccountId());
	        requireAccountId(dto.toAccountId());
	        requirePositive(dto.amount());
	        
	        if (isBlank(dto.operationId())) {
	            dto = dto.toBuilder()
	                     .operationId(UUID.randomUUID().toString())
	                     .build();
	        }
	        return transferClient.transfer(dto);
	    }

	    @Override
	    public TransferViewDto transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String operationId) {
	        requireAccountId(fromAccountId);
	        requireAccountId(toAccountId);
	        requirePositive(amount);
	        TransferDto dto = TransferDto.builder()
	                .fromAccountId(fromAccountId)
	                .toAccountId(toAccountId)
	                .amount(amount)
	                .operationId(isBlank(operationId) ? UUID.randomUUID().toString() : operationId)
	                .build();
	        return transferClient.transfer(dto);
	    }

		@Override
		public void updateProfile(Long userId, UpdateUserAccount dto) {
			requireUserId(userId);
			accountsClient.updateProfile(userId, dto);
			
		}

		@Override
		public void changePassword(Long userId, ChangePasswordDto dto) {
			requireUserId(userId);
		    if (dto == null) throw new BadRequestException("PASSWORD_PAYLOAD_REQUIRED");
		    if (isBlank(dto.email())) throw new BadRequestException("EMAIL_REQUIRED");
		    if (isBlank(dto.newPassword())) throw new BadRequestException("PASSWORD_REQUIRED");
		    accountsClient.changePassword(userId, dto);
			
		}

		@Override
		public void deleteUser(Long userId) {
			 requireUserId(userId);
			 accountsClient.deleteUser(userId);
			
		}
	
	    private static void requireUserId(Long userId) {
	        if (userId == null) throw new BadRequestException("USER_ID_REQUIRED");
	    }

	    private static void requireAccountId(Long accountId) {
	        if (accountId == null) throw new BadRequestException("ACCOUNT_ID_REQUIRED");
	        
	    }
	    
	    private static void requireCurrency(Currency currency) {
	        if (currency == null) throw new BadRequestException("CURRENCY_REQUIRED");
	    }


	    private static void requirePositive(BigDecimal value) {
	        if (value == null || value.signum() <= 0) {
	            throw new BadRequestException("AMOUNT_MUST_BE_POSITIVE");
	        }
	    }

	    private static boolean isBlank(String s) {
	        return s == null || s.trim().isEmpty();
	    }

	    private CashOperationViewDto executeCash(OperationType type, CashChangeDto dto, String idempotencyKey) {
	        requireCashChange(dto);
	        String key = isBlank(idempotencyKey) ? UUID.randomUUID().toString() : idempotencyKey;

	        if (type == OperationType.DEPOSIT) {
	            return cashClient.deposit(dto, key);
	        } else if (type == OperationType.WITHDRAW) {
	            return cashClient.withdraw(dto, key);
	        }
	        throw new BadRequestException("UNSUPPORTED_CASH_OPERATION");
	    }

	    private void requireCashChange(CashChangeDto dto) {
	        if (dto == null) throw new BadRequestException("CASH_PAYLOAD_REQUIRED");
	        requireAccountId(dto.accountId());
	        requireCurrency(dto.currency());
	        requirePositive(dto.amount());
	    }

		@Override
		public List<SendNotificationRequest> getRecentNotifications(Long userId, Integer limit) {
			if (userId == null) throw new BadRequestException("USER_ID_REQUIRED");
	        Integer safe = (limit == null || limit <= 0) ? 10 : Math.min(limit, 50);
	        try {
	            return notifications.getRecent(userId, safe);
	        } catch (Exception e) {
	            return List.of();
	        }
	    }
}
