#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if command -v java >/dev/null 2>&1; then
  JAVA_MAJOR=$(java -version 2>&1 | awk -F[\".] '/version/ {print $2}')
  if [[ "$JAVA_MAJOR" == "17" ]]; then
    if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
      echo "$JAVA_HOME"
      exit 0
    fi
    JAVA_BIN=$(command -v java)
    JAVA_HOME_GUESS=$(cd "$(dirname "$JAVA_BIN")/.." && pwd)
    echo "$JAVA_HOME_GUESS"
    exit 0
  fi
fi

JDK_DIR=".jdk"
mkdir -p "$JDK_DIR"
JDK_MARKER="$JDK_DIR/java17-home.txt"

if [[ -f "$JDK_MARKER" ]]; then
  CACHED_HOME=$(cat "$JDK_MARKER")
  if [[ -x "$CACHED_HOME/bin/java" ]]; then
    echo "$CACHED_HOME"
    exit 0
  fi
fi

URL="https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse"
ARCHIVE="$JDK_DIR/temurin17.tar.gz"

echo "[PsychologicalHorror] Java 17 not found, downloading Temurin JDK 17..."
curl -fL "$URL" -o "$ARCHIVE"

tar -xzf "$ARCHIVE" -C "$JDK_DIR"
EXTRACTED=$(find "$JDK_DIR" -maxdepth 1 -type d -name 'jdk-*' | head -n 1)

if [[ -z "$EXTRACTED" || ! -x "$EXTRACTED/bin/java" ]]; then
  echo "[PsychologicalHorror] ERROR: failed to prepare local JDK 17"
  exit 1
fi

echo "$EXTRACTED" > "$JDK_MARKER"
echo "$EXTRACTED"
