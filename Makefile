SHELL := /bin/bash

build:
	docker-compose build

migrate-dont-stop: ready
	docker-compose up -d;
	./ready.sh postgres 10;
	docker exec -t postgres createdb -U postgres auth; exit 0;
	./gradlew core:flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"


run: migrate-dont-stop

stop:
	docker-compose stop
