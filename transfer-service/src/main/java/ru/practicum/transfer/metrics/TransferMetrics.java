package ru.practicum.transfer.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import ru.practicum.observability.metrics.MetricFormat;
import ru.practicum.observability.metrics.MetricName;
import ru.practicum.observability.metrics.MetricTags;

@Component
@RequiredArgsConstructor
public class TransferMetrics {

    private final MeterRegistry registry;

    public void failed(Long fromAccountId, Long toAccountId) {
        Counter.builder(MetricFormat.metric(MetricName.TRANSFER_FAILED_TOTAL))
                .tag(MetricFormat.tag(MetricTags.SENDER), MetricFormat.maskLast4(fromAccountId))
                .tag(MetricFormat.tag(MetricTags.RECEIVER), MetricFormat.maskLast4(toAccountId))
                .register(registry)
                .increment();
    }
}
