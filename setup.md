Environment Variables
---------------------
 - Edit, `~/.profile`
 
   ```bash
   export AUTH_DB_URL="jdbc:postgresql://127.0.0.1:5432/auth";
   export AUTH_DB_USER="postgres";
   export AUTH_DB_PASSWORD="";
   export AUTH_DB_DRIVER="org.postgresql.Driver";
   ```
 - Run the command, `source ~/.profile`

Postgres
--------
 - install Postgres
 - create the database, `auth`

Run Database Migrations
------------------
```bash
mvn clean package -DskipTests
mvn flyway:migrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth" -Dflyway.initOnMigrate=true
```

Install Maven
--------------

Install Java 8 SDK
-------------------

IntelliJ 14.1.1
---------------
- clone the auth repository
- install IntelliJ
- start IntelliJ
- select, `create new project`
- select, `Java project`
- Enter, `auth-combined` for, Project Name
- A new project should start
- The right hand side should have a `maven` tab, click it.
- The maven window should appear
- In the maven window toolbar, click the plus icon `+`
- a path window should open
- find the location of the auth git repo you cloned
- click on `auth/pom.xml`
- in the left hand project window `auth` should appear.
- start coding.
