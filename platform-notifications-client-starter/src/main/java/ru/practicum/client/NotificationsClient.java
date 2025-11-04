package ru.practicum.client;

import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

public interface NotificationsClient {
	
    public void send(SendNotificationRequest notificationRequest);    
}
