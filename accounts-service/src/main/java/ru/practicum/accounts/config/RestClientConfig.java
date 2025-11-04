package ru.practicum.accounts.config;

import java.io.IOException;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
     OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository registrations,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

   @Bean
     RestClient restClient(RestClient.Builder builder, OAuth2AuthorizedClientManager manager) {
        return builder
                .baseUrl("http://api-gateway")
                .requestInterceptor(new ClientHttpRequestInterceptor() {
                    @Override
                    public ClientHttpResponse intercept(
                            org.springframework.http.HttpRequest request,
                            byte[] body,
                            ClientHttpRequestExecution execution
                    ) throws IOException {
                        OAuth2AuthorizeRequest authorizeRequest =
                                OAuth2AuthorizeRequest.withClientRegistrationId("svc")
                                        .principal("system")
                                        .build();

                        OAuth2AuthorizedClient authorizedClient = manager.authorize(authorizeRequest);
                        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                            throw new IllegalStateException("Failed to obtain access token via client_credentials");
                        }

                        request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
                        return execution.execute(request, body);
                    }
                })
                .build();
    }
}
