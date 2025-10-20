#!/usr/bin/env bash
set -Eeuo pipefail

echo ">>> Остановить Docker Compose"
docker compose down || true

# Optional: login to avoid anon throttling
if [[ -n "${DOCKERHUB_USER:-}" && -n "${DOCKERHUB_PWD:-}" ]]; then
  echo ">>> Docker Hub login"
  echo "$DOCKERHUB_PWD" | docker login -u "$DOCKERHUB_USER" --password-stdin || true
fi

retry() { # exponential backoff: 6 tries, 5s,10s,15s,20s,25s,30s
  local n=1 max=6
  while true; do
    "$@" && return 0 || {
      (( n == max )) && return 1
      echo ">>> Retry $n/$max for: $*"; sleep $((5*n)); ((n++))
    }
  done
}

echo ">>> Docker pull все образы браузеров"
json_file="./config/browsers.json"
if ! command -v jq &>/dev/null; then
  echo "❌ jq is not installed. Please install jq and try again."; exit 1
fi
if [[ ! -f "$json_file" ]]; then
  echo "❌ File not found: $json_file"; exit 1
fi

# уникальные имена образов из .image
mapfile -t IMAGES < <(jq -r '.. | objects | select(has("image")) | .image' "$json_file" | sort -u)

for image in "${IMAGES[@]}"; do
  echo "Pulling $image..."
  retry docker pull "$image"
done

echo ">>> docker compose pull (с ретраями)"
retry docker compose pull

echo ">>> Запуск Docker Compose"
retry docker compose up -d --remove-orphans

echo "✅ Готово"
