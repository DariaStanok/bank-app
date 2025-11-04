package ru.practicum.transfer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import ru.practicum.transfer.integration.AccountsClient;
import ru.practicum.transfer.integration.BlockerClient;
import ru.practicum.transfer.integration.ExchangeClient;

@Configuration
public class HttpClientsConfig {

	@Bean
	AccountsClient accountsClient(RestClient restClient) {
		return HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(restClient))
				.build()
				.createClient(AccountsClient.class);
	}

	@Bean
	@ConditionalOnProperty(prefix = "features", name = "blocker-enabled", havingValue = "true")
	BlockerClient blockerClient(RestClient restClient) {
		return HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(restClient))
				.build()
				.createClient(BlockerClient.class);
	}

	@Bean
	@ConditionalOnProperty(prefix = "features", name = "exchange-enabled", havingValue = "true")
	ExchangeClient exchangeClient(RestClient restClient) {
		return HttpServiceProxyFactory
				.builderFor(RestClientAdapter
						.create(restClient)).build()
				.createClient(ExchangeClient.class);
	}
}
