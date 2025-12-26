package ru.practicum.httpclient.props;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import lombok.Data;

@Data
public class ClientProps {
    private URI baseUrl;
    private Duration connectTimeout;
    private Duration readTimeout;
    private Map<String, String> defaultHeaders;
}