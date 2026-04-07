#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [[ -x ./gradlew ]]; then
  exit 0
fi

if command -v gradle >/dev/null 2>&1; then
  echo "[HardHorror] gradlew not found, generating wrapper via system gradle..."
  gradle wrapper
  exit 0
fi

GRADLE_VERSION="8.8"
LOCAL_DIR=".gradle-bin"
DIST_ZIP="gradle-${GRADLE_VERSION}-bin.zip"
DIST_DIR="${LOCAL_DIR}/gradle-${GRADLE_VERSION}"
GRADLE_BIN="${DIST_DIR}/bin/gradle"

mkdir -p "${LOCAL_DIR}"

if [[ ! -x "${GRADLE_BIN}" ]]; then
  URL="https://services.gradle.org/distributions/${DIST_ZIP}"
  echo "[HardHorror] system gradle not found, downloading ${URL} ..."
  curl -fL "${URL}" -o "${LOCAL_DIR}/${DIST_ZIP}"
  unzip -q -o "${LOCAL_DIR}/${DIST_ZIP}" -d "${LOCAL_DIR}"
fi

echo "[HardHorror] generating gradle wrapper via local Gradle ${GRADLE_VERSION} ..."
"${GRADLE_BIN}" wrapper
