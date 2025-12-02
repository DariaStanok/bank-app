package ru.practicum.exchange.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.service.ExchangeService;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.exchange.ExchangeGetRateResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchange")
public class ExchangeController {

	private final ExchangeService service;

    @GetMapping("/rate")
    public ExchangeGetRateResponse getRate(@RequestParam("base") Currency base,
                                           @RequestParam("quote") Currency quote) {
        return service.getRate(base, quote);
    }
    
}
