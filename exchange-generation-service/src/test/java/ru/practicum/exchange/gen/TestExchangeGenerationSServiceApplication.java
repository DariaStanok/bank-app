package ru.practicum.exchange.gen;

import org.springframework.boot.SpringApplication;

public class TestExchangeGenerationSServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ExchangeGenerationServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
