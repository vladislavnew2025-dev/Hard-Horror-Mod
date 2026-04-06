package com.hardhorror;

/**
 * Defines the final sequence when fear reaches critical values.
 */
public final class EndingController {

    public EndingState nextState(FearSystem fearSystem, EndingState currentState) {
        if (fearSystem.level() != FearSystem.FearLevel.CRITICAL) {
            return currentState;
        }

        return switch (currentState) {
            case DORMANT -> EndingState.OUT_OF_BODY_CAMERA;
            case OUT_OF_BODY_CAMERA -> EndingState.WORLD_REWRITE;
            case WORLD_REWRITE -> EndingState.COMPLETE;
            case COMPLETE -> EndingState.COMPLETE;
        };
    }

    public enum EndingState {
        DORMANT,
        OUT_OF_BODY_CAMERA,
        WORLD_REWRITE,
        COMPLETE
    }
}
