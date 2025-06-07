package com.kbo.todayskbo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MyKafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "test-group-v2")
    public void listen(String message) {
        log.info("✅ KafkaConsumer 받은 메시지: {}", message);

    }
}