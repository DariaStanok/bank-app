package ru.practicum.exchange.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.exchange.config.ExchangeSettings;
import ru.practicum.exchange.metrics.ExchangeRatesMetrics;
import ru.practicum.platform.contracts.enums.Currency;
import ru.practicum.platform.contracts.exchange.ExchangeGetRateResponse;
import ru.practicum.platform.contracts.exchange.ExchangeRateItem;
import ru.practicum.web.exception.BadRequestException;
import ru.practicum.web.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeSettings settings;
    private final ConcurrentHashMap<Currency, ExchangeRateItem> rates = new ConcurrentHashMap<>();
    private final ExchangeRatesMetrics metrics;
    private volatile Instant lastBatchAt;
  

    @Override
    public ExchangeGetRateResponse getRate(Currency base, Currency quote) {
        if (base == null || quote == null) throw new BadRequestException("VALIDATION_ERROR");
        if (!settings.supported().contains(base) || !settings.supported().contains(quote)) {
            throw new BadRequestException("VALIDATION_ERROR");
        }
        ensureRub();
        if (base == quote) return rateSameCurrency();
        if (quote == Currency.RUB) return rateToRub(base);
        if (base == Currency.RUB)  return rateRubTo(quote);
        return rateCross(base, quote);
    }
    
    @Override
    public void upsertRates(List<ExchangeRateItem> batch) {
        if (batch == null || batch.isEmpty()) throw new BadRequestException("VALIDATION_ERROR");
        ensureRub();

        validateBatch(batch);
        applyBatch(batch);
        
        Instant maxAt = maxAt(batch);
        if (maxAt != null) {
            lastBatchAt = maxAt;    
            metrics.markRatesUpdated(maxAt); 
        }
    }

    private Instant maxAt(List<ExchangeRateItem> batch) {
        Instant max = null;
        for (ExchangeRateItem it : batch) {
            if (it == null || it.at() == null) {
                continue;
            }
            if (max == null || it.at().isAfter(max)) {
                max = it.at();
            }
        }
        return max;
    }
    
    private void ensureRub() {
        rates.computeIfAbsent(
                Currency.RUB,
                c -> new ExchangeRateItem(
                        Currency.RUB,
                        Currency.RUB,
                        BigDecimal.ONE.setScale(settings.scale(), settings.roundingMode()),
                        Instant.now()
                )
        );
    }

    private ExchangeGetRateResponse rateSameCurrency() {
        BigDecimal one = normalize(BigDecimal.ONE);
        return new ExchangeGetRateResponse(one, Instant.now());
    }

    private ExchangeGetRateResponse rateToRub(Currency base) {
    	ExchangeRateItem e = rates.get(base);
        if (e == null || e.rate() == null || e.rate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotFoundException("RATE_NOT_AVAILABLE");
        }
        return new ExchangeGetRateResponse(normalize(e.rate()), e.at());
    }

    private ExchangeGetRateResponse rateRubTo(Currency quote) {
    	ExchangeRateItem q = rates.get(quote);
        if (q == null || q.rate() == null || q.rate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotFoundException("RATE_NOT_AVAILABLE");
        }
        BigDecimal rate = BigDecimal.ONE.divide(q.rate(), settings.scale(), settings.roundingMode());
        return new ExchangeGetRateResponse(normalize(rate), q.at());
    }

    private ExchangeGetRateResponse rateCross(Currency base, Currency quote) {
    	  ExchangeRateItem b = rates.get(base);
          ExchangeRateItem q = rates.get(quote);
          if (b == null || q == null || b.rate() == null || q.rate() == null
                  || b.rate().signum() <= 0 || q.rate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotFoundException("RATE_NOT_AVAILABLE");
        }
        BigDecimal rubToQuote = BigDecimal.ONE.divide(q.rate(), settings.scale(), settings.roundingMode());
        BigDecimal rate = normalize(b.rate().multiply(rubToQuote));
        Instant at = b.at().isBefore(q.at()) ? b.at() : q.at();
        return new ExchangeGetRateResponse(rate, at);
    }
    
    private void applyBatch(List<ExchangeRateItem> batch) {
        for (ExchangeRateItem it : batch) {
            Currency from = it.from();
            Currency to   = it.to();
            boolean fromRub = (from == Currency.RUB);
            Currency cur = fromRub ? to : from;
            BigDecimal rateToRub = fromRub ? safeDivide(BigDecimal.ONE, it.rate()): it.rate();    
            
            BigDecimal normalized = normalize(rateToRub);
            Instant at = it.at();                  

            rates.compute(cur, (k, old) -> {
                if (old == null) return new ExchangeRateItem(cur, Currency.RUB, normalized, at);
                if (!at.isAfter(old.at())) {
                    return old;
                }  
                return new ExchangeRateItem(cur, Currency.RUB, normalized, at);
            });
        }
    }

	private void validateBatch(List<ExchangeRateItem> batch) {
        Set<Currency> seen = new HashSet<>();
        for (ExchangeRateItem it : batch) {
            if (it == null || it.rate() == null || it.at() == null || it.rate().compareTo(BigDecimal.ZERO) <= 0)
                throw new BadRequestException("VALIDATION_ERROR");

            Currency from = it.from();
            Currency to   = it.to();
            if (from == null || to == null) throw new BadRequestException("VALIDATION_ERROR");

            boolean fromRub = (from == Currency.RUB);
            boolean toRub = (to   == Currency.RUB);
            if ((fromRub && toRub) || (!fromRub && !toRub)) {
                throw new BadRequestException("VALIDATION_ERROR");
            }

            Currency cur = fromRub ? to : from;          
            if (cur == Currency.RUB) throw new BadRequestException("VALIDATION_ERROR");
            if (!settings.supported().contains(cur)) throw new BadRequestException("VALIDATION_ERROR");

            if (!seen.add(cur)) throw new BadRequestException("VALIDATION_ERROR");  
        }
    }

    private BigDecimal normalize(BigDecimal v) {
        return v.setScale(settings.scale(), settings.roundingMode());
    }

    private BigDecimal safeDivide(BigDecimal a, BigDecimal b) {
        if (b == null || b.compareTo(BigDecimal.ZERO) == 0) throw new BadRequestException("VALIDATION_ERROR");
        return a.divide(b, settings.scale(), settings.roundingMode());
    }
    
    
}
