#!/bin/sh

$KAFKA_HOME/bin/kafka-server-start.sh /data/config/server.properties &> /data/logs/kafka-server.log
