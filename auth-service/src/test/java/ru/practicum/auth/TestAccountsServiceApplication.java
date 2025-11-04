package ru.practicum.auth;

import org.springframework.boot.SpringApplication;

public class TestAccountsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(AuthServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
