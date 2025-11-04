package ru.practicum.platform.contracts.cash;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.practicum.platform.contracts.enums.Currency;

@Builder(toBuilder = true)
public record CashChangeDto (
	@NotNull
	Long accountId,
	@NotNull
	Currency currency,
	@NotNull
	@Positive
	BigDecimal amount
	){}
