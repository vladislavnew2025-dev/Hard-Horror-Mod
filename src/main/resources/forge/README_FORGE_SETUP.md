# Forge wiring (Minecraft 1.20.1)

This repository now includes Forge bridge classes under:
- `src/main/java/com/hardhorror/forge/HardHorrorForgeEntrypoint.java`
- `src/main/java/com/hardhorror/forge/ForgeEventBridge.java`

## How to use with Forge MDK
1. Create/download a Forge 1.20.1 MDK project.
2. Copy `com/hardhorror` package into the MDK `src/main/java`.
3. Copy `src/main/resources/forge/mods.toml.example` to MDK `src/main/resources/META-INF/mods.toml`.
4. Edit version/metadata in `mods.toml`.
5. Run `gradlew runClient` in the MDK project.

## Note
`build_quick.sh` and `build_quick.bat` in this repo compile only non-Forge core classes.
They are for logic iteration outside the full Forge runtime.
