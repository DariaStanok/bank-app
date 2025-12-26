package ru.practicum.observability;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.instrument.config.MeterFilter;

@AutoConfiguration
@ConditionalOnClass(MeterFilter.class)
@EnableConfigurationProperties(ObservabilityProperties.class)
public class ObservabilityAutoConfiguration {
	@Bean
    MeterFilter cardinalityGuard(ObservabilityProperties props) {
		
        boolean guardEnabled = Boolean.TRUE.equals(props.getCardinalityGuardEnabled());
        if (!guardEnabled) {
            return MeterFilter.accept();
        }
        boolean usernameAllowed = Boolean.TRUE.equals(props.getAllowUsernameTag());
        if (usernameAllowed) {
            return MeterFilter.accept();
        }
        return MeterFilter.deny(id ->
                id.getTags().stream().anyMatch(t -> "username".equalsIgnoreCase(t.getKey()))
        );
    }

}
