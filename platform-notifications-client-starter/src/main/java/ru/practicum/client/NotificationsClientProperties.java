package ru.practicum.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications.client")
public class NotificationsClientProperties {

	private String path = "/api/v1/notifications";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
