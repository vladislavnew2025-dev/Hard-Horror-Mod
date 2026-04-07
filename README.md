# Hard Horror Mod

Scaffold for a psychological horror Minecraft mod focused on anxiety, adaptive encounters, and rare high-impact screamers.

## Current repository state
This repository currently contains Java gameplay scaffolding (without full Forge MDK wiring yet) plus quick build scripts for Windows and Linux.

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
- Script first switches to its own directory (`%~dp0`) so outputs always go into the project root.
- Compiles all Java files into `out/`.
- Packs compiled classes into `dist/hard-horror-test-YYYYMMDD_HHMMSS.jar`.
- Uses `jar cf ...` when available, and falls back to PowerShell `Compress-Archive` if `jar` is missing/fails.
- Writes persistent build logs into `logs/build_YYYYMMDD_HHMMSS.log` (locale-safe timestamp via PowerShell).
- Keeps console open with `pause`, prints explicit source/class output counts, and prints explicit `RESULT: SUCCESS/FAILED` diagnostics.

If your terminal still closes instantly, use `build_quick_keep_open.bat`.
It also switches to project dir and then runs `build_quick.bat` through `cmd /k` so terminal stays open.

## Quick build on Linux
Use:
```bash
./build_quick.sh
```
- Compiles sources into `out/`.
- Packages `dist/hard-horror-test-YYYYMMDD_HHMMSS.jar`.
- Writes logs to `logs/build_YYYYMMDD_HHMMSS.log`.
- Prints explicit `RESULT: SUCCESS/FAILED` in terminal and log.

## Next step
Generate a Forge 1.20.1 MDK project structure and connect these classes to Forge events and rendering.
