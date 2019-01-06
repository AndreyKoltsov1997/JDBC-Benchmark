# NOTE: Creating PostgreSQL with both username and password set to "docker"

FROM library/postgres
ENV POSTGRES_USER docker
ENV POSTGRES_PASSWORD docker
ENV POSTGRES_DB docker

# NOTE: Dockerizing JDBC benchmark (Java) 
FROM openjdk:8-jdk-alpine3.7 AS builder
RUN java -version

COPY . /benchmark/
WORKDIR /benchmark/
RUN apk --no-cache add maven && mvn --version
RUN ls /benchmark
RUN mvn package

# Stage 2 (to create a downsized "container executable", ~87MB)
FROM openjdk:8-jre-alpine3.7
WORKDIR /root/
COPY --from=builder /benchmark/target/benchmark.jar .

EXPOSE 8123
ENTRYPOINT ["java", "-jar", "./benchmark.jar"]