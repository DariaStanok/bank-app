package ru.practicum.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ru.practicum.exchange.config.ExchangeSettings;

@SpringBootApplication
@EnableConfigurationProperties (ExchangeSettings.class)
public class ExchangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeServiceApplication.class, args);
	}

}
