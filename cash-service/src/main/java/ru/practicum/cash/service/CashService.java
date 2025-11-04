package ru.practicum.cash.service;

import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;

public interface CashService {
	CashOperationViewDto deposit(CashChangeDto dto, String idempotencyKey);

	CashOperationViewDto withdraw(CashChangeDto dto, String idempotencyKey);
}