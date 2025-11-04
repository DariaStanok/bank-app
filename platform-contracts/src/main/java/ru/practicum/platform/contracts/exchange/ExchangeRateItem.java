package ru.practicum.platform.contracts.exchange;

import java.math.BigDecimal;
import java.time.Instant;

import ru.practicum.platform.contracts.enums.Currency;

public record ExchangeRateItem (    
		Currency from,
        Currency to,
        BigDecimal rate,
        Instant at) {}
