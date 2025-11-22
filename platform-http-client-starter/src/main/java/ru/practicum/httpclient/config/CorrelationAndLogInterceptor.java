package ru.practicum.httpclient.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CorrelationAndLogInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger("httpclient");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String corr = request.getHeaders().getFirst("X-Request-Id");
        if (corr == null || corr.isBlank()) {
            String fromMdc = MDC.get("X-Request-Id");
            corr = (fromMdc != null && !fromMdc.isBlank()) ? fromMdc : UUID.randomUUID().toString();
            request.getHeaders().set("X-Request-Id", corr);
        }

        long t0 = System.nanoTime();
        ClientHttpResponse resp = execution.execute(request, body);
        long tookMs = (System.nanoTime() - t0) / 1_000_000;
        log.info("{} {} -> {} ({} ms) corr={}",
                request.getMethod(), request.getURI().getPath(), resp.getStatusCode().value(), tookMs, corr);
        return resp;
    }

	
}
