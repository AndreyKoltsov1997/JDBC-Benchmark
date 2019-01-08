# To be updated soon.
USAGE: ./benchmark.Benchmark *username* *password* --host=*host* --port=*port* ...
       ... --name=*name* --table=*table name* --payload=*payload* --insertions-=*amount of insertions* --threads=*amount of threads* --file=*name of file for results*

# Build with maven from root folder: 
$ mvn clean package -Ddb.username=(username here) -Ddb.password=(password here) -Ddb.host=(host here) -Ddb.port=(port here) -Ddb.name=(name here) -Ddb.table=(target table here) -Dbenchmark.payload=(payload here) -Dbenchmark.threads=(amount of threads here) -Dbenchmark.insertions=(amount of insertions here) -Dlog.file=(filename for logs here)

# Example: inserting 1000 bytes with 2 threads into PostgreSQL DB hosted on 10.5.0.6:5432, logging onto the file "log.csv"
$ mvn clean -Ddb.username=postgres -Ddb.password=password -Ddb.host=10.5.0.6 -Ddb.port=5432 -Dbenchmark.payload=1000 -Dbenchmark.threads=2 -Dlog.file=log.csv compile assembly:single package docker:build

# Build docker image with maven:
$ mvn -s .m2/settings.xml clean compile assembly:single package docker:build

# Run inside docker container: 
$ docker-compose up  
