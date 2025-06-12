package com.kbo.todayskbo.controller;

import com.kbo.todayskbo.domain.game.GameDetailDto;
import com.kbo.todayskbo.domain.game.GameResponseDto;
import com.kbo.todayskbo.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/{gameDate}")
    public ResponseEntity<List<GameResponseDto>> getGames(@PathVariable String gameDate) {
        LocalDate parsedDate = LocalDate.parse(gameDate);
        return ResponseEntity.ok(gameService.getGamesByDate(parsedDate));
    }

    @GetMapping("/detail/{gameId}")
    public ResponseEntity<GameDetailDto> getGameDetail(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameDetail(gameId));
    }

}
