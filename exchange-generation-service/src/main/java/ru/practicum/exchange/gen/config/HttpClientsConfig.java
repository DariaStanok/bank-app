package ru.practicum.exchange.gen.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import ru.practicum.exchange.gen.integration.ExchangeClient;

@Configuration
public class HttpClientsConfig {

	@Bean
	@ConditionalOnProperty(prefix = "features", name = "exchange-enabled", havingValue = "true")
	ExchangeClient exchangeClient(RestClient restClient) {
		return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
				.createClient(ExchangeClient.class);
	}
}
