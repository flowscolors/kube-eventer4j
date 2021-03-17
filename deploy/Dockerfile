FROM openjdk:8-jdk-alpine
ARG JAR_FILE=./eventer4j-0.1.0.jar
COPY ${JAR_FILE} eventer4j-0.1.0.jar
ENTRYPOINT ["java","-jar","/eventer4j-0.1.0.jar"]