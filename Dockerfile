FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/globaledugroup.jar /app/globaledugroup.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/globaledugroup.jar"]