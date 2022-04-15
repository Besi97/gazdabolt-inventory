FROM eclipse-temurin:17-jre

WORKDIR /usr/share/inventory-service
ARG JAR_FILE_NAME

ADD target/lib lib
ADD target/${JAR_FILE_NAME}.jar inventory-service.jar

ENTRYPOINT ["java", "-jar", "/usr/share/inventory-service/inventory-service.jar"]
