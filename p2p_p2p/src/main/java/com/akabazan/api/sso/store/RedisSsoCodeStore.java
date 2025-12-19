package com.akabazan.api.sso.store;

import com.akabazan.api.sso.config.SsoProperties;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisSsoCodeStore {

    private static final int CODE_BYTES = 32;
    private static final String CODE_KEY_PREFIX = "sso:code:";

    private static final DefaultRedisScript<String> GET_AND_DELETE_SCRIPT =
            new DefaultRedisScript<>(
                    "local v = redis.call('GET', KEYS[1]); " +
                    "if v then redis.call('DEL', KEYS[1]); end; " +
                    "return v;",
                    String.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final StringRedisTemplate stringRedisTemplate;
    private final SsoProperties ssoProperties;

    public RedisSsoCodeStore(StringRedisTemplate stringRedisTemplate, SsoProperties ssoProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.ssoProperties = ssoProperties;
    }

    public long getCodeTtlSeconds() {
        return ssoProperties.getCodeTtlSeconds();
    }

    public String issueCode(UUID userId) {
        Duration ttl = Duration.ofSeconds(Math.max(0, ssoProperties.getCodeTtlSeconds()));
        if (ttl.isZero()) {
            throw new IllegalStateException("SSO code TTL is disabled");
        }

        String code = generateCode();
        String key = CODE_KEY_PREFIX + code;
        stringRedisTemplate.opsForValue().set(key, userId.toString(), ttl);
        return code;
    }

    public UUID consumeCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String key = CODE_KEY_PREFIX + code.trim();
        String userId = stringRedisTemplate.execute(GET_AND_DELETE_SCRIPT, Collections.singletonList(key));
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return UUID.fromString(userId.trim());
    }

    private String generateCode() {
        byte[] bytes = new byte[CODE_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

