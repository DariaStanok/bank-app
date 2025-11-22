package ru.practicum.cash;

import org.springframework.boot.SpringApplication;

public class TestCashServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(CashServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
