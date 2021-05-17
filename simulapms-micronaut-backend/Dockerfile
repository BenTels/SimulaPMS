FROM openjdk:16.0.1
ARG JAR_FILE
COPY target/${JAR_FILE} /opt/service.jar
CMD java -jar /opt/service.jar