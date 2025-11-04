package ru.practicum.transfer.service;

import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;

public interface TransferService {
	TransferViewDto create(TransferDto transferDto); 

	TransferViewDto getById(Long id);

	TransferViewDto getByOperationId(String operationId);
}
