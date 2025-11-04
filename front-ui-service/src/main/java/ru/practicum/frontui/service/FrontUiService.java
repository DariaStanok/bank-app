package ru.practicum.frontui.service;

import java.math.BigDecimal;
import java.util.List;

import ru.practicum.frontui.dto.RegisterRequestDto;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;
import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;

public interface FrontUiService {

	void register(RegisterRequestDto dto);

	UserViewDto getUserSnapshot(Long userId);

	List<AccountView> getAccounts(Long userId);
	
	AccountView getAccount(Long userId, Long accountId);

	AccountView createAccount(Long userId, UpdateExternalAccount request);

	AccountView updateAccount(Long userId, Long accountId, UpdateExternalAccount request);

	Boolean deleteAccount(Long userId, Long accountId);

	CashOperationViewDto deposit(CashChangeDto dto, String idempotencyKey);

	CashOperationViewDto withdraw(CashChangeDto dto, String idempotencyKey);

	TransferViewDto transfer(TransferDto dto);

	TransferViewDto transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String operationId);
	
	List<SendNotificationRequest> getRecentNotifications(Long userId, Integer limit);
	
	void updateProfile(Long userId, UpdateUserAccount dto);

	void changePassword(Long userId, ChangePasswordDto dto);

	void deleteUser(Long userId);

}
