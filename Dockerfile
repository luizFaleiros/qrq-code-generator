# Estágio de build
FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN curl -sSL -o opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.29.0/opentelemetry-javaagent.jar
RUN ./gradlew clean build -x test

# Estágio de execução
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
COPY --from=builder /app/opentelemetry-javaagent.jar opentelemetry-javaagent.jar

ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ENV AWS_REGION=us-east-1
ENV AWS_S3_BUCKET=qr-code-generator-storage
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar \
  -Dotel.service.name=qr-generetaor \
  -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
  -Dotel.logs.exporter=otlp \
  -Dotel.metrics.exporter=otlp\
  -Dotel.traces.exporter=otlp\
  -Dotel.metric.export.interval=15000 \
  -Dotel.exporter.otlp.protocol=grpc"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]