#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [[ ! -x ./gradlew ]]; then
  if command -v gradle >/dev/null 2>&1; then
    echo "[HardHorror] gradlew missing, generating wrapper via 'gradle wrapper'..."
    gradle wrapper
  else
    echo "[HardHorror] ERROR: gradlew not found and 'gradle' is not installed."
    echo "Install Gradle or copy Forge MDK wrapper files into ./forge."
    exit 1
  fi
fi

./gradlew clean build

echo "[HardHorror] Build finished. Put this jar into Minecraft mods folder:"
ls -1 build/libs/*.jar
