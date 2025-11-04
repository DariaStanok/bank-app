package ru.practicum.platform.contracts.transfer;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.platform.contracts.enums.Currency;

@Builder(toBuilder = true)
public record TransferDto(@NotNull BigDecimal amount,

		@NotNull Currency currency,

		@NotNull Long fromAccountId,

		@NotNull Long toAccountId,

		@NotBlank String operationId) {}
