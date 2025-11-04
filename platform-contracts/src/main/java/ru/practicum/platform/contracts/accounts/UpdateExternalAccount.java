package ru.practicum.platform.contracts.accounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.platform.contracts.enums.Currency;

public record UpdateExternalAccount(
		@NotBlank
	     String externalAccountId,
	    @NotNull
	     Currency currency
		) {}
