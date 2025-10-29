#!/bin/bash

# ШАГ 1: Поднятие сервисов приложения

# Запуск локального Kubernetes-кластера с использованием Docker как драйвера
minikube start --driver=docker

# Создание ConfigMap для конфигурации Selenoid
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

# Установка Helm чарта с релизом nbank
helm install nbank ./nbank-chart

# Получение списка сервисов в кластере
kubectl get svc

# Получение списка подов в кластере
kubectl get pods

# Просмотр логов конкретного сервиса (например, backend)
kubectl logs deployment/backend

# Проброс портов на локальную машину
kubectl port-forward svc/frontend 3000:80
kubectl port-forward svc/backend 4111:4111
kubectl port-forward svc/selenoid 4444:4444
kubectl port-forward svc/selenoid-ui 8080:8080