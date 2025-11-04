package ru.practicum.client;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import ru.practicum.platform.contracts.notifications.SendNotificationRequest;

public class RestNotificationsClient implements NotificationsClient {
	
	 private final RestClient rest;
	 private final String path;

	    RestNotificationsClient(RestClient rest, String path) {
	        this.rest = rest;
	        this.path = path;
	    }

	    @Override
	    public void send(SendNotificationRequest req) {
	        rest.post()
	           .uri(path)
	           .contentType(MediaType.APPLICATION_JSON)
	           .body(req)
	           .retrieve()
	           .toBodilessEntity();
	    }
}
