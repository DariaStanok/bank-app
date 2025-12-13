package ru.practicum.notifications.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import ru.practicum.observability.metrics.MetricFormat;
import ru.practicum.observability.metrics.MetricName;
import ru.practicum.observability.metrics.MetricTags;
import ru.practicum.platform.contracts.enums.NotificationEvent;

@Component
@RequiredArgsConstructor
public class NotificationsMetrics {

    private final MeterRegistry registry;

    public void sendFailed(NotificationEvent event) {
        Counter.builder(MetricFormat.metric(MetricName.NOTIFICATIONS_SEND_FAILED_TOTAL))
                .tag(MetricFormat.tag(MetricTags.EVENT), MetricFormat.value(event))
                .register(registry)
                .increment();
    }
}
