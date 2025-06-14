FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# 기본 패키지 설치
RUN apt-get update && apt-get install -y \
    wget curl gnupg openjdk-11-jdk unzip net-tools supervisor

# Kafka 및 Zookeeper 설치
RUN mkdir -p /opt/kafka && \
    wget -qO- https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz | tar -xz --strip=1 -C /opt/kafka

# Kafka UI 설치
RUN mkdir -p /opt/kafka-ui && \
    wget -q https://github.com/provectus/kafka-ui/releases/download/v0.7.2/kafka-ui-0.7.2.zip && \
    unzip kafka-ui-0.7.2.zip -d /opt/kafka-ui && \
    rm kafka-ui-0.7.2.zip

# 설정 파일 및 실행 스크립트 복사
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 2181 9092 9094 8080

CMD ["/start.sh"]