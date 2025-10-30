package com.akabazan.framework.data.spi;

import java.time.Clock;

public interface ClockProvider {
    Clock getClock();
}

