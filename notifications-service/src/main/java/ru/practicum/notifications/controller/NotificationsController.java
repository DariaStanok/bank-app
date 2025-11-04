package ru.practicum.notifications.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.practicum.notifications.service.NotificationsService;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications") 
public class NotificationsController {

	private final NotificationsService service;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void send(@RequestBody SendNotificationRequest notificationRequest) { 
        service.send(notificationRequest);
    }

  
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SendNotificationRequest> list(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return service.getRecent(userId, limit);
    }
 }
