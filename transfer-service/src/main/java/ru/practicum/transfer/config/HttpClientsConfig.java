package ru.practicum.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import ru.practicum.httpclient.config.ClientsRegistry;
import ru.practicum.transfer.integration.AccountsClient;
import ru.practicum.transfer.integration.BlockerClient;
import ru.practicum.transfer.integration.ExchangeClient;

@Configuration
@RequiredArgsConstructor
public class HttpClientsConfig {
	
	private final ClientsRegistry clientsRegistry;
	
	@Bean
	AccountsClient accountsClient() {
		return clientsRegistry.httpService("accounts", AccountsClient.class);
	}

	@Bean
	BlockerClient blockerClient() {
        return clientsRegistry.httpService("blocker", BlockerClient.class);
    }
	
	@Bean
	 ExchangeClient exchange () {
		 return clientsRegistry.httpService("exchange generation service", ExchangeClient.class);
	 }
}
