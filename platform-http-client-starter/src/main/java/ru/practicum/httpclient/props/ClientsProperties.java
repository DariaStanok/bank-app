package ru.practicum.httpclient.props;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "clients")
public class ClientsProperties {
    private Map<String, ClientProps> map;
}