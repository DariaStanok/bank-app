package ru.practicum.frontui.integration;

import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import ru.practicum.frontui.dto.RegisterRequestDto;

@HttpExchange
public interface AuthClient {

	@PostExchange("/auth/register")
	void register(RegisterRequestDto dto);

	@PostExchange("/auth/delete?userId={userId}")
	void deleteUser(Long userId);
}
