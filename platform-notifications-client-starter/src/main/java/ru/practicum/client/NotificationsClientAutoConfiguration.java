package ru.practicum.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@AutoConfiguration
public class NotificationsClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(NotificationsClient.class)
    NotificationsClient notificationsClient(
            RestClient restClient,                      
            NotificationsClientProperties props
    ) {
        return new RestNotificationsClient(restClient, props.getPath());
    }
}


