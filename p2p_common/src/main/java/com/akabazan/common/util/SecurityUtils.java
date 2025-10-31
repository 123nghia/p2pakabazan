package com.akabazan.common.util;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Lấy userId của người dùng đang đăng nhập từ SecurityContext.
     */
    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
