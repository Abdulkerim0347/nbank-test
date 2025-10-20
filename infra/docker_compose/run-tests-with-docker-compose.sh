#!/bin/bash


echo "Поднимаем тестовое окружение"

docker compose up -d


echo "Запускаем тесты"

docker run --rm \
  -e APIBASEURL=http://localhost:4111 \
  -e UIBASEURL=http://localhost:80 \
  -e SELENOID_URL=http://localhost:4444 \
  -e SELENOID_UI_URL=http://localhost:8080 \
  nbank-tests


echo "Останавливаем тестовое окружение"

docker compose down