FROM java
ADD /jdbc-benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar //
ENTRYPOINT ["java", "-jar", "/jdbc-benchmark.jar", "postgres", "password", "--host=localhost", "--port=5432"]
