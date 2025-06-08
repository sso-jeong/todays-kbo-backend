package com.kbo.todayskbo.kafka;

import com.kbo.todayskbo.domain.game.GameKafkaMessage;
import com.kbo.todayskbo.domain.game.Game;
import com.kbo.todayskbo.domain.team.Team;
import com.kbo.todayskbo.repository.game.GameRepository;
import com.kbo.todayskbo.repository.team.TeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameKafkaConsumer {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "games", groupId = "game-consumer-group")
    public void consume(String message) {
        try {
            GameKafkaMessage gameMessage = objectMapper.readValue(message, GameKafkaMessage.class);

            // 팀 조회 또는 생성
            Team home = findOrCreateTeam(gameMessage.getHomeTeamName());
            Team away = findOrCreateTeam(gameMessage.getAwayTeamName());

            LocalDate gameDate = LocalDate.parse(gameMessage.getGameDate());

            // 이미 저장된 경기인지 확인
            Optional<Game> existing = gameRepository.findByGameDateAndHomeTeam_NameAndAwayTeam_Name(
                    gameDate, home.getName(), away.getName()
            );

            if (existing.isPresent()) {
                log.info("⚠️ 이미 저장된 경기: {}", existing.get());
                return;
            }

            Game game = Game.builder()
                    .gameDate(gameDate)
                    .weekday(gameMessage.getWeekday())
                    .homeTeam(home)
                    .awayTeam(away)
                    .homeScore(gameMessage.getHomeScore())
                    .awayScore(gameMessage.getAwayScore())
                    .status(gameMessage.getStatus())
                    .stadium(gameMessage.getStadium())
                    .inningStatus(gameMessage.getInningStatus())
                    .highlightUrl(gameMessage.getHighlightUrl())
                    .build();

            gameRepository.save(game);
            log.info("✅ Game 저장 완료: {}", game);

        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패: {}", message, e);
        }
    }

    private Team findOrCreateTeam(String name) {
        return teamRepository.findByName(name)
                .orElseGet(() -> teamRepository.save(Team.builder().name(name).build()));
    }
}