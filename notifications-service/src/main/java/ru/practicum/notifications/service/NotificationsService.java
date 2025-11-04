package ru.practicum.notifications.service;

import java.util.List;

import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

public interface NotificationsService {
	  void send(SendNotificationRequest notificationRequest);
	  List<SendNotificationRequest> getRecent(Long userId, int limit);
}  
