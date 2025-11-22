package ru.practicum.client;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import ru.practicum.httpclient.config.ClientsRegistry;

@AutoConfiguration
@EnableConfigurationProperties(NotificationsClientProperties.class)
@RequiredArgsConstructor

public class NotificationsClientAutoConfiguration {
	
	 private final ClientsRegistry clientsRegistry;

	 @Bean
	    @ConditionalOnMissingBean(NotificationsClient.class)
	    NotificationsClient notificationsClient(NotificationsClientProperties props) {
	        RestClient rest = clientsRegistry.get("notifications");
	        return new RestNotificationsClient(rest, props.getPath());
	    }
}


