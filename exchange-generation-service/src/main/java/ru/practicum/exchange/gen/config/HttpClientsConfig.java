package ru.practicum.exchange.gen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.gen.integration.ExchangeClient;
import ru.practicum.httpclient.config.ClientsRegistry;

@Configuration
@RequiredArgsConstructor
public class HttpClientsConfig {
	
	 private final ClientsRegistry clientsRegistry;
	 
	 @Bean
	 ExchangeClient exchange () {
		 return clientsRegistry.httpService("exchange generation service", ExchangeClient.class);
	 }
 

}
