package ru.practicum.observability.metrics;

import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetricFormat {

    public static String metric(MetricName name) {
        return "bank_" + name.name().toLowerCase(Locale.ROOT);
    }

    public static String tag(MetricTags tag) {
        return tag.name().toLowerCase(Locale.ROOT);
    }

    public static String value(Enum<?> e) {
        if (e == null) {
            return "unknown";
        }
        return e.name().toLowerCase(Locale.ROOT);
    }

    public static String maskLast4(Object id) {
        if (id == null) {
            return "unknown";
        }
        String s = String.valueOf(id).trim();
        if (s.isEmpty()) {
            return "unknown";
        }
        if (s.length() <= 4) {
            return s;
        }
        return "****" + s.substring(s.length() - 4);
    }
}
