package ru.practicum.frontui;

import org.springframework.boot.SpringApplication;

public class TestFrontUIServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(FrontUIServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
