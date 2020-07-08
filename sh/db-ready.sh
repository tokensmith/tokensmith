#!/bin/bash

host=$1
pg_uri="postgres://postgres:postgres@$host:5432"

# make sure pg is ready to accept connections
until pg_isready -h postgres-host -p 5432 -U postgres
do
  echo "Waiting for postgres at: $pg_uri"
  sleep 2;
done