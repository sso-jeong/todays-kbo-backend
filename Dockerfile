FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive

# 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    wget curl gnupg openjdk-11-jdk unzip net-tools supervisor && \
    apt-get clean

# Kafka 다운로드 설정
ENV KAFKA_VERSION=3.7.0
ENV SCALA_VERSION=2.13
ENV KAFKA_HOME=/opt/kafka

# ✅ Kafka 설치 (아카이브에서 다운로드)
RUN mkdir -p ${KAFKA_HOME} && \
    wget https://archive.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -O /tmp/kafka.tgz && \
    tar -xzf /tmp/kafka.tgz --strip=1 -C ${KAFKA_HOME} && \
    rm /tmp/kafka.tgz


# 실행 스크립트 추가
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 2181 9092 9094 8080

CMD ["/start.sh"]
