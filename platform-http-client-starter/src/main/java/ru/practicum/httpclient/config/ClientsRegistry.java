package ru.practicum.httpclient.config;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.RequiredArgsConstructor;
import ru.practicum.httpclient.props.ClientProps;
import ru.practicum.httpclient.props.ClientsProperties;

@RequiredArgsConstructor
public class ClientsRegistry {

    private final RestClient.Builder baseBuilder;
    private final ClientsProperties properties;
    private final List<ClientHttpRequestInterceptor> interceptors;

    private final Map<String, RestClient> cache = new ConcurrentHashMap<>();

    public RestClient get(String name) {
        return cache.computeIfAbsent(name, this::build);
    }

    public <T> T httpService(String name, Class<T> httpInterface) {
    	RestClient client = get(name);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build();
        return factory.createClient(httpInterface);
    }

    private RestClient build(String name) {
        ClientProps cp = properties.getMap() != null ? properties.getMap().get(name) : null;
        if (cp == null || cp.getBaseUrl() == null) {
            throw new IllegalStateException("clients." + name + ".base-url is not configured");
        }

        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();

        Duration ct = cp.getConnectTimeout() != null ? cp.getConnectTimeout() : Duration.ofSeconds(2);
        Duration rt = cp.getReadTimeout()    != null ? cp.getReadTimeout()    : Duration.ofSeconds(5);

        rf.setConnectTimeout((int) ct.toMillis());
        rf.setReadTimeout((int) rt.toMillis());

        RestClient.Builder b = baseBuilder.clone()
                .requestFactory(rf)
                .baseUrl(cp.getBaseUrl().toString());

        if (!CollectionUtils.isEmpty(cp.getDefaultHeaders())) {
            cp.getDefaultHeaders().forEach(b::defaultHeader);
        }

        if (!CollectionUtils.isEmpty(interceptors)) {
            interceptors.forEach(b::requestInterceptor);
        }

        return b.build();
    }
}
