package ru.practicum.exchange.gen.integration;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.platform.contracts.exchange.ExchangeRateItem;

@HttpExchange
public interface ExchangeClient {
    @PostExchange("/api/v1/exchange/rates")
    void upsertRates(@RequestBody List<ExchangeRateItem> body);
}