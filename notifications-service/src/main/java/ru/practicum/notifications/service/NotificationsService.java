package ru.practicum.notifications.service;

import java.util.List;

import ru.practicum.platform.contracts.notifications.NotificationMessageDto;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

public interface NotificationsService {
	  void handleNotification(NotificationMessageDto msg);
	  List<SendNotificationRequest> getRecent(Long userId, int limit);
}  
