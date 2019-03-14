FROM openjdk:8-jre-alpine

WORKDIR /home/app

ADD target/upload-service-1.0-SNAPSHOT.jar .

CMD ["java", "-jar", "upload-service-1.0-SNAPSHOT.jar"]