package ru.practicum.exchange.gen.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.gen.config.GeneratorSettings;
import ru.practicum.exchange.gen.integration.ExchangeClient;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.exchange.ExchangeRateItem;
import ru.practicum.web.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class GenerationServiceImpl implements GenerationService {

    private final GeneratorSettings settings;
    private final ExchangeClient exchange;

    private final ConcurrentHashMap<Currency, BigDecimal> last = new ConcurrentHashMap<>();
    private final AtomicBoolean inited = new AtomicBoolean(false);
    private volatile SplittableRandom rnd;


    @Scheduled(fixedRateString = "${exgen.fixed-rate-ms}")
    void scheduledTick() {
        doGenerate();
    }

    @Override
    public void generateTick() {
        doGenerate();
    }

    private void doGenerate() {
        initOnce();
        Instant now = Instant.now();
        List<ExchangeRateItem> batch = buildBatch(now);
        if (!batch.isEmpty()) {
            exchange.upsertRates(batch); 
        }
    }

    private List<ExchangeRateItem> buildBatch(Instant now) {
        List<ExchangeRateItem> out = new ArrayList<>();
        for (Currency cur : settings.supported()) {
            if (cur == Currency.RUB) continue;
            BigDecimal next = nextToRub(cur, last.get(cur));
            last.put(cur, next);
            out.add(new ExchangeRateItem(cur, Currency.RUB, next, now)); 
        }
        return out;
    }

    private BigDecimal nextToRub(Currency cur, BigDecimal prev) {
        if (prev == null) {
            BigDecimal init = settings.initialToRub() == null ? null : settings.initialToRub().get(cur);
            if (init == null || init.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("INITIAL_RATE_REQUIRED_" + cur.name());
            }
            return clamp(init, cur);
        }
        BigDecimal factor = randomFactor(settings.driftPct());
        BigDecimal candidate = prev.multiply(factor);
        return clamp(candidate, cur);
    }


    private void initOnce() {
        if (inited.get()) return;
        synchronized (this) {
            if (inited.get()) return;

            if (settings == null) throw new IllegalStateException("SETTINGS_REQUIRED");
            if (settings.supported() == null || settings.supported().isEmpty()) {
                throw new BadRequestException("SUPPORTED_REQUIRED");
            }
            if (!settings.supported().contains(Currency.RUB)) {
                throw new BadRequestException("SUPPORTED_MUST_INCLUDE_RUB");
            }

            rnd = new SplittableRandom(hmacSeed("exgen-seed-v1"));
            last.put(Currency.RUB, BigDecimal.ONE);

            if (settings.initialToRub() != null) {
                settings.initialToRub().forEach((cur, rate) -> {
                    if (cur != Currency.RUB && rate != null && rate.compareTo(BigDecimal.ZERO) > 0) {
                        last.put(cur, rate);
                    }
                });
            }
            inited.set(true);
        }
    }


    private BigDecimal randomFactor(BigDecimal driftPct) {
        if (driftPct == null || driftPct.signum() < 0) {
            throw new BadRequestException("DRIFT_PCT_INVALID");
        }
        double p = driftPct.doubleValue();
        double delta = (rnd.nextDouble() * 2.0 * p) - p; 
        return BigDecimal.valueOf(1.0 + delta);
    }

    private BigDecimal clamp(BigDecimal v, Currency cur) {
        if (settings.minToRub() != null) {
            BigDecimal min = settings.minToRub().get(cur);
            if (min != null && v.compareTo(min) < 0) v = min;
        }
        if (settings.maxToRub() != null) {
            BigDecimal max = settings.maxToRub().get(cur);
            if (max != null && v.compareTo(max) > 0) v = max;
        }
        return v;
    }
    
    private long hmacSeed(String label) {
        try {
            byte[] secret = java.util.Base64.getDecoder().decode(settings.seedSecretBase64());
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] d = mac.doFinal(label.getBytes(UTF_8)); 
            return java.nio.ByteBuffer.wrap(d, 0, 8).getLong();
        } catch (Exception e) {
            throw new IllegalStateException("HMAC_SEED_FAILED", e);
        }
    }
}
