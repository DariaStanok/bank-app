package ru.practicum.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
	
	  @Bean
	  @Order(2)
	  SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
	    http.csrf(csrf -> csrf.disable());
	    http.authorizeHttpRequests(reg -> reg
	        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
	        .requestMatchers("/auth/register", "/auth/login", "/auth/logout").permitAll()
	        .anyRequest().authenticated()
	    );
	    http.httpBasic(Customizer.withDefaults()); 
	    return http.build();
	  }
}
