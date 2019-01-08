# JDBC Database Driver benchmark 

The app measures average bandwidth and throughput of JDBC Driver considering specified payload, threads and amount of insertion operations. 
It takes the following parameters: 
* test dot 1
* test dot 2
* test dot 3;
* test dor **4**; 
  1. Database credentials: 
    * username (parameter: *-Ddb.username*) - **required**
    * password (parameter:* -Ddb.password*) - **required** 
  2. Database location: 
    * host (parameter: -Ddb.host), if not specified - **"localhost" by default**; 
    * port (parameter: -Ddb.port), if not specified - **"8080" by default**;
  3. Target database info: 
    * database name - a name of target catalog. (parameter: *-Ddb.name*), if not specified - ** "jdbcBenchmarkDb" by default**;
    * table name - a name of target database. (parameter: **-Ddb.table**), if not specified - ** *current timestamp with respect to SQL available synbols for name* **
  4. Benchmark parameters: 
    * payload (parameter: *-Dbenchmark.payload*), if not specified - ** *"1"* by default since JDBC minimal allowed amount of inserted string is 1. **;
    * amount of threads which would be performing insert operations concurrently (parameter: *-Dbenchmark.threads*), if not specified - ** *"1"* by defult **;
    * amount of insertions (parameter: *-Dbenchmark.insertions*), if not specified - ** *infinite* amount of insertions **;
   5. Logging parameters: 
     * file name for logs (parameter: *-Dlog.file*), if file doesn't exist, it'd be created. If not specified - ** logging into file won't be activated. **

USAGE: ./benchmark.Benchmark *username* *password* --host=*host* --port=*port* ...
       ... --name=*name* --table=*table name* --payload=*payload* --insertions-=*amount of insertions* --threads=*amount of threads* --file=*name of file for results*



# Build with maven from root folder: 
$ mvn clean package -Ddb.username=(username here) -Ddb.password=(password here) -Ddb.host=(host here) -Ddb.port=(port here) -Ddb.name=(name here) -Ddb.table=(target table here) -Dbenchmark.payload=(payload here) -Dbenchmark.threads=(amount of threads here) -Dbenchmark.insertions=(amount of insertions here) -Dlog.file=(filename for logs here)

# Example: inserting 1000 bytes with 2 threads into PostgreSQL DB hosted on 10.5.0.6:5432, logging onto the file "log.csv" and dockerize it. 
$ mvn clean -Ddb.username=postgres -Ddb.password=password -Ddb.host=10.5.0.6 -Ddb.port=5432 -Dbenchmark.payload=1000 -Dbenchmark.threads=2 -Dlog.file=log.csv compile assembly:single package docker:build

# Build docker image with maven:
$ mvn -s .m2/settings.xml clean compile assembly:single package docker:build

# Run inside docker container: 
$ docker-compose up  
