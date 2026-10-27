package server.support;

import common.util.TimeSource;

/**
 * Reloj falso para probar cooldowns sin esperar tiempo real.
 */
public class MutableClock implements TimeSource {

    private long millis;

    public MutableClock(long startMillis) {
        this.millis = startMillis;
    }

    @Override
    public long currentTimeMillis() {
        return millis;
    }

    public void advance(long amount) {
        millis += amount;
    }
}
