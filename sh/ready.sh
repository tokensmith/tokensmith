#!/bin/bash

container_name=$1
declare -i max_attempts=$2;

for ((current_attempt = 0; current_attempt < max_attempts; current_attempt++)); do
  ready=`docker inspect -f {{.State.Running}} $container_name`
  if [ -z "$ready" ]; then
    ready=false
  fi

  echo "container: $container_name, ready: $ready"
  if [ $ready == true ]; then
    break;
  fi

  echo "waiting for database to start. attempt: $current_attempt"
  sleep 2;
done