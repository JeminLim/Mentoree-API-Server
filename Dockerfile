FROM openjdk:17-ea-11-jdk-slim
VOLUME /mentoree
COPY /build/libs/mentoree-1.0.jar mentoree.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "mentoree.jar"]