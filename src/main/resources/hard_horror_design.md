# Psychological Horror Mod — design notes (MVP)

## Target stack
- Minecraft 1.20.1
- Forge
- Java 17

## Core pillars
1. **Anxiety over jumpscares**: long quiet periods, subtle anomalies.
2. **Observation loop**: fear level drives effects and encounter intensity.
3. **Adaptive threat**: system reacts to player habits.
4. **Rare screamers**: impactful events with strict cooldown.

## Event budget (initial)
- Door screamer chance: 17%
- Cave screamer chance: 35%
- Overworld wander screamer chance: 1.5%
- Global screamer cooldown: 20 minutes

## Test entity + screamer (current)
- `WatcherEntity`: non-realistic placeholder entity for encounter testing.
- `EncounterManager`: controls simple watcher spawn and test screamer trigger.
- `PixelScreamer`: low-intensity ASCII/pixel screamer frame for development.

## Ending flow
1. Fear reaches critical threshold.
2. Camera detaches and player sees their own character.
3. World rewrite sequence starts (title/chat/environment corruption).
4. Save transitions to rewritten state.

## Integration checklist
- Wire `FearSystem` to player tick event.
- Wire `ScreamerScheduler` to door open + cave tick + wander tick hooks.
- Trigger client VFX/SFX by `FearLevel`.
- Add custom entity renderer + spawn controller.
- Implement rewrite sequence with timed server/client events.
