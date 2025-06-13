package com.kbo.todayskbo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbo.todayskbo.dto.GameDto;
import com.kbo.todayskbo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {

    private final GameService gameService;

    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> save(@RequestBody GameDto dto) {
        gameService.saveGame(dto);
        return ResponseEntity.ok("✅ Game saved successfully");
    }

    @KafkaListener(topics = "game-result-meta2", groupId = "kbo-consumer")
    public void listenFromKafka(String message) {
        try {
            GameDto dto = objectMapper.readValue(message, GameDto.class);
            gameService.saveGame(dto);
        } catch (Exception e) {
            System.err.println("❌ game-result-meta2 Kafka 메시지 처리 실패: " + e.getMessage());
            e.printStackTrace();  // 🔥 상세 에러 로그 출력
        }
    }

    @KafkaListener(topics = "related-games", groupId = "kbo-consumer")
    public void listenRelatedGames(String message) {
        try {
            GameDto dto = objectMapper.readValue(message, GameDto.class);
            gameService.saveInningGame(dto);
        } catch (Exception e) {
            System.err.println("❌ related-games Kafka 메시지 처리 실패: " + e.getMessage());
            e.printStackTrace();  // 🔥 상세 에러 로그 출력
        }
    }

    @GetMapping("/{gameDate}")
    public ResponseEntity<List<GameDto>> getGames(@PathVariable String gameDate) {
        LocalDate parsedDate = LocalDate.parse(gameDate);
        return ResponseEntity.ok(gameService.getGamesByDate(parsedDate));
    }

    /*
    @GetMapping("/detail/{gameId}")
    public ResponseEntity<GameDto> getGameDetail(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameDetail(gameId));
    }*/
}

/*
@GetMapping("/{gameDate}")
public ResponseEntity<List<GameResponseDto>> getGames(@PathVariable String gameDate) {
    LocalDate parsedDate = LocalDate.parse(gameDate);
    return ResponseEntity.ok(gameService.getGamesByDate(parsedDate));
}

@GetMapping("/detail/{gameId}")
public ResponseEntity<GameDetailDto> getGameDetail(@PathVariable Long gameId) {
    return ResponseEntity.ok(gameService.getGameDetail(gameId));
}
*/
