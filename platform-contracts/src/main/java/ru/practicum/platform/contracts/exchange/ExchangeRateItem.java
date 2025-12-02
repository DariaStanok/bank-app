package ru.practicum.platform.contracts.exchange;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import ru.practicum.platform.contracts.enums.Currency;

@Builder(toBuilder = true)
public record ExchangeRateItem (    
		Currency from,
        Currency to,
        BigDecimal rate,
        Instant at) {}
