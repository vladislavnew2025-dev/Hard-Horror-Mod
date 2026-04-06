package com.hardhorror;

/**
 * Tracks psychological pressure as a value from 0 to 100.
 */
public final class FearSystem {
    private static final int MIN = 0;
    private static final int MAX = 100;

    private int fear = 0;

    public int getFear() {
        return fear;
    }

    public void addFear(int amount) {
        fear = Math.min(MAX, fear + Math.max(0, amount));
    }

    public void reduceFear(int amount) {
        fear = Math.max(MIN, fear - Math.max(0, amount));
    }

    public FearLevel level() {
        if (fear < 30) {
            return FearLevel.LOW;
        }
        if (fear < 70) {
            return FearLevel.MEDIUM;
        }
        if (fear < 95) {
            return FearLevel.HIGH;
        }
        return FearLevel.CRITICAL;
    }

    public enum FearLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
