# Tokensmith
Tokensmith is a Java implementation of an [OAuth 2.0](http://tools.ietf.org/html/rfc6749) and [OIDC](https://openid.net/) Identity server. 

## Run the server

### Locally

#### Configuration

The server is configured in a [properties file](http/src/main/resources/application-default.properties). The values can be overidden with command line arguments or environemnt variables. More information for how to overide the default values is  available in [spring's docs](https://docs.spring.io/spring-boot/docs/1.3.0.M4/reference/html/boot-features-external-config.html). 

 - Arguments can passed in as, `-DallowLocalUrls=false`
 - Environment variables can be set as, `export allowLocalUrls=false`

#### Start Postgres and Kafka 
Start database and kafka.
```bash
docker-compose build
docker-compose up -d
```

#### Run database migrations.
```bash
./gradlew -x test clean build
./gradlew core:flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"
```

#### Compile the application
```bash
./gradlew clean install
```

#### Start the application
```bash
java -jar http/build/libs/http-0.0.1-SNAPSHOT.war
```

Or, from an IDE the main method is in [TokenSmithServer](http/src/main/java/net/tokensmith/authorization/http/server/TokenSmithServer.java)

## Seeding the database

The database is initially seeded with a few [database migrations](https://github.com/tokensmith/tokensmith/tree/development/core/src/main/resources/db/migration). 

## Messages that are published
Tokensmith publishes messages to kafka when
 - A user [registers](core/src/main/java/net/tokensmith/authorization/register/Register.java#L97) through interacting with `/register` 
 - A user [registers](core/src/main/java/net/tokensmith/authorization/register/RegisterOpenIdUser.java#L74) through interacting with the API.
 - A user requests to [reset their password](core/src/main/java/net/tokensmith/authorization/nonce/reset/ForgotPassword.java#L68) through interacting with `/forgot-password`.
 - A user [reset their password](https://github.com/tokensmith/tokensmith/blob/c4dcc8a0af08600a5bf75b9faedf4629b7e97002/core/src/main/java/net/tokensmith/authorization/nonce/reset/ForgotPassword.java#L107).

To send emails to users, run the [mailer](https://github.com/tokensmith/mailer) application

Or, subscribe to the topic, `mailer` and write a mailer worker.

## Interaction

See the [http](http/README.md) readme for documents on interacting with Tokensmith.

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