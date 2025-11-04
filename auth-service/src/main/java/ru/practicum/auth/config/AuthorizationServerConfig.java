package ru.practicum.auth.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {
	
	@Autowired PasswordEncoder passwordEncoder;
	
	@Bean
	@Order(1)
	  SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
	    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
	    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
	         .oidc(Customizer.withDefaults()); 
	    http.cors(Customizer.withDefaults());
	    return http.build();
	  }
	
	  @Bean
	   RegisteredClientRepository registeredClientRepository() {
	    RegisteredClient gatewayClient = RegisteredClient.withId(UUID.randomUUID().toString())
	        .clientId("gateway-client")
	        .clientSecret(passwordEncoder.encode("gateway-secret")) 
	        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
	        .scope("internal")
	        .build();
	   
	  
	  RegisteredClient frontUiClient = RegisteredClient.withId(UUID.randomUUID().toString())
			    .clientId("front-ui-client")
			    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)    
			    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			    .redirectUri("http://localhost:8080/ui/login/oauth2/code/front-ui")
			    .postLogoutRedirectUri("http://localhost:8080/ui/login")
			    .redirectUri("http://localhost:8088/login/oauth2/code/front-ui")
			    .postLogoutRedirectUri("http://localhost:8088/login")
			    .scope(OidcScopes.OPENID)
			    .scope(OidcScopes.PROFILE)
			    .clientSettings(ClientSettings.builder()
			        .requireProofKey(true)               
			        .requireAuthorizationConsent(false)   
			        .build())
			    .tokenSettings(TokenSettings.builder()
			        .accessTokenTimeToLive(Duration.ofMinutes(10))
			        .build())
			    .build();
	  return new InMemoryRegisteredClientRepository(gatewayClient, frontUiClient);
	  }

	  @Bean
	   JWKSource<SecurityContext> jwkSource() {
	    RSAKey rsa = generateRsa();
	    JWKSet set = new JWKSet(rsa);
	    return (selector, ctx) -> selector.select(set);
	  }

	  @Bean
	   JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
	    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	  }

	  private static RSAKey generateRsa() {
	    try {
	      KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
	      g.initialize(2048);
	      KeyPair kp = g.generateKeyPair();
	      RSAPublicKey pub = (RSAPublicKey) kp.getPublic();
	      RSAPrivateKey priv = (RSAPrivateKey) kp.getPrivate();
	      return new RSAKey.Builder(pub).privateKey(priv).keyID(UUID.randomUUID().toString()).build();
	    } catch (Exception e) {
	      throw new IllegalStateException(e);
	    }
	  }
}
