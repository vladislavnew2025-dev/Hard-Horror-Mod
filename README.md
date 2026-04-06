# Hard Horror Mod

Scaffold for a psychological horror Minecraft mod focused on anxiety, adaptive encounters, and rare high-impact screamers.

## Current repository state
This repository currently contains Java gameplay scaffolding (without full Forge MDK wiring yet) plus a quick Windows build script.

## Implemented systems
- `FearSystem`: 0..100 fear progression with level bands.
- `AdaptiveDirector`: behavior-driven probability modifiers.
- `ScreamerScheduler`: location-aware screamer chance + 20-minute cooldown.
- `EndingController`: staged ending with out-of-body camera and world rewrite state.
- `EncounterManager`: test encounter orchestration.
- `WatcherEntity`: simple non-realistic test entity placeholder.
- `PixelScreamer`: low-intensity text-based screamer frame for tests.

## Quick build on Windows
Use `build_quick.bat`.
- Compiles all Java files into `out/`.
- Writes persistent build logs into `logs/build_YYYYMMDD_HHMMSS.log` (locale-safe timestamp via PowerShell).
- Keeps console open with `pause`, prints explicit source/class output counts, and prints explicit compile diagnostics for easier troubleshooting on Windows.

## Next step
Generate a Forge 1.20.1 MDK project structure and connect these classes to Forge events and rendering.
