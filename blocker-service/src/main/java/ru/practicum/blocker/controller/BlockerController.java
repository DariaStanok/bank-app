package ru.practicum.blocker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.blocker.service.BlockerService;
import ru.practicum.platform.contracts.blocker.BlockerCheckRequest;
import ru.practicum.platform.contracts.blocker.BlockerCheckResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blocker")
public class BlockerController {

    private final BlockerService blockerService;

    @PostMapping("/checks")
    public BlockerCheckResponse check(@RequestBody BlockerCheckRequest body) {
        return blockerService.check(body);
    }
}