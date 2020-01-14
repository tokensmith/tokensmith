# Tokensmith
Tokensmith is a Java implementation of an [OAuth 2.0](http://tools.ietf.org/html/rfc6749) and OIDC Identity server. 

## Run the server

### locally

Configuration

XXX - I think these are in properties files now.

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

Or, from an IDE the main method is located at..
`net.tokensmith.authorization.http.server.GizmoServer`

### else where
XXX - recommendations for configuration


## Request features and report bugs
 - Include links to the RFCs that relate to the feature or bug.

## Project layout
This repo has 4 sub projects.

### [http](http)
Everything related to handling http.
### [core](core)
Use cases for supporting OAuth2 and OIDC
### [repository](repository)
Entities and Repository interfaces
### [login](login)
A SDK to interact with Tokensmith (OIDC ID Server).

## TODO
 - document configuration
 - document how to run/build
 - website