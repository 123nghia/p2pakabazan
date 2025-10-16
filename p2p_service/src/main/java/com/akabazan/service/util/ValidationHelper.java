package com.akabazan.service.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for common validation operations
 */
@Component
public class ValidationHelper {

    /**
     * Checks if a string is not blank (not null, not empty, not whitespace only)
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Validates that all required fields are not blank
     */
    public static boolean hasAllRequiredFields(String... fields) {
        for (String field : fields) {
            if (!isNotBlank(field)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates amount is positive
     */
    public static boolean isPositiveAmount(double amount) {
        return amount > 0;
    }

    /**
     * Validates amount is within limits
     */
    public static boolean isAmountWithinLimits(double amount, double minLimit, double maxLimit) {
        return amount >= minLimit && amount <= maxLimit;
    }
}
