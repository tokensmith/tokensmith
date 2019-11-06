HTTP for Authorization
-----------------------

Dependencies
------------
 - Postgres
 - Kafka

Contributing
------------
 - All code changes must have a story or bug written in Gherkin.
 - Follow the auth [setup](https://github.com/RootServices/auth/blob/development/setup.md) instructions
 - Follow the auth-http [setup](setup.md) instructions
 - All code must be written with the SOLID principles.
 - Unit and Integration tests are required.

Requesting Features and reporting bugs
-------------------------------------
 - Features are reported and tracked in [pivotal tracker](https://www.pivotaltracker.com/n/projects/1199316).
 - Reporting issues through github is acceptable. We will probably transfer them to PT.


Running migrations (replace values where necessary).
----------------------------------------------------
```
mvn clean package -DskipTests
mvn flyway:migrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth" -Dflyway.initOnMigrate=true
```

Running the tests from the terminal.
------------------------------------
 - Install all dependencies.
 - Set environment variables (see, Running Migrations).
 - Create the db specified in, `application-default.properties`.
 - Run migrations against the test db (see, Running Migrations)
 - Use gradle to run the tests, `./gradlew clean test`

Running the application
------------------------
 - start database server
 - run the persistence migrations (see, Running Migrations)
 - start zookeeper and kafka. [Pelican](https://github.com/RootServices/pelican/blob/development/contributing.md) 
 has some docs on how to get them started.
 - java -jar /path/to/auth-http/build/libs/gizmo-http-<version>.war

