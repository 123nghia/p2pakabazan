package com.akabazan.framework.data.spi;

import java.time.Clock;

public class DefaultClockProvider implements ClockProvider {
    private final Clock clock;

    public DefaultClockProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Clock getClock() {
        return clock;
    }
}

