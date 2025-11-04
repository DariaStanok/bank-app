package ru.practicum.frontui.integration;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.platform.contracts.transfer.TransferDto;
import ru.practicum.platform.contracts.transfer.TransferViewDto;

@HttpExchange("/api/v1/transfers")
public interface TransferClient {

    @PostExchange
    TransferViewDto transfer(@RequestBody TransferDto request);
    
    @GetExchange("/{id}")
    TransferViewDto get(@PathVariable Long id);

    @GetExchange("/operation/{operationId}")
    TransferViewDto getByOperation(@PathVariable String operationId);
}
