package com.akabazan.framework.data.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "p2p.data")
public class P2PDataProperties {

    /** Database vendor identifier used for Flyway locations (e.g., postgres, mysql). */
    private String vendor = "postgres";

    /** Enable snake_case physical naming strategy for Hibernate. */
    private boolean namingSnakeCase = false;

    /** Strategy for ID generation (identity, uuid-v4, etc.). */
    private String idStrategy = "uuid-v4";

    /** Whether soft delete is enabled by convention. */
    private boolean softDeleteEnabled = false;

    /** Default timezone for JDBC/Hibernate interaction. */
    private String timezone = "UTC";

    /** If true, auto-add vendor-specific Flyway migration location. */
    private boolean flywayAutoLocationsEnabled = true;

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isNamingSnakeCase() {
        return namingSnakeCase;
    }

    public void setNamingSnakeCase(boolean namingSnakeCase) {
        this.namingSnakeCase = namingSnakeCase;
    }

    public String getIdStrategy() {
        return idStrategy;
    }

    public void setIdStrategy(String idStrategy) {
        this.idStrategy = idStrategy;
    }

    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }

    public void setSoftDeleteEnabled(boolean softDeleteEnabled) {
        this.softDeleteEnabled = softDeleteEnabled;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isFlywayAutoLocationsEnabled() {
        return flywayAutoLocationsEnabled;
    }

    public void setFlywayAutoLocationsEnabled(boolean flywayAutoLocationsEnabled) {
        this.flywayAutoLocationsEnabled = flywayAutoLocationsEnabled;
    }
}

