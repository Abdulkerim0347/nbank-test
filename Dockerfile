FROM maven:3.9.9-eclipse-temurin-23

# Дефолтные значения аргументов
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:80

# Переменные окружения для контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}

# работаем из папки /app
WORKDIR /app

# копируем помник
COPY pom.xml .

# загружаем зависимости и кешируем
RUN mvn dependency:go-offline

# копируем весь проект
COPY . .

# теперь внутри есть зависимости, есть весь проект и мы готовы запускать тесты

USER root

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile: ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
   } > /app/logs/run.log 2>&1"