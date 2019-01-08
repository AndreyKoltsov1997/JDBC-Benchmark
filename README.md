# JDBC Database Driver benchmark 

	The app measures average bandwidth and throughput of JDBC Driver considering specified payload, threads and amount of insertion operations. 


#1. Overview

## 1.1 Supported database.
The app is able to test the following databases: 
 * **PostgreSQL**;

## 1.2 Metrics
The benchmark provides 2 metrics for JDBC DB driver: 
 * Average bandwidth (payload bytes inserted per second);
 * Average thoughput (insert operations per second);

# 2. Benchmark configuration.

The app takes the following parameters: 
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


# 3. Build and run. 

 The app uses maven for building. *WARNING*: It's required to use custom Maven settings (stored within .m2/settings.xml), make sure to state them. 
 Custom settings are used for building docker image and deploying the app to maven repository. 
 The parameters of the app are specified and described in paragraph 2. 
In order to build it with default parameters (except database credentials, which are required), use: 
 > $ mvn clean -Ddb.username=postgres -Ddb.password=password compile assembly:single package

Benchmark's maven configuration could build docker image. In order to build docker image for the app with default configuration, use "docker:build" command. Example:

> $ mvn -s .m2/settings.xml clean -Ddb.username=postgres -Ddb.password=password compile assembly:single package docker:build

# 4. Test run. 

In order see how benchmark works, it's possible to launch in with the reference database (**PostgreSQL**) inside docker container. 

## 4.1 Reference DB configuration. 
 * *database*: PostgreSQL ;
 * *host*:  10.5.0.6:5432 ;
 * *username*: postgresql ;
 * *password*: pasword ;

 ## 4.2 Launching. 
  *Example*: inserting 1000 bytes with 2 threads into PostgreSQL DB hosted on 10.5.0.6:5432 inside docker container.
  Firstly, you'll have to build the app: 
  > $ mvn -s .m2/settings.xml  clean -Ddb.username=postgres -Ddb.password=password -Ddb.host=10.5.0.6 -Ddb.port=5432 -Dbenchmark.payload=1000 -Dbenchmark.threads=2 compile assembly:single package docker:build
  After that, simply write
  > $ docker-compose up 

