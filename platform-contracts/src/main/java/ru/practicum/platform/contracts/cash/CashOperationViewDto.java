package ru.practicum.platform.contracts.cash;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.practicum.platform.contracts.enums.CashOpStatus;
import ru.practicum.platform.contracts.enums.Currency;

@Builder(toBuilder = true)
public record CashOperationViewDto (
	@NotBlank	
	String operationId,
	@NotNull
	Long accountId,
	@NotNull
	Currency currency,
	@NotNull @Positive
	BigDecimal amount,
	@NotNull
	BigDecimal newBalance,
	@NotNull
	CashOpStatus status,
	@NotNull
	Instant at){}
