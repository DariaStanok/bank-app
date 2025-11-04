package ru.practicum.frontui.integration;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;

@HttpExchange
public interface CashClient {

	@PostExchange("/deposit")
    CashOperationViewDto deposit(
        @RequestBody CashChangeDto request,
        @RequestHeader String idempotencyKey
    );

    @PostExchange("/withdraw")
    CashOperationViewDto withdraw(
        @RequestBody CashChangeDto request,
        @RequestHeader String idempotencyKey
    );
}
