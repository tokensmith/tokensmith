Environment Variables
---------------------
 - Edit, `~/.profile`
 
   ```bash
   export AUTH_DB_URL="jdbc:postgresql://127.0.0.1:5432/auth";
   export AUTH_DB_USER="postgres";
   export AUTH_DB_PASSWORD="";
   export ISSUER="https://sso.rootservices.org"
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


[Kafka](https://kafka.apache.org/)
------
Shamelessy stolen from [kafka quickstart quide](https://kafka.apache.org/quickstart#quickstart_download).

[download kafka](https://kafka.apache.org/downloads)

un-tar it
```bash
$ tar -xzf kafka_2.11-0.11.0.1.tgz
$ cd kafka_2.11-0.11.0.1
```

start zookeeper.
```bash
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

start kafka server
```bash
$ bin/kafka-server-start.sh config/server.properties
```

create the topics, welcome and reset
```bash
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic welcome
$ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic reset
```

verify topic was created
```
$ bin/kafka-topics.sh --list --zookeeper localhost:2181
```