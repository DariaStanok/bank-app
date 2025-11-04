package ru.practicum.exchange.gen.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.practicum.platform.contracts.enums.Currency;

@Validated
@ConfigurationProperties(prefix = "exgen")
public record GeneratorSettings(
        @NotEmpty Set<Currency> supported,                
        @Min(0) @Max(12) int scale,                       
        @NotNull RoundingMode roundingMode,               
        long fixedRateMs,                                 
        @NotNull BigDecimal driftPct,                     
        @NotNull String seedSecretBase64,                                      
        @NotNull String exchangeServiceId,                
        Map<Currency, BigDecimal> initialToRub,           
        Map<Currency, BigDecimal> minToRub,               
        Map<Currency, BigDecimal> maxToRub                
) {}