FROM java:8
WORKDIR .
COPY target/scala-2.12/plarium-stats-1.0.jar /
ENTRYPOINT ["java","-jar","plarium-stats-1.0.jar"]
EXPOSE 8080