#!/usr/bin/env bash

psql -c 'drop database auth;' -U postgres
psql -c 'create database auth;' -U postgres
mvn clean package -DskipTests;
mvn flyway:migrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth" -Dflyway.initOnMigrate=true;
