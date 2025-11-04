package ru.practicum.transfer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;
import ru.practicum.transfer.service.TransferService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {
	
	private final TransferService transferService; 
	
	@PostMapping
	public TransferViewDto create(@RequestBody @Valid TransferDto transferDto) {
		return transferService.create(transferDto);
	}

	@GetMapping("/{id}")
	public TransferViewDto get(@PathVariable Long id) {
		return transferService.getById(id);
	}

	@GetMapping("/operation/{operationId}")
	public TransferViewDto getByOperation(@PathVariable String operationId) {
		return transferService.getByOperationId(operationId);
	}

}
