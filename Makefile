SHELL := /bin/bash

build:
	docker-compose -f docker-compose.yml -f docker-compose.server.yml build

run: build
	docker-compose -f docker-compose.yml -f docker-compose.server.yml up -d

stop:
	docker-compose -f docker-compose.yml -f docker-compose.server.yml stop

ps:
	docker-compose -f docker-compose.yml -f docker-compose.server.yml ps

build-dev:
	docker-compose -f docker-compose.yml build

migrate: build-dev
	docker-compose -f docker-compose.yml up -d;
	./sh/ready.sh tokensmith_db 10;
	docker exec -t tokensmith_db createdb -U postgres auth; exit 0;
	./gradlew core:flywayMigrate -Dflyway.user=postgres -Dflyway.password="" -Dflyway.url="jdbc:postgresql://127.0.0.1:5432/auth"

run-dev: migrate
