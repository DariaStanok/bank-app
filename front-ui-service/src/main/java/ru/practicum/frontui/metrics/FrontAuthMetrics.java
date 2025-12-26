package ru.practicum.frontui.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import ru.practicum.observability.metrics.MetricFormat;
import ru.practicum.observability.metrics.MetricName;
import ru.practicum.observability.metrics.MetricTags;

@Component
@RequiredArgsConstructor
public class FrontAuthMetrics {

	private final Counter loginSuccess;
    private final Counter loginFailure;

    public FrontAuthMetrics(MeterRegistry registry) {
        String metric = MetricFormat.metric(MetricName.AUTH_LOGIN_TOTAL);
        String tagKey = MetricFormat.tag(MetricTags.RESULT);

        loginSuccess = Counter.builder(metric)
                .tag(tagKey, MetricFormat.result(true))
                .register(registry);

       loginFailure = Counter.builder(metric)
                .tag(tagKey, MetricFormat.result(false))
                .register(registry);
    }

    public void loginSucceeded() {
        loginSuccess.increment();
    }

    public void loginFailed() {
        loginFailure.increment();
    }
}
