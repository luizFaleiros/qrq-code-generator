# QR CODE GENERATOR
This project is built to generate a QR Code using [zxing](https://github.com/zxing/zxing) library and save file on amazon s3.
## Running
To Runing the locally we will use [docker localStack](https://hub.docker.com/r/localstack/localstack) and is necessary to install this dependency [awscli](https://docs.localstack.cloud/user-guide/integrations/aws-cli/#localstack-aws-cli-awslocal) to work properly.
In case you want to run with observability wee will need to download the [opentelemetry-javaanget](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.29.0/opentelemetry-javaagent.jar) and set the folowing properties values to run

```
otel.exporter.otlp.endpoint=http://localhost:4317;otel.exporter.otlp.protocol=grpc;otel.logs.exporter=otlp;otel.metric.export.interval=15000;otel.metrics.exporter=otlp;otel.service.name=generator;otel.traces.exporter=otlp;JAVA_TOOL_OPTIONS=-javaagent:opentelemetry-javaagent.jar
```
