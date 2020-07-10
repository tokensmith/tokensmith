SHELL := /bin/bash

build:
	docker-compose build

migrate-dont-stop: build
	docker-compose up -d;
	./sh/ready.sh tokensmith_postgres 10;
	docker exec -t tokensmith_postgres createdb -U postgres auth; exit 0;
	./gradlew core:flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"


run: migrate-dont-stop

stop:
	docker-compose stop
