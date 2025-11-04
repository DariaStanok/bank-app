package ru.practicum.notifications;

import org.springframework.boot.SpringApplication;

public class TestAccountsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(NotificationsServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
