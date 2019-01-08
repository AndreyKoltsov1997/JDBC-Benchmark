FROM java
ADD /jdbc-benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar //
ENTRYPOINT ["java", "-jar", "/jdbc-benchmark-1.0-SNAPSHOT-jar-with-dependencies.jar", "postgres", "password", "--name=", "--table=", "--host=10.5.0.6", "--port=5432", "--payload=1000", "--insertions=", "--threads=2", "--file=log.csv"]
