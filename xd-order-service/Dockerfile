
#FROM  adoptopenjdk/openjdk11:ubi
# jre take less space
FROM  adoptopenjdk/openjdk11:jre11u-nightly
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]