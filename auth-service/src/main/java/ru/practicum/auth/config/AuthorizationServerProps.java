package ru.practicum.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@Configuration
@ConfigurationProperties(prefix = "authorization-server")
public class AuthorizationServerProps {
    private String issuer;
    
    public String getIssuer() { 
    	return issuer; 
    }
    
    public void setIssuer(String issuer) { 
    	this.issuer = issuer; 
    }
    
    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(this.issuer)
                .build();
    }
}
