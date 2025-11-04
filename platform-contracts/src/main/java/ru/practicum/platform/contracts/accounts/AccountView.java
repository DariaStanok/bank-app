package ru.practicum.platform.contracts.accounts;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.platform.contracts.enums.Currency;

public record AccountView(
		@NotNull
        Long id,
        @NotBlank
        String externalAccountId,
        @NotNull
        Currency currency,
        @NotNull
        BigDecimal balance
) {}
