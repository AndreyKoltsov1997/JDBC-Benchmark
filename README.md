# To be updated soon.
USAGE: ./benchmark.Benchmark *username* *password* --host=*host* --port=*port* ...
       ... --name=*name* --table=*table name* --payload=*payload* --insertions-=*amount of insertions* --threads=*amount of threads* --file=*name of file for results*

# Build with maven from root folder: 
mvn clean package
#Build docker image with maven:
mvn docker:build 
