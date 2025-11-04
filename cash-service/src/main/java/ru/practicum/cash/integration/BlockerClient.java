package ru.practicum.cash.integration;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.platform.contracts.blocker.BlockerCheckRequest;
import ru.practicum.platform.contracts.blocker.BlockerCheckResponse;

@HttpExchange
public interface BlockerClient {

	 @PostExchange("/api/v1/blocker/checks") 
	 BlockerCheckResponse check(@RequestBody BlockerCheckRequest body); 
}
