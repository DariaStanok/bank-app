package ru.practicum.security;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "platform.security")
public class PlatformSecurityProperties {

	private List<String> permitAll = List.of("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**");
	private List<String> apiPatterns = List.of("/api/v1/**");
	private String requiredAuthority = "SCOPE_internal";

}
