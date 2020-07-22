#!/bin/bash


# returns 1 if postgres is ready, 0 if it is not.
function isReady {
  local connection_url=$1
  local max_attempts=$2;
  local ready;

  for ((current_attempt = 0; current_attempt < max_attempts; current_attempt++)); do
    eval pg_isready -h $connection_url
    ready=$?

    echo "debug: db: $connection_url, ready: $ready"
    if [ "$ready" == "0" ]; then
      echo "debug: database host is ready"
      return 1
    fi

    echo "debug: waiting for database to start. attempt: $current_attempt"
    sleep 2;
  done
  return 0
}

# returns 1 if the database exists, 0 if it does not
function dbExists {
  local connection_url=$1
  local user=$2
  local db_name=$3

  exists="$( psql -h $connection_url -U $user -tAc "SELECT 1 FROM pg_database WHERE datname='$db_name'" )"
  if [ "$exists" == "1" ]; then
    echo "debug: database exists"
    return 1
  else
    echo "debug: database does not exist"
    return 0
  fi

}

# creates a database.
function createDb {
  local connection_url=$1
  local user=$2
  local db_name=$3

  eval psql -h $connection_url -U $user -tAc "create database $db_name"
}

# runs migrations, assumes this is executed in root directory of project.
function runMigrations {
  local connection_url=$1
  local user=$2
  local db_name=$3

  eval ./gradlew core:flywayMigrate -Dflyway.user=$user -Dflyway.password="" -Dflyway.url="jdbc:postgresql://$connection_url/$db_name"
}

function main {
  local connection_url=$1
  local max_attempts=$2;
  local user=$3
  local db_name=$4

  isReady "$connection_url" "$max_attempts"
  if [ "$?" = "1" ]; then
    echo "main: ready"
    dbExists "$connection_url" "$user" "$db_name"
    if [ "$?" = "0" ]; then
      echo "main: creating database"
      createDb "$connection_url" "$user" "$db_name"
    fi
    echo "main: running migrations"
    runMigrations "$connection_url" "$user" "$db_name"
    exit 0
  else
    echo "main: not ready"
    exit 1
  fi
}

connection_url="${AUTH_DB_HOST:-db}"
max_attempts="${DB_MAX_ATTEMPTS:-10}"
user="${AUTH_DB_USER:-postgres}"
db_name="${AUTH_DB_NAME:-auth}"

main "$connection_url" "$max_attempts" "$user" "$db_name"