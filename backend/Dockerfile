FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /home/app
COPY pom.xml pom.xml
RUN mvn dependency:go-offline
COPY . .
RUN mvn package -Dmaven.test.skip=true
# RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre AS deploy
WORKDIR /home/app
COPY --from=build /home/app/target/football5-0.0.1-SNAPSHOT.jar /home/app/football5-0.0.1-SNAPSHOT.jar
COPY /src/main/java/ar/uba/fi/ingsoft1/football5/common/img/default_profile.webp /home/app/default_profile.webp
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/app/football5-0.0.1-SNAPSHOT.jar"]