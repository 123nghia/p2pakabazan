package com.akabazan.framework.data.spi;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.UUID;

/**
 * Minimal UUID v7 generator (time-ordered) based on unix epoch millis and random tail.
 * Note: This is a lightweight implementation intended for general IDs, not cryptographic use.
 */
public class UuidV7Generator implements IdGenerator {

    private final ClockProvider clockProvider;
    private final RandomGenerator random;

    public UuidV7Generator(ClockProvider clockProvider) {
        this.clockProvider = clockProvider;
        // Use ThreadLocalRandom as default; could be replaced with SecureRandom if required
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public String generate() {
        long epochMilli = Instant.now(clockProvider.getClock()).toEpochMilli();
        long msb = 0L;
        // 48-bit timestamp (ms since epoch) in the most significant bits
        msb |= (epochMilli & 0x0000FFFFFFFFFFFFL) << 16;
        // Set version 7 (bits 12..15 of the low 16 bits)
        msb |= 0x0000000000007000L;
        // Fill remaining 12 random bits in the low 16 bits
        msb |= (random.nextInt() & 0x0FFF);

        long lsb = random.nextLong();
        // Set the variant (10xx...)
        lsb &= 0x3FFFFFFFFFFFFFFFL;
        lsb |= 0x8000000000000000L;

        return new UUID(msb, lsb).toString();
    }
}

