package com.hardhorror;

import java.time.Instant;

/**
 * Entry point for the Hard Horror mod logic scaffold.
 *
 * <p>This repository still has no Forge/Fabric runtime attached,
 * so this class includes a tiny simulation loop method that can
 * be wired to real game events later.</p>
 */
public final class HardHorrorMod {
    public static final String MOD_ID = "psychologicalhorror";

    private final FearSystem fearSystem;
    private final AdaptiveDirector adaptiveDirector;
    private final ScreamerScheduler screamerScheduler;
    private final EndingController endingController;
    private final EncounterManager encounterManager;

    public HardHorrorMod() {
        this.fearSystem = new FearSystem();
        this.adaptiveDirector = new AdaptiveDirector();
        this.screamerScheduler = new ScreamerScheduler();
        this.endingController = new EndingController();
        this.encounterManager = new EncounterManager();
    }

    /**
     * Test-only simulation tick to mimic dark cave pressure.
     */
    public void tickSimulation(boolean inDarkness, boolean underground, boolean openedDoor) {
        if (inDarkness) {
            fearSystem.addFear(1);
        }
        if (underground) {
            fearSystem.addFear(2);
        }

        if (openedDoor && screamerScheduler.onDoorOpened(Instant.now())) {
            encounterManager.triggerTestScreamer();
        }

        encounterManager.trySpawnWatcher(fearSystem.level());
    }

    public FearSystem fearSystem() {
        return fearSystem;
    }

    public AdaptiveDirector adaptiveDirector() {
        return adaptiveDirector;
    }

    public ScreamerScheduler screamerScheduler() {
        return screamerScheduler;
    }

    public EndingController endingController() {
        return endingController;
    }

    public EncounterManager encounterManager() {
        return encounterManager;
    }
}
