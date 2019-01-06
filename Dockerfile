FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY out/artifacts/jdbc_benchmark_jar/jdbc-benchmark.jar $PROJECT_HOME/jdbc-benchmark.jar

