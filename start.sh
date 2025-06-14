#!/bin/bash

# Zookeeper 실행
echo "✅ Starting Zookeeper..."
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties > /tmp/zookeeper.log 2>&1 &

# Kafka 실행
sleep 5
echo "✅ Starting Kafka..."
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties > /tmp/kafka.log 2>&1 &