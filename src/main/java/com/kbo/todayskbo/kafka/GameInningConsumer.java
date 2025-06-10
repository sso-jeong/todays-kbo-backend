package com.kbo.todayskbo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbo.todayskbo.domain.game.Game;
import com.kbo.todayskbo.domain.game.GameInningScore;
import  com.kbo.todayskbo.domain.game.crawler.GameKafkaMessage;
import com.kbo.todayskbo.domain.team.Team;
import com.kbo.todayskbo.repository.game.GameInningScoreRepository;
import com.kbo.todayskbo.repository.game.GameRepository;
import com.kbo.todayskbo.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameInningConsumer {

    //game-inning-scores
    // gameId, teamName, inning, runs, awayTeamName, homeTeamName
    private final GameRepository gameRepository;
    private final GameInningScoreRepository gameInningScoreRepository;
    private final TeamRepository teamRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "game-inning-scores", groupId = "game-inning-scores-consumer-group")
    public void scoresConsume(String message) {
        try {
            GameKafkaMessage gameMessage = objectMapper.readValue(message, GameKafkaMessage.class);

            Long gameId = gameMessage.getGameId();

            // 팀 조회 또는 생성
            Team home = findOrCreateTeam(gameMessage.getHomeTeamName());
            Team away = findOrCreateTeam(gameMessage.getAwayTeamName());

            // 이미 저장된 경기인지 확인
            Optional<Game> existing = gameRepository.findById(gameId);
          //  Optional<Game> existing2 = gameRepository.findByStatus(gameId);

// 추가 비교할거 필요함
            if (existing.isPresent() ) {
                log.info("⚠️ 이미 저장된 경기: {}", existing.get());
                return;
            }
            // Game 테이블: 없으면 생성
            Game game = gameRepository.findById(gameId)
                    .orElseGet(() -> gameRepository.save(Game.builder()
                            .id(gameId)
                            .homeTeam(home)
                            .awayTeam(away)
                            .build()));

            // GameInningScore 저장
            GameInningScore score = GameInningScore.builder()
                    .game(game)
                    .team(findOrCreateTeam(gameMessage.getTeamName()))
                    .inning(gameMessage.getInning())
                    .runs(gameMessage.getRuns())
                    .build();

            gameInningScoreRepository.save(score);

            log.info("✅ Game 및 GameInningScore 저장 완료: gameId={}", gameId);
            log.info("✅ Game 저장 완료: {}", game);

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패: {}", message, e);
        }
    }

    private Long findTeamIdByName(String name) {
        return teamRepository.findByName(name)
                .map(Team::getId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 팀 이름: " + name));
    }

    private Team findOrCreateTeam(String name) {
        return teamRepository.findByName(name)
                .orElseGet(() -> teamRepository.save(Team.builder().name(name).build()));
    }
}