package ru.practicum.platform.contracts.transfer;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.enums.TransferStatus;

public record TransferViewDto(@NotNull Long id, @NotBlank String operationId, @NotNull Long fromAccountId,
		@NotNull Long toAccountId, @NotNull BigDecimal amount, @NotNull Currency currency,
		@NotNull BigDecimal debitAmount, @NotNull BigDecimal creditAmount, @NotNull BigDecimal rate,
		@NotNull TransferStatus status) {
}
