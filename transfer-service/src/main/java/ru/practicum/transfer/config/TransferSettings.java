package ru.practicum.transfer.config;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "transfer")
public  record TransferSettings ( 
		@NotNull Boolean blockerEnabled,
		@NotNull Boolean exchangeRequired) {}