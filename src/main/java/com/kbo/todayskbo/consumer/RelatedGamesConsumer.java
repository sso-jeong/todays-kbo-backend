package com.kbo.todayskbo.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbo.todayskbo.dto.GameDto;
import com.kbo.todayskbo.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelatedGamesConsumer {

    private final ObjectMapper objectMapper;
    private final GameService gameService;

    @KafkaListener(topics = "related-games", groupId = "kbo-consumer")
    public void consume(String message) {
        try {
            GameDto dto = objectMapper.readValue(message, GameDto.class);
            gameService.saveGame(dto);
            log.info("✅ Game saved from related-games: {}", dto.getGameId());
        } catch (Exception e) {
            log.error("❌ Kafka consume error [related-games]", e);
        }
    }
}