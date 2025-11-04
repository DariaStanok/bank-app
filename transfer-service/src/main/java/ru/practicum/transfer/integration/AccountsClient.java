package ru.practicum.transfer.integration;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;

@HttpExchange 
public interface AccountsClient {
	
	@GetExchange("/api/v1/users/{userId}/accounts/{id}")
	AccountView getAccount(@PathVariable Long id);

	@PostExchange("/api/v1/users/{userId}/accounts/{id}/balance-change")
	NewBalanceDto changeBalance(@PathVariable Long id, @RequestBody BalanceChangeDto body);

	

}
