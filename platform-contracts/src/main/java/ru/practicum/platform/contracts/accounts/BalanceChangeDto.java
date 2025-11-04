package ru.practicum.platform.contracts.accounts;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.OperationType;

@Builder(toBuilder = true)
public record BalanceChangeDto(
		@NotNull
        OperationType type,
        @NotNull
        Currency currency,
        @NotNull
    	@Positive
        BigDecimal amount
) {}
