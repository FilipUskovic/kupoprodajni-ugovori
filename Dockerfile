##TODO: ovo je isto trnutno opcionalno za ovu aplikaciju jer ce oduzeti nesto vremena dok se sve skine i build-a

FROM eclipse-temurin:21-jdk-alpine AS build
LABEL authors="filipuskovic"

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
#TODO: za produkciju ne bih Preskakao testove no zelim ustediti vrije na pokretanju
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
