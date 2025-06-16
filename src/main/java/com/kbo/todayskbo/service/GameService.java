package com.kbo.todayskbo.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbo.todayskbo.dto.*;
import com.kbo.todayskbo.entity.Game;
import com.kbo.todayskbo.entity.GameRecord;
import com.kbo.todayskbo.entity.InningScore;
import com.kbo.todayskbo.entity.GameRheb;
import com.kbo.todayskbo.repository.GameRecordRepository;
import com.kbo.todayskbo.repository.GameRepository;
import com.kbo.todayskbo.repository.GameInningScoreRepository;
import com.kbo.todayskbo.repository.GameRhebRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameInningScoreRepository inningScoreRepository;
    private final GameRhebRepository rhebRepository;
    private final GameRecordRepository repository;
    private final ObjectMapper objectMapper;

    // ########################
    // kafka start
    // ########################
    @Transactional
    public void saveGame(GameDto dto) {
        log.error("service start");

        Game game = Game.builder()
                .gameId(dto.getGameId())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .gameDate(dto.getGameDate())
                .gameDateTime(dto.getGameDateTime())
                .stadium(dto.getStadium())
                .winner(dto.getWinner())
                .statusLabel(dto.getStatusLabel())
                .cancel(dto.isCancel())
                .homeTeamCode(dto.getHomeTeamCode())
                .homeTeamName(dto.getHomeTeamName())
                .homeTeamScore(dto.getHomeTeamScore())
                .awayTeamCode(dto.getAwayTeamCode())
                .awayTeamName(dto.getAwayTeamName())
                .awayTeamScore(dto.getAwayTeamScore())
                .statusCode(dto.getStatusCode())
                .statusInfo(dto.getStatusInfo())
                /*
                "BEFORE".equals(game.getStatusCode()) && game.isCancel() ? "취소" :
                "BEFORE".equals(game.getStatusCode()) && !game.isCancel() &&
                                game.getStatusInfo() != null && game.getStatusInfo().contains("회") ? "진행중" :
                "BEFORE".equals(game.getStatusCode()) && !game.isCancel() ? "예정" :
                "RESULT".equals(game.getStatusCode()) ? "종료" : "-"*/

                .homeStarterName(dto.getHomeStarterName())
                .awayStarterName(dto.getAwayStarterName())
                .winPitcherName(dto.getWinPitcherName())
                .losePitcherName(dto.getLosePitcherName())
                .homeCurrentPitcherName(dto.getHomeCurrentPitcherName())
                .awayCurrentPitcherName(dto.getAwayCurrentPitcherName())
                .weekday(dto.getWeekday())
                .build();
        gameRepository.save(game);


    }


    @Transactional
    public void saveInningGame(GameDto dto) {
        log.error("service start");
        List<String> homeInningScores = Optional.ofNullable(dto.getHomeTeamScoreByInning()).orElse(Collections.emptyList());
        List<String> awayInningScores = Optional.ofNullable(dto.getAwayTeamScoreByInning()).orElse(Collections.emptyList());

        for (int i = 0; i < 9; i++) {
            int homeScore = 0;
            int awayScore = 0;

            try {
                String homeVal = (homeInningScores.size() > i) ? homeInningScores.get(i) : null;
                if (homeVal != null && !homeVal.equals("-") && !homeVal.equalsIgnoreCase("null") && !homeVal.isBlank()) {
                    homeScore = Integer.parseInt(homeVal.trim());
                }
            } catch (Exception e) {
                System.err.println("⚠️ homeScore 변환 실패: " + homeInningScores.get(i));
            }

            try {
                String awayVal = (awayInningScores.size() > i) ? awayInningScores.get(i) : null;
                if (awayVal != null && !awayVal.equals("-") && !awayVal.equalsIgnoreCase("null") && !awayVal.isBlank()) {
                    awayScore = Integer.parseInt(awayVal.trim());
                }
            } catch (Exception e) {
                System.err.println("⚠️ awayScore 변환 실패: " + awayInningScores.get(i));
            }

            InningScore score = InningScore.builder()
                    .gameId(dto.getGameId())
                    .inning(i + 1)
                    .homeScore(homeScore)
                    .awayScore(awayScore)
                    .build();

            inningScoreRepository.save(score);

        }

        GameRheb homeRheb = GameRheb.builder()
                .gameId(dto.getGameId())
                .teamCode(dto.getHomeTeamCode())
                .isHome(dto.isHome())
                .run(dto.getHomeTeamRheb().get(0))
                .hit(dto.getHomeTeamRheb().get(1))
                .error(dto.getHomeTeamRheb().get(2))
                .baseOnBall(dto.getHomeTeamRheb().get(3))
                .build();
        rhebRepository.save(homeRheb);

        GameRheb awayRheb = GameRheb.builder()
                .gameId(dto.getGameId())
                .teamCode(dto.getAwayTeamCode())
                .isHome(dto.isHome())
                .run(dto.getAwayTeamRheb().get(0))
                .hit(dto.getAwayTeamRheb().get(1))
                .error(dto.getAwayTeamRheb().get(2))
                .baseOnBall(dto.getAwayTeamRheb().get(3))
                .build();
        rhebRepository.save(awayRheb);

    }

    @Transactional
    public void saveRecord(GameDto dto) {
        try {
            GameRecord entity = GameRecord.builder()
                    .gameId(dto.getGameId())
                    .etcRecordsJson(objectMapper.writeValueAsString(dto.getEtcRecords()))
                    .pitchingResultJson(objectMapper.writeValueAsString(dto.getPitchingResult()))
                    .teamPitchingBoxscoreJson(objectMapper.writeValueAsString(dto.getTeamPitchingBoxscore()))
                    .battersBoxscoreJson(objectMapper.writeValueAsString(dto.getBattersBoxscore()))
                    .scoreBoardJson(objectMapper.writeValueAsString(dto.getScoreBoard()))
                    .build();

            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ JSON 직렬화 실패", e);
        }
    }

    // ########################
    // kafka end
    // ########################


    public List<GameDtoResponse> getGamesByDate(LocalDate gameDate) {
        List<GameDtoResponse> games = gameRepository.findByGameDate(gameDate);

        return games.stream()
                .map(game -> GameDtoResponse.builder()
                        .gameId(game.getGameId())
                        .gameDate(game.getGameDate())
                        .weekday(game.getWeekday())
                        .stadium(game.getStadium())
                        .homeTeamScore(game.getHomeTeamScore())
                        .awayTeamScore(game.getAwayTeamScore())
                        .homeTeamName(game.getHomeTeamName())
                        .awayTeamName(game.getAwayTeamName())
                        .winner(game.getWinner())
                        .statusLabel(game.getStatusLabel())
                        .winPitcherName(game.getWinPitcherName())
                        .losePitcherName(game.getLosePitcherName())
                        .homeCurrentPitcherName(game.getHomeCurrentPitcherName())
                        .awayCurrentPitcherName(game.getAwayCurrentPitcherName())
                        .build())
                .collect(Collectors.toList());
    }


    public GameInningScoreResponse getGameInningById(String gameId) {

        List<InningScore> innings = inningScoreRepository.findByGameIdOrderByInningAsc(gameId);

        List<Integer> homeScores = innings.stream()
                .map(InningScore::getHomeScore)
                .collect(Collectors.toList());

        List<Integer> awayScores = innings.stream()
                .map(InningScore::getAwayScore)
                .collect(Collectors.toList());

        return GameInningScoreResponse.builder()
                .gameId(gameId)
                .homeTeamScoreByInning(homeScores)
                .awayTeamScoreByInning(awayScores)
                .build();
    }

    public List<GameRhebResponse> getGameRhebById(String gameId) {

        List<GameRheb> games = rhebRepository.findByGameId(gameId);
        return games.stream()
                .map(game -> GameRhebResponse.builder()
                        .gameId(game.getGameId())
                        .run(game.getRun())
                        .hit(game.getHit())
                        .error(game.getError())
                        .baseOnBall(game.getBaseOnBall())
                        .build())
                .collect(Collectors.toList());

    }

    public GameRecordResponse findByGameId(String gameId) {
        GameRecord record = repository.findByGameId(gameId)
                .orElseThrow(() -> new RuntimeException("❌ GameRecord not found: " + gameId));

        try {
            return GameRecordResponse.builder()
                    .etcRecords(objectMapper.readValue(record.getEtcRecordsJson(), List.class))
                    .teamPitchingBoxscore(objectMapper.readValue(record.getTeamPitchingBoxscoreJson(), Map.class))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ JSON 역직렬화 실패", e);
        }
    }

}