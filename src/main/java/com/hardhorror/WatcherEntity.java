package com.hardhorror;

import java.util.Random;

/**
 * Non-realistic test entity placeholder.
 */
public record WatcherEntity(int distanceBlocks, boolean active) {

    public static WatcherEntity spawnForTest(Random random) {
        int distance = 8 + random.nextInt(25);
        return new WatcherEntity(distance, true);
    }
}
