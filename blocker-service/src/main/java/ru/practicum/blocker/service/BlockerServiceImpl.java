package ru.practicum.blocker.service;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.SplittableRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.blocker.config.BlockerSettings;
import ru.practicum.platform.contracts.blocker.BlockerCheckRequest;
import ru.practicum.platform.contracts.blocker.BlockerCheckResponse;
import ru.practicum.web.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class BlockerServiceImpl implements BlockerService {

	private final BlockerSettings settings;

    @Override
    public BlockerCheckResponse check(BlockerCheckRequest req) {
        validate(req);
        if (req.amount().compareTo(settings.threshold()) > 0) {
            return new BlockerCheckResponse(false);
        }
        if (settings.denyPercent() > 0 && hitDeterministicDeny(req)) {
            return new BlockerCheckResponse(false);
        }
        return new BlockerCheckResponse(true);
    }

    private void validate(BlockerCheckRequest req) {
        if (req == null) throw new BadRequestException("VALIDATION_ERROR");
        if (req.fromAccountId() == null || req.toAccountId() == null)
            throw new BadRequestException("VALIDATION_ERROR");
        if (Objects.equals(req.fromAccountId(), req.toAccountId()))
            throw new BadRequestException("VALIDATION_ERROR");
        if (req.amount() == null || req.amount().signum() <= 0)
            throw new BadRequestException("VALIDATION_ERROR");
    }

    private boolean hitDeterministicDeny(BlockerCheckRequest req) {
        var norm = req.amount().setScale(2, RoundingMode.DOWN);

        byte[] data = ByteBuffer.allocate(8 + 8 + 8)
                .putLong(req.fromAccountId())
                .putLong(req.toAccountId())
                .putLong(norm.unscaledValue().longValue())
                .array();

        long seed = firstLongOfHmacSha256(data, settings.secretBytes());
        int bucket = new SplittableRandom(seed).nextInt(100);
        return bucket < settings.denyPercent();
    }

    private long firstLongOfHmacSha256(byte[] data, byte[] secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] d = mac.doFinal(data); 
            return ByteBuffer.wrap(d, 0, 8).getLong(); 
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 unavailable", e);
        }
    }

}
