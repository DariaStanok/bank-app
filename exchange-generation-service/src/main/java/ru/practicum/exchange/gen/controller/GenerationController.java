package ru.practicum.exchange.gen.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.gen.service.GenerationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exgen")
public class GenerationController {

    private final GenerationService service;

    @PostMapping("/tick")
    public void tick() {
        service.generateTick(); 
    }
}
