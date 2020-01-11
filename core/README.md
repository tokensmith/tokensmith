Use cases for, [OAuth 2.0](http://tools.ietf.org/html/rfc6749)
---------------------------------------------------------------------------------------------------------------------


Environment Variable for publishing to a message queue
----------------------------------------------

See pelican for details.

```bash
export MESSAGE_QUEUE_HOST='localhost:9092'
```

Running database migrations (replace values where necessary).
----------------------------------------------------
```
./gradlew -x test clean build
./gradlew flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"  
```

Running the tests from the terminal.
------------------------------------
```bash
./gradlew clean core:test
```

Interact with postgres
-----------------------

```bash
$ docker exec -it postgres bash

root@8581e63f4474:/# psql -U postgres
postgres=# \c auth;
auth=# \dt
```