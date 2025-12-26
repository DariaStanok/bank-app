package ru.practicum.frontui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import ru.practicum.frontui.metrics.OAuth2LoginAuthHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final OAuth2LoginAuthHandler authHandler;
	
	 @Bean
	 SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                    "/css/**", "/js/**", "/images/**", "/webjars/**",
	                    "/ui/login", "/ui/register", "/error",
	                    "/actuator/health/**", "/actuator/prometheus"
	                ).permitAll()
	                .anyRequest().authenticated()
	            )
	            .oauth2Login(oauth -> oauth
	                .loginPage("/ui/login")
	                .successHandler(authHandler)
	                .failureHandler(authHandler)
	            )
	            .logout(logout -> logout
                    .logoutUrl("/ui/logout")
                    .logoutSuccessUrl("/auth/logout")
                )
	            .csrf(Customizer.withDefaults());

	        return http.build();
	    }
	}
	