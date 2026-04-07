#!/usr/bin/env bash
set -u

cd "$(dirname "$0")"
mkdir -p logs out dist

TS="$(date +%Y%m%d_%H%M%S)"
LOG_FILE="logs/build_${TS}.log"
JAR_FILE="dist/psychological-horror-test-${TS}.jar"

log() {
  echo "$1" | tee -a "$LOG_FILE"
}

log "[PsychologicalHorror] Quick build started"
log "[PsychologicalHorror] Working directory: $(pwd)"

if ! command -v javac >/dev/null 2>&1; then
  log "ERROR: javac was not found in PATH."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
  log "ERROR: jar was not found in PATH."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

SRC_COUNT=$(find src/main/java/com/hardhorror -maxdepth 1 -type f -name '*.java' | wc -l | tr -d ' ')
log "Source files: ${SRC_COUNT}"
if [[ "$SRC_COUNT" -le 0 ]]; then
  log "ERROR: no Java sources were found."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

log "Compiling Java sources..."
if ! javac -d out src/main/java/com/hardhorror/*.java >>"$LOG_FILE" 2>&1; then
  log "ERROR: javac compilation failed."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

CLASS_COUNT=$(find out -type f -name '*.class' | wc -l | tr -d ' ')
log "Class files counted: ${CLASS_COUNT}"
if [[ "$CLASS_COUNT" -le 0 ]]; then
  log "ERROR: no .class files were produced."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

rm -f "$JAR_FILE"
log "Packaging jar to $JAR_FILE"
if ! jar cf "$JAR_FILE" -C out . >>"$LOG_FILE" 2>&1; then
  log "ERROR: jar packaging failed."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

if [[ ! -f "$JAR_FILE" ]]; then
  log "ERROR: jar file is missing after packaging."
  log "[PsychologicalHorror] RESULT: FAILED"
  exit 1
fi

log "Build OK. Compiled ${SRC_COUNT} files, generated ${CLASS_COUNT} class files."
log "Jar created: ${JAR_FILE}"
log "[PsychologicalHorror] RESULT: SUCCESS"
