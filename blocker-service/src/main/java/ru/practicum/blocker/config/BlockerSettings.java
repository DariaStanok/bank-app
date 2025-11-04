package ru.practicum.blocker.config;

import java.math.BigDecimal;
import java.util.Base64;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "blocker")
public record BlockerSettings (
	        @NotNull
	        BigDecimal threshold,        
	        @NotNull @Min(0) @Max(100) 
	        Integer denyPercent, 
	        @NotBlank 
	        String secretBase64         
		) {
	
		    public byte[] secretBytes() {
		        return Base64.getDecoder().decode(secretBase64);
    }
}

