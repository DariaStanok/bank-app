package ru.practicum.kafka.starter;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import ru.practicum.platform.contracts.exchange.ExchangeRateItem;
import ru.practicum.platform.contracts.notifications.NotificationMessageDto;

@Component
public class NotificationProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public NotificationProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaProperties kafkaProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void send(NotificationMessageDto message) {
        kafkaTemplate.send(
                kafkaProperties.getNotificationsTopic(),
                message
        );
    }
    
    public void sendExchangeRate(ExchangeRateItem item) {
        kafkaTemplate.send(
        		kafkaProperties.getExchangeRatesTopic(),
        		item
        	);
    }
}
