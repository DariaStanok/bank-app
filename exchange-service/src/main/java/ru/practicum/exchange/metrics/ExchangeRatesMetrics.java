package ru.practicum.exchange.metrics;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import ru.practicum.observability.metrics.MetricFormat;
import ru.practicum.observability.metrics.MetricName;

@Component
@RequiredArgsConstructor
public class ExchangeRatesMetrics {

    private final MeterRegistry registry;
    private final Clock clock;
    private volatile Instant lastUpdateAt;

    @PostConstruct
    void init() {
        Gauge.builder(
                MetricFormat.metric(MetricName.EXCHANGE_LAST_UPDATE_SECONDS_AGO),
                this,
                ExchangeRatesMetrics::secondsSinceLastUpdate
        ).register(registry);
    }

    public void markRatesUpdated(Instant at) {
        if (at == null) {
            return;
        }
        lastUpdateAt = at;
    }

    private double secondsSinceLastUpdate() {
        Instant last = lastUpdateAt;
        if (last == null) {
            return 0d;
        }
        Instant now = clock.instant();
        long seconds = Duration.between(last, now).getSeconds();
        return Math.max(0, seconds);
    }
}
