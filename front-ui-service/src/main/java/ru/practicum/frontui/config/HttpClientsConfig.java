package ru.practicum.frontui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.integration.AccountsClient;
import ru.practicum.frontui.integration.AuthClient;
import ru.practicum.frontui.integration.CashClient;
import ru.practicum.frontui.integration.NotificationsQueryClient;
import ru.practicum.frontui.integration.TransferClient;
import ru.practicum.httpclient.config.ClientsRegistry;

@Configuration
@RequiredArgsConstructor
public class HttpClientsConfig {

	private final ClientsRegistry clientsRegistry;

	@Bean
	AccountsClient accountsClient() {
		return clientsRegistry.httpService("accounts", AccountsClient.class);
	}
	
	@Bean
	AuthClient authClient() {
		return clientsRegistry.httpService("auth", AuthClient.class);
	}

	@Bean
	CashClient cashClient () {
		return clientsRegistry.httpService("cash" , CashClient.class);
	}
	
	@Bean
	NotificationsQueryClient notifications() {
		return clientsRegistry.httpService("notifications", NotificationsQueryClient.class);
	}
	
	@Bean
	TransferClient transferClient () {
		return clientsRegistry.httpService("transfer", TransferClient.class);
	}
	
}
