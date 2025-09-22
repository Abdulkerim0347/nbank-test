#!/bin/bash

# Настройки
IMAGE_NAME=nbank-tests
DOCKERHUB_USERNAME=abdulkerim0347
TAG=latest

# Токен берём из переменной окружения
if [ -z "$DOCKERHUB_TOKEN" ]; then
  echo "❌ Ошибка: DOCKERHUB_TOKEN не задан"
  exit 1
fi

# Логин в Docker Hub с токеном
echo ">>> Логин в Docker Hub с токеном"
echo $DOCKERHUB_TOKEN | docker login --username $DOCKERHUB_USERNAME --password-stdin

# Тегирование образа
echo ">>> Тегирование образа"
docker tag $IMAGE_NAME $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

# Отправка образа в Docker Hub
echo ">>> Отправка образа в Docker Hub"
docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

echo ">>> Готово! Образ доступен как: docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
