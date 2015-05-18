Add the following to your ~/.profile
```bash
export AUTH_DB_URL="jdbc:postgresql://127.0.0.1:5432/auth";
export AUTH_DB_USER="postgres";
export AUTH_DB_PASSWORD="";
export AUTH_DB_DRIVER="org.postgresql.Driver";
```

IntelliJ
- install maven
- install Java 8 SDK
- clone the auth repository
- install IntelliJ
- start IntelliJ
- select 'create new project'
- selct 'Java project'
- Enter, 'auth-combined' for, Project Name
- A new project should start
- The right hand side should have a 'maven' tab, click it.
- The maven window should appear
- In the maven window toolbar, click the plus icon '+'
- a path window should open
- find the location of the auth git repo you cloned
- click on auth/pom.xml
- in the left hand project window auth should appear.
- start coding.
