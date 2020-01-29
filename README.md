# Tokensmith
Tokensmith is a Java implementation of an [OAuth 2.0](http://tools.ietf.org/html/rfc6749) and OIDC Identity server. 

## Run the server

### locally

#### Configuration

The server is configured in a [properties file](http/src/main/resources/application-default.properties) which can be 
overridden by either using environment variables or command line arguments.

Arguments are passed in as, `-DallowLocalUrls=false`
Environment variables would be set as, `export allowLocalUrls=false`

More information is available in [spring's docs](https://docs.spring.io/spring-boot/docs/1.3.0.M4/reference/html/boot-features-external-config.html).

One item is required to set as an environment variable which instructs Token Smith where to publish messages.
```bash
export MESSAGE_QUEUE_HOST='localhost:9092'
```

Hard coded config exists in [TokenSmithConfig](http/src/main/java/net/tokensmith/authorization/http/server/TokenSmithConfig.java).
Contributions are welcome to make that dynamic.

Start database and kafka.
```bash
docker-compose build
docker-compose up -d
```

Run database migrations.
```bash
./gradlew -x test clean build
./gradlew core:flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"
```

Compile the application
```bash
./gradlew clean install
```

Start the application
```bash
java -jar http/build/libs/http-0.0.1-SNAPSHOT.war
```

Or, from an IDE the main method is in [TokenSmithServer](http/src/main/java/net/tokensmith/authorization/http/server/TokenSmithServer.java)

## Seeding the database

The database is initially seeded with a few [database migration](https://github.com/tokensmith/tokensmith/tree/development/core/src/main/resources/db/migration). 

## Interaction

See the [http](http/README.md) readme for documents on interacting.

## Request features and report bugs
 - Include links to the RFCs that relate to the feature or bug.

## Project layout
This repo has 4 sub projects.

### [http](http)
Everything related to accepting and responding to HTTP requests
### [core](core)
Use cases for supporting OAuth2 and OIDC.
### [repository](repository)
Entities and Repository interfaces
### [login](login)
A SDK to interact with Tokensmith (OIDC ID Server).