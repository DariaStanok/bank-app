package ru.practicum.frontui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import ru.practicum.frontui.integration.AccountsClient;
import ru.practicum.frontui.integration.AuthClient;
import ru.practicum.frontui.integration.CashClient;
import ru.practicum.frontui.integration.NotificationsQueryClient;
import ru.practicum.frontui.integration.TransferClient;



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
	   CashClient cashClient (RestClient restClient) {
		   return HttpServiceProxyFactory
				   .builderFor(RestClientAdapter.create(restClient))
				   .build()
				   .createClient(CashClient.class);
	   }
	   
	   @Bean
	   TransferClient transferClient (RestClient restClient) {
		   return HttpServiceProxyFactory
				   .builderFor(RestClientAdapter.create(restClient))
				   .build()
				   .createClient(TransferClient.class);
	   }
	   
	   @Bean
	    AuthClient authClient(RestClient restClient) {
	        return HttpServiceProxyFactory
	                .builderFor(RestClientAdapter.create(restClient))
	                .build()
	                .createClient(AuthClient.class);
	    }
	   
	    @Bean
	    NotificationsQueryClient notificationsQueryClient(RestClient restClient) {
	        return HttpServiceProxyFactory
	                .builderFor(RestClientAdapter.create(restClient))
	                .build()
	                .createClient(NotificationsQueryClient.class);
	    }
	   
}
