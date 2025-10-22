#!/bin/bash
set -e  # stop script on any error

IMAGE_NAME=nbank-tests
DOCKERHUB_USERNAME=abdulkerim0347
COMMIT_HASH=$(git rev-parse --short HEAD)
TAG=${COMMIT_HASH:-latest}

if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "❌ ERROR: DOCKERHUB_TOKEN not set"
  exit 1
fi

echo ">>> Logging in to Docker Hub..."
echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

echo ">>> Tagging image as ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"
docker tag "${IMAGE_NAME}" "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"

echo ">>> Pushing image..."
docker push "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"

echo "✅ Done! Image available at:"
echo "docker pull ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"
