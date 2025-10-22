#!/bin/bash
set -e  # stop script on any error

IMAGE_NAME=nbank-tests
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-abdulkerim0347}
: "${DOCKERHUB_TOKEN:?DOCKERHUB_TOKEN not set}"

COMMIT_HASH=$(git rev-parse --short HEAD)
TAG="${COMMIT_HASH}"

if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "❌ ERROR: DOCKERHUB_TOKEN not set"
  exit 1
fi

echo ">>> Login"
echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

echo ">>> Build ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"
docker build -t "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}" .

echo ">>> Push"
docker push "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"

echo "✅ Done! Image available at:"
echo "docker pull ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"
