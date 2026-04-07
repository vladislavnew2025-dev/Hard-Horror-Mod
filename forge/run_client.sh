#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

./ensure_gradle.sh
./gradlew runClient
