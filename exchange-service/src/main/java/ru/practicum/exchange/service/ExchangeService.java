package ru.practicum.exchange.service;

import java.util.List;

import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.exchange.ExchangeGetRateResponse;
import ru.practicum.platform.contracts.exchange.ExchangeRateItem;

public interface ExchangeService {
	 ExchangeGetRateResponse getRate(Currency base, Currency quote);
	 void upsertRates(List<ExchangeRateItem> batch);
}
