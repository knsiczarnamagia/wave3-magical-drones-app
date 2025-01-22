FROM eclipse-temurin:21

WORKDIR /app

COPY . /app

RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/magical-drones-app.jar"]