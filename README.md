# Hard Horror Mod

Scaffold for a psychological horror Minecraft mod focused on anxiety, adaptive encounters, and rare high-impact screamers.

## Current repository state
This repository now contains:
- core Java gameplay scaffolding under `src/main/java/com/hardhorror`
- Forge bridge classes under `src/main/java/com/hardhorror/forge`
- a dedicated Forge Gradle project in `./forge` for real Minecraft запуск
- quick build scripts for Windows/Linux for lightweight logic checks

## Implemented systems
- `FearSystem`: 0..100 fear progression with level bands.
- `AdaptiveDirector`: behavior-driven probability modifiers.
- `ScreamerScheduler`: location-aware screamer chance + 20-minute cooldown.
- `EndingController`: staged ending with out-of-body camera and world rewrite state.
- `EncounterManager`: test encounter orchestration.
- `WatcherEntity`: simple non-realistic test entity placeholder.
- `PixelScreamer`: low-intensity text-based screamer frame for tests.

## IMPORTANT: quick build vs real Forge mod
- `build_quick.sh` / `build_quick.bat` → compile logic and package a plain jar for checks.
- `forge/` project (`./gradlew runClient`) → real Forge runtime build/run path for Minecraft.

## Run as Forge mod on Linux (recommended now)
```bash
cd forge
./run_client.sh
```
If `gradlew` is missing, `run_client.sh` tries `gradle wrapper` automatically.

## Build installable Forge mod jar (for mods folder)
```bash
cd forge
./build_mod.sh
```
After build, take jar from `forge/build/libs/` and copy it into Minecraft `mods/` for Forge 1.20.1.

## Quick build on Linux
Use:
```bash
./build_quick.sh
```
- Compiles sources into `out/`.
- Packages `dist/hard-horror-test-YYYYMMDD_HHMMSS.jar`.
- Writes logs to `logs/build_YYYYMMDD_HHMMSS.log`.
- Prints explicit `RESULT: SUCCESS/FAILED` in terminal and log.

## Quick build on Windows
Use `build_quick.bat`.
- Script first switches to its own directory (`%~dp0`) so outputs always go into the project root.
- Compiles all Java files into `out/`.
- Packs compiled classes into `dist/hard-horror-test-YYYYMMDD_HHMMSS.jar`.
- Uses `jar cf ...` when available, and falls back to PowerShell `Compress-Archive` if `jar` is missing/fails.
- Writes persistent build logs into `logs/build_YYYYMMDD_HHMMSS.log`.
- Keeps console open with `pause`, prints explicit source/class output counts, and prints explicit `RESULT: SUCCESS/FAILED` diagnostics.

If terminal still closes instantly, use `build_quick_keep_open.bat`.

## Forge integration bridge files
- `src/main/java/com/hardhorror/forge/HardHorrorForgeEntrypoint.java`
- `src/main/java/com/hardhorror/forge/ForgeEventBridge.java`
- `src/main/resources/forge/mods.toml.example`
- `src/main/resources/forge/README_FORGE_SETUP.md`
