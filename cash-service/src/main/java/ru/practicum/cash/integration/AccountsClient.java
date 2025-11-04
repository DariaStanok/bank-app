package ru.practicum.cash.integration;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import ru.practicum.platform.contracts.accounts.AccountView;
import ru.practicum.platform.contracts.accounts.BalanceChangeDto;
import ru.practicum.platform.contracts.accounts.NewBalanceDto;

@HttpExchange
public interface AccountsClient {

	@GetExchange("/api/v1/accounts/{userId}")
    AccountView getAccount(@PathVariable Long id);

    @PostExchange("/api/v1/accounts/{userId}/balance-change")
    NewBalanceDto changeBalance(@PathVariable Long id, @RequestBody BalanceChangeDto body);

}
