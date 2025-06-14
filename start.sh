#!/bin/bash

# Zookeeper 실행
echo "✅ Starting Zookeeper..."
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties > /tmp/zookeeper.log 2>&1 &

# Kafka 실행
sleep 5
echo "✅ Starting Kafka..."
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties > /tmp/kafka.log 2>&1 &

# Kafka UI 실행
sleep 10
echo "✅ Starting Kafka UI..."
java -jar /opt/kafka-ui/kafka-ui-api.jar > /tmp/kafka-ui.log 2>&1 &

# 대기 (컨테이너가 죽지 않도록)
tail -f /tmp/kafka-ui.log