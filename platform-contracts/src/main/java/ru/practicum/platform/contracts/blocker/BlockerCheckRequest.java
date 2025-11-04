package ru.practicum.platform.contracts.blocker;

import java.math.BigDecimal;

public record BlockerCheckRequest (
		Long fromAccountId, 
		Long toAccountId, 
		BigDecimal amount){}


