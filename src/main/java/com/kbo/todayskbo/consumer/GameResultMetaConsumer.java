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
public class GameResultMetaConsumer {

    private final ObjectMapper objectMapper;
    private final GameService gameService;


    @KafkaListener(topics = "game-result-meta2", groupId = "kbo-consumer", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        log.error("\n\n\n\n\n\n#############mm:" + message);
        try {
            GameDto dto = objectMapper.readValue(message, GameDto.class);
            gameService.saveGame(dto);
            log.info("✅ Game saved from game-result-meta3: {}", dto.getGameId());
        } catch (Exception e) {
            log.error("❌ Failed to consume game-result-meta3", e);
        }
    }
}
