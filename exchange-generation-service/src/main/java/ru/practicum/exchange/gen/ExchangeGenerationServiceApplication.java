package ru.practicum.exchange.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import ru.practicum.exchange.gen.config.GeneratorSettings;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(GeneratorSettings.class)
public class ExchangeGenerationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeGenerationServiceApplication.class, args);
	}

}
