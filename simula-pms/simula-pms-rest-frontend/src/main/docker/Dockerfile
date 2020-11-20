FROM adoptopenjdk:15.0.1_9-jre-openj9-0.23.0
RUN ["mkdir", "/opt/simulapms"]
COPY --chown=root:root ../../../target/simula-pms-rest-frontend-*.jar /opt/simulapms/simula-pms-rest-frontend.jar
COPY --chown=root:root ../../../target/dependency/*.jar /opt/simulapms/
RUN ["chmod", "-R", "400", "/opt/simulapms"]
CMD ["java", "-javaagent:/opt/simulapms/spring-instrument.jar", "-javaagent:/opt/simulapms/aspectjweaver.jar", "-jar", "/opt/simulapms/simula-pms-rest-frontend.jar"] 