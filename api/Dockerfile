FROM gradle:8.14-jdk24 AS build

LABEL maintainer="Pablo Liste Cancela <ppabli12@gmail.com>"

WORKDIR /api
COPY . .
RUN gradle clean bootJar --no-daemon

FROM openjdk:25-jdk-slim
WORKDIR /api
COPY --from=build /api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]