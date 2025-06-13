package com.kbo.todayskbo.kafkaTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbo.todayskbo.domain.game.*;
import com.kbo.todayskbo.domain.game.crawler.GameKafkaMessage;
import com.kbo.todayskbo.domain.team.Team;
import com.kbo.todayskbo.repository.game.*;
import com.kbo.todayskbo.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameInningConsumer {
    /*

    private final GameRepository gameRepository;
    private final GameHighlightPlayRepository gameHighlightPlayRepository;
    private final GameInningScoreRepository gameInningScoreRepository;
    private final GamePitcherResultRepository gamePitcherResultRepository;
    private final GameScheduleRepository gameScheduleRepository;
    private final GameTotalStatRepository gameTotalStatRepository;
    private final GameWpaPlayerRepository gameWpaPlayerRepository;
    private final HighlightRepository highlightRepository;
    private final TeamRepository teamRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = {"game-result-meta", "game-inning-scores", "game-total-stats" },
            groupId = "game-data-consumer-group-replay"
    )
    public void scoresConsume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("✅ 받은 메시지 ({}): {}", topic, message);

            GameKafkaMessage gameMessage = objectMapper.readValue(message, GameKafkaMessage.class);

            log.info("🏟️ 경기 정보: homeScore={}, awayScore={}, status={}",
                    gameMessage.getHomeScore(), gameMessage.getAwayScore(), gameMessage.getStatus());

            Long gameId = gameMessage.getGameId();

            String homeTeamName = gameMessage.getHomeTeamName();
            String awayTeamName = gameMessage.getAwayTeamName();

            if (homeTeamName == null || awayTeamName == null) {
                log.error("❌ 메시지에서 팀명이 누락됨: {}", message);
                return; // 혹은 throw
            }

            Team home = findOrCreateTeam(homeTeamName);
            Team away = findOrCreateTeam(awayTeamName);


            Game game = gameRepository.findById(gameId).orElse(null);

            log.info("📦 파싱된 메시지 gameId: {}", gameMessage.getGameId());
            if (gameMessage.getGameId() == null) {
                log.error("❌ gameId가 null입니다. 메시지: {}", message);
                return;
            }


            if ("game-result-meta".equals(topic)) {

                if (game == null) {
                    game = Game.builder()
                            .id(gameId)
                            .gameDate(gameMessage.getGameDate())
                            .weekday(gameMessage.getWeekday())
                            .homeTeam(home)
                            .awayTeam(away)
                            .homeScore(gameMessage.getHomeScore())
                            .awayScore(gameMessage.getAwayScore())
                            .status(gameMessage.getStatus())
                            .stadium(gameMessage.getStadium())
                            .build();
                } else {
                    // 필드 업데이트 (null 값이면 기존 값 유지)
                    if (gameMessage.getGameDate() != null) game.setGameDate(gameMessage.getGameDate());
                    if (gameMessage.getWeekday() != null) game.setWeekday(gameMessage.getWeekday());
                    if (gameMessage.getHomeScore() != null) game.setHomeScore(gameMessage.getHomeScore());
                    if (gameMessage.getAwayScore() != null) game.setAwayScore(gameMessage.getAwayScore());
                    if (gameMessage.getStatus() != null) game.setStatus(gameMessage.getStatus());
                    if (gameMessage.getStadium() != null) game.setStadium(gameMessage.getStadium());
                }
                gameRepository.save(game);

                GameSchedule schedule = GameSchedule.builder()
                        .gameDate(gameMessage.getGameDate())
                        .stadium(gameMessage.getStadium())
                        .homeTeam(home)
                        .awayTeam(away)
                        .status(gameMessage.getStatus())
                        .build();
                gameScheduleRepository.save(schedule);

                GameWpaPlayer wpaPlayer = GameWpaPlayer.builder()
                        .game(game)
                        .team(findOrCreateTeam(gameMessage.getTeamName()))
                        .build();
                gameWpaPlayerRepository.save(wpaPlayer);

                Highlight highlight = Highlight.builder()
                        .game(game)
                        .build();
                highlightRepository.save(highlight);

                GameHighlightPlay highlightPlay = GameHighlightPlay.builder()
                        .game(game)
                        .inning(gameMessage.getInning())
                        .build();
                gameHighlightPlayRepository.save(highlightPlay);

                GamePitcherResult pitcherResult = GamePitcherResult.builder()
                        .game(game)
                        .build();
                gamePitcherResultRepository.save(pitcherResult);

            } else if ("game-inning-scores".equals(topic)) {
                GameInningScore score = GameInningScore.builder()
                        .game(game)
                        .team(findOrCreateTeam(gameMessage.getTeamName()))
                        .inning(gameMessage.getInning())
                        .runs(gameMessage.getRuns())
                        .build();
                gameInningScoreRepository.save(score);

            } else if ("game-total-stats".equals(topic)) {
                GameTotalStat stat = GameTotalStat.builder()
                        .game(game)
                        .team(findOrCreateTeam(gameMessage.getTeamName()))
                        .statType(gameMessage.getType())
                        .value(gameMessage.getValue())
                        .build();
                gameTotalStatRepository.save(stat);

            } else {
                log.warn("⚠️ 알 수 없는 토픽: {}", topic);
            }

            log.info("✅ [{}] 저장 완료: gameId={}", topic, gameId);
        } catch (Exception e) {
            log.error("❌ Kafka 메시지 처리 실패: message={}, topic={}", message, topic, e);
        }
    }

    private Team findOrCreateTeam(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("❌ Team 이름이 null 또는 빈 문자열입니다!");
        }
        return teamRepository.findByName(name)
                .orElseGet(() -> teamRepository.save(Team.builder().name(name).build()));
    }
*/
}