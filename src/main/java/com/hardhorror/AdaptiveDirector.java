package com.hardhorror;

/**
 * Learns from player behavior and adjusts event probabilities.
 */
public final class AdaptiveDirector {
    private int lookBacks;
    private int sprints;
    private int caveHides;

    public void recordLookBack() {
        lookBacks++;
    }

    public void recordSprint() {
        sprints++;
    }

    public void recordCaveHide() {
        caveHides++;
    }

    /**
     * Chance modifier for a rear apparition event.
     */
    public double rearApparitionBoost() {
        return Math.min(0.25d, lookBacks * 0.005d);
    }

    /**
     * Chance modifier for chase-style audio in overworld/caves.
     */
    public double chaseAudioBoost() {
        return Math.min(0.20d, sprints * 0.004d);
    }

    /**
     * Chance modifier for near-distance apparitions in caves/shelters.
     */
    public double closeManifestBoost() {
        return Math.min(0.30d, caveHides * 0.006d);
    }
}
