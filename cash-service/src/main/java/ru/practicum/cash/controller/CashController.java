package ru.practicum.cash.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.cash.service.CashService;
import ru.practicum.platform.contracts.cash.CashChangeDto;
import ru.practicum.platform.contracts.cash.CashOperationViewDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cash")
@Validated
public class CashController {

    private final CashService cashService;

    @PostMapping("/deposits")
    public CashOperationViewDto deposit(@RequestBody @Valid CashChangeDto dto,
                                        @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey) {
        return cashService.deposit(dto, idempotencyKey);
    }

    @PostMapping("/withdrawals")
    public CashOperationViewDto withdraw(@RequestBody @Valid CashChangeDto dto,
                                         @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey) {
        return cashService.withdraw(dto, idempotencyKey);
    }

}
