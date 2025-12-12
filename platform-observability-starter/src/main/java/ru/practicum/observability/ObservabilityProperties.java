package ru.practicum.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "observability.metrics")
public class ObservabilityProperties {
	
    private Boolean cardinalityGuardEnabled;
    private Boolean allowUsernameTag;
}
