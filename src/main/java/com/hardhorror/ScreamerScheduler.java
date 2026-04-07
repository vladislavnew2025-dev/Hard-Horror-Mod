package com.hardhorror;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * Controls rare screamer events and their cooldown.
 */
public final class ScreamerScheduler {
    private static final Duration GLOBAL_COOLDOWN = Duration.ofMinutes(20);
    private static final double DOOR_CHANCE = 0.17d;
    private static final double CAVE_CHANCE = 0.35d;
    private static final double WORLD_CHANCE = 0.015d;

    private final Random random = new Random();
    private Instant nextAllowed = Instant.EPOCH;

    public boolean onDoorOpened(Instant now) {
        return roll(now, DOOR_CHANCE);
    }

    public boolean onCaveTick(Instant now) {
        return roll(now, CAVE_CHANCE);
    }

    public boolean onWorldWanderTick(Instant now) {
        return roll(now, WORLD_CHANCE);
    }

    private boolean roll(Instant now, double chance) {
        if (now.isBefore(nextAllowed)) {
            return false;
        }
        if (random.nextDouble() <= chance) {
            nextAllowed = now.plus(GLOBAL_COOLDOWN);
            return true;
        }
        return false;
    }

    public Instant nextAllowedAt() {
        return nextAllowed;
    }
}
