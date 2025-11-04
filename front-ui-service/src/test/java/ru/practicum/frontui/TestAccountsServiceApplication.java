package ru.practicum.frontui;

import org.springframework.boot.SpringApplication;

public class TestAccountsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(FrontUIServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
