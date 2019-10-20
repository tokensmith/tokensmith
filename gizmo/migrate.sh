#!/usr/bin/env bash

./gradlew -x test clean build
./gradlew flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"

