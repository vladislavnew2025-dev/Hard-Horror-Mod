#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

export JAVA_HOME="$(./ensure_java17.sh)"
export PATH="$JAVA_HOME/bin:$PATH"

./ensure_gradle.sh
./gradlew clean build

echo "[PsychologicalHorror] Build finished. Put this jar into Minecraft mods folder:"
ls -1 build/libs/*.jar
