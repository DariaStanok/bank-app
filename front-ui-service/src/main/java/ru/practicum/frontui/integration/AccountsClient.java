package ru.practicum.frontui.integration;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.ChangePasswordDto;
import ru.practicum.platform.contracts.accounts.UpdateExternalAccount;
import ru.practicum.platform.contracts.accounts.UpdateUserAccount;
import ru.practicum.platform.contracts.accounts.UserViewDto;

@HttpExchange
public interface AccountsClient {

	@GetExchange("/api/v1/users/{userId}/accounts/view")
	UserViewDto getUserSnapshot(@PathVariable Long userId);

	@GetExchange("/api/v1/users/{userId}/accounts")
	List<AccountView> getAccounts(@PathVariable Long userId);

	@GetExchange("/api/v1/users/{userId}/accounts/{accountId}")
	AccountView getAccount(@PathVariable Long userId, @PathVariable Long accountId);

	@PostExchange("/api/v1/users/{userId}/accounts")
	AccountView createAccount(@PathVariable Long userId, @RequestBody UpdateExternalAccount request);

	@PutExchange("/api/v1/users/{userId}/accounts/{accountId}")
	AccountView updateAccount(@PathVariable Long userId, @PathVariable Long accountId,
			@RequestBody UpdateExternalAccount request);

	@DeleteExchange("/api/v1/users/{userId}/accounts/{accountId}")
	Boolean deleteAccount(@PathVariable Long userId, @PathVariable Long accountId);

	@PostExchange("/api/v1/users/{userId}/profile")
	void updateProfile(@PathVariable Long userId, @RequestBody UpdateUserAccount dto);

	@PostExchange("/api/v1/users/{userId}/password")
	void changePassword(@PathVariable Long userId, @RequestBody ChangePasswordDto dto);

	@DeleteExchange("/api/v1/users/{userId}")
	    void deleteUser(@PathVariable Long userId);

}
