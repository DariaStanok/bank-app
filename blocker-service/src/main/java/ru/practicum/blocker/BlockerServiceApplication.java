package ru.practicum.blocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ru.practicum.blocker.config.BlockerSettings;

@SpringBootApplication
@EnableConfigurationProperties(BlockerSettings.class)
public class BlockerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockerServiceApplication.class, args);
	}

}
