FROM eclipse-temurin:23-jdk AS builder




LABEL maintainer="huiqing"




## How to build the application


WORKDIR /app


# copy files
COPY ./mvnw .
COPY .mvn .mvn




COPY pom.xml .
COPY src src




# make mvnw executable
RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true
# /app/target/eventmanagement-0.0.1-SNAPSHOT.jar


FROM eclipse-temurin:23-jre




WORKDIR /app




COPY --from=builder /app/target/movies-0.0.1-SNAPSHOT.jar vttpb-paf-assessment.jar


# check if curl command is available
RUN apt update && apt install -y curl




ENV PORT=8080
ENV SPRING_DATASOURCE_URL=jdbc:mysql://monorail.proxy.rlwy.net:12239/railway
ENV SPRING_DATASOURCE_USERNAME="" SPRING_DATASOURCE_PASSWORD=""
ENV SPRING_DATA_MONGODB_URI=""



EXPOSE ${PORT}


SHELL [ "/bin/sh", "-c" ]




ENTRYPOINT SERVER_PORT=${PORT} java -jar vttpb-paf-assessment.jar