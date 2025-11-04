package ru.practicum.platform.contracts.notifications;

import java.time.Instant;

import lombok.Builder;
import ru.practicum.platform.contracts.enums.NotificationEvent;

@Builder(toBuilder = true)
public record SendNotificationRequest(
        NotificationEvent event,
        String operationId,
        Long userId, 
        String message,
        Instant at) {}