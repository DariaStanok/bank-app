package ru.practicum.exchange.kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.service.ExchangeService;
import ru.practicum.platform.contracts.exchange.ExchangeRateItem;

@Component
@RequiredArgsConstructor
public class ExchangeRatesConsumer {

    private final ExchangeService exchangeService;

    @KafkaListener(
            topics = "${app.kafka.exchange-rates-topic}",
            groupId = "${app.kafka.consumer-group-id}"
    )
    public void consume(ExchangeRateItem item) {
        exchangeService.upsertRates(List.of(item));
    }
}