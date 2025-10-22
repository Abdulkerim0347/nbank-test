#!/usr/bin/env bash
set -euo pipefail

IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")

OUT_POSIX="$(pwd)/test-output/$TIMESTAMP"
OUT_WIN="$(cygpath -w "$OUT_POSIX")"

mkdir -p "$OUT_POSIX"/{logs,results,report}

echo ">>> Building image..."
docker build -t "$IMAGE_NAME" .

echo ">>> Running ${TEST_PROFILE} tests..."
MSYS_NO_PATHCONV=1 MSYS2_ARG_CONV_EXCL="*" docker run --rm \
  --add-host=host.docker.internal:host-gateway \
  -v "${OUT_WIN}\logs:/app/logs" \
  -v "${OUT_WIN}\results:/app/target/surefire-reports" \
  -v "${OUT_WIN}\report:/app/target/site" \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://host.docker.internal:4111 \
  -e UIBASEURL=http://nginx:80 \
  -e UIREMOTE=http://host.docker.internal:4444/wd/hub \
  "$IMAGE_NAME"

echo "Log:     $OUT_POSIX/logs/run.log"
echo "Results: $OUT_POSIX/results"
echo "Report:  $OUT_POSIX/report"
