The MyBatis implementation for Auth Persistence
-----------------------------------------------
[![Build Status](https://travis-ci.org/base-services/auth-db-mybatis.svg?branch=development)](https://travis-ci.org/base-services/auth-db-mybatis)

Dependencies
------------
<ul>
    <li>Postgres 9.3</li>
    <li>Java 1.7</li>
    <li>Maven 3.2.3</li>
</ul>

Environment Variables for configuring db connection
---------------------------------------------------
```
export AUTH_DB_URL="jdbc:postgresql://127.0.0.1:5432/auth";
export AUTH_DB_USER="postgres";
export AUTH_DB_PASSWORD="";
export AUTH_DB_DRIVER="org.postgresql.Driver";
```

Running the tests from the terminal.
------------------------------------
<ul>
    <li>Install all dependencies.</li>
    <li>Set environment variables.</li>
    <li>Create the database specified in AUTH_DB_URL.</li>
    <li>Run migrations against the test database, `mvn migration:up`</li>
    <li>Use maven to run the tests, `mvn test`</li>
</ul>

Running the tests from IntelliJ 13.1.4
---------------------------------------
<ul>
    <li>Install all dependencies.</li>
    <li>Run -> Edit Configurations -> Defaults -> JUnit -> Configruation -> Enter Env Variables into, `Environment Variables`</li>
    <li>Run -> Edit Configurations -> Maven -> Enter `test` for `Command Line` -> Runner -> Enter Env Variables into, `Environment Variables`</li>
    <li>Create the database specified in AUTH_DB_URL.</li>
    <li>Run migrations against the test database, `mvn migration:up`</li>
    <li>You are ready to debug and run mvn commands from the IDE</li>
</ul>
