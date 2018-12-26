FROM java:8
COPY target/user-service-0.0.1-SNAPSHOT.jar /tmp/user-service-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/tmp/user-service-0.0.1-SNAPSHOT.jar", "--server.servlet.context-path=/rpm-project" ,"&"]
