package ru.practicum.platform.contracts.notifications;

import java.time.Instant;

import lombok.Builder;
import ru.practicum.platform.contracts.enums.NotificationEvent;

@Builder(toBuilder = true)
public record NotificationMessageDto( 
	
    NotificationEvent event,  
    Long userId,
    Long accountId,
    String operationId,      
    String message,         
    Instant at

) {}
