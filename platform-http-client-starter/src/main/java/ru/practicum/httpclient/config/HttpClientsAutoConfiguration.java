package ru.practicum.httpclient.config;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import ru.practicum.httpclient.props.ClientsProperties;

@AutoConfiguration
@EnableConfigurationProperties(ClientsProperties.class)
public class HttpClientsAutoConfiguration {

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    ClientHttpRequestInterceptor correlationAndLogInterceptor() {
        return new CorrelationAndLogInterceptor();
    }

    @Bean
    ClientsRegistry clientsRegistry(RestClient.Builder baseBuilder,
                                           ClientsProperties props,
                                           List<ClientHttpRequestInterceptor> interceptors) {
        return new ClientsRegistry(baseBuilder, props, interceptors);
    }
}
