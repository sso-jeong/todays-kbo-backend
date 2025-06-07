package com.kbo.todayskbo.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final MyKafkaProducer producer;

    @PostMapping("/send")
    public void send(@RequestParam String message) {
        producer.send("test-topic", message);
    }
}