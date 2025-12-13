package ru.practicum.blocker.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import ru.practicum.observability.metrics.MetricFormat;
import ru.practicum.observability.metrics.MetricName;

@Component
public class BlockerMetrics {

    private final Counter blockedTotal;

    public BlockerMetrics(MeterRegistry registry) {
       blockedTotal = Counter.builder(MetricFormat.metric(MetricName.BLOCKER_BLOCKED_TOTAL))
                .register(registry);
    }

    public void blocked() {
        blockedTotal.increment();
    }
}
