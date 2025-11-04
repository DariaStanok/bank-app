package ru.practicum.frontui.integration;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

@HttpExchange
public interface NotificationsQueryClient {

    @GetExchange("/api/v1/notifications")
    List<SendNotificationRequest> getRecent(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "limit", required = false) Integer limit
    );
}
