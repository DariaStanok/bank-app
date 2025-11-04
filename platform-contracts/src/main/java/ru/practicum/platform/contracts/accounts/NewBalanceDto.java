package ru.practicum.platform.contracts.accounts;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record NewBalanceDto(
		@NotNull
        BigDecimal newBalance,
        @NotNull
        Instant at
) {}