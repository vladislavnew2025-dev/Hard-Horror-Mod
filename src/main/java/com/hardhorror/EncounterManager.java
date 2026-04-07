package com.hardhorror;

import java.util.Random;

/**
 * Handles test encounters: a simple watcher entity and lightweight screamer.
 */
public final class EncounterManager {
    private final Random random = new Random();
    private WatcherEntity watcher;

    public void trySpawnWatcher(FearSystem.FearLevel fearLevel) {
        if (watcher != null && watcher.active()) {
            return;
        }

        double spawnChance = switch (fearLevel) {
            case LOW -> 0.0d;
            case MEDIUM -> 0.01d;
            case HIGH -> 0.03d;
            case CRITICAL -> 0.06d;
        };

        if (random.nextDouble() <= spawnChance) {
            watcher = WatcherEntity.spawnForTest(random);
            System.out.println("[PsychologicalHorror] Watcher spawned at distance " + watcher.distanceBlocks());
        }
    }

    public WatcherEntity watcher() {
        return watcher;
    }

    public void triggerTestScreamer() {
        System.out.println("[PsychologicalHorror] Screamer triggered (test).\n" + PixelScreamer.frame());
    }
}
