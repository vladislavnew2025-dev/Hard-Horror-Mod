#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

./ensure_gradle.sh
./gradlew clean build

echo "[HardHorror] Build finished. Put this jar into Minecraft mods folder:"
ls -1 build/libs/*.jar
