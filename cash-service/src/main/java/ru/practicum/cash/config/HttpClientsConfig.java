package ru.practicum.cash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import ru.practicum.cash.integration.AccountsClient;
import ru.practicum.cash.integration.BlockerClient;
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
    BlockerClient blockerClient() {
        return clientsRegistry.httpService("blocker", BlockerClient.class);
    }
}
