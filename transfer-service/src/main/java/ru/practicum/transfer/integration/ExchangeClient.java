package ru.practicum.transfer.integration;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import ru.practicum.platform.contracts.enums.Currency;

@HttpExchange
public interface ExchangeClient {

	@GetExchange("/api/v1/exchange/rate")
	BigDecimal getRate(@RequestParam Currency from, @RequestParam Currency to);

}
