#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

export JAVA_HOME="$(./ensure_java17.sh)"
export PATH="$JAVA_HOME/bin:$PATH"

./ensure_gradle.sh
./gradlew runClient
