package ru.practicum.exchange.config;

import java.math.RoundingMode;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.platform.contracts.enums.Currency;

@Validated
@ConfigurationProperties(prefix = "exchange")
public record ExchangeSettings(
		@Min(0) @Max(12) 
		int scale,                 
        @NotNull 
        RoundingMode roundingMode,         
        @NotEmpty 
        Set<Currency> supported           
) {}

