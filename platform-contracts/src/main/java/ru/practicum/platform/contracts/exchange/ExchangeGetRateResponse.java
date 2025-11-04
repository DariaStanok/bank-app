package ru.practicum.platform.contracts.exchange;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeGetRateResponse (
		BigDecimal rate,
        Instant at){}
