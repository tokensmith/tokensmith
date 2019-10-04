#!/bin/sh

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &> /data/logs/zookeeper.log
