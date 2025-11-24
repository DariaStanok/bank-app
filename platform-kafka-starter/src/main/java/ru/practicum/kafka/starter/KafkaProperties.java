package ru.practicum.kafka.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaProperties {
	
    private String bootstrapServers;
    private String notificationsTopic;
    private String exchangeRatesTopic;

}
