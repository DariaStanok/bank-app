package ru.practicum.platform.contracts.exchange;

import ru.practicum.platform.contracts.enums.Currency;

public record ExchangeGetRateRequest (
		Currency from,
        Currency to){}
