package com.kbo.todayskbo.service;


import com.kbo.todayskbo.dto.GameDto;
import com.kbo.todayskbo.dto.GameDtoResponse;
import com.kbo.todayskbo.entity.Game;
import com.kbo.todayskbo.entity.InningScore;
import com.kbo.todayskbo.entity.GameRheb;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameInningScoreRepository inningScoreRepository;
    private final GameRhebRepository rhebRepository;

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
                        .build())
                .collect(Collectors.toList());
    }




}

        /*
        public List<GameResponseDto> getGamesByDate(LocalDate date) {
            List<Game> games = gameRepository.findByGameDate(date);

            return games.stream()
                    .map(game -> GameResponseDto.builder()
                            .id(game.getId())
                            .gameDate(game.getGameDate())
                            .weekday(game.getWeekday())
                            .homeTeamName(game.getHomeTeam().getName())
                            .awayTeamName(game.getAwayTeam().getName())
                            .homeScore(game.getHomeScore())
                            .awayScore(game.getAwayScore())
                            .status(game.getStatus())
                            .stadium(game.getStadium())
                            .build())
                    .collect(Collectors.toList());
        }


        public GameDetailDto getGameDetail(Long gameId) {
            List<Map<String, Object>> rows = gameStatRepository.findFullGameDataByGameId(gameId);

            if (rows.isEmpty()) {
                throw new IllegalArgumentException("해당 gameId에 대한 데이터가 없습니다.");
            }

            Map<String, Object> firstRow = rows.get(0);
            GameDetailDto.GameDetailDtoBuilder gameBuilder = GameDetailDto.builder()
                    .gameId(((Number) firstRow.get("gameId")).longValue())
                    .gameDate((String) firstRow.get("gameDate"))
                    .weekday((String) firstRow.get("weekday"))
                    .status((String) firstRow.get("status"))
                    .stadium((String) firstRow.get("stadium"))
                    .homeScore((Integer) firstRow.get("homeScore"))
                    .awayScore((Integer) firstRow.get("awayScore"))
                    .homeTeamName((String) firstRow.get("homeTeamName"))
                    .awayTeamName((String) firstRow.get("awayTeamName"));

            Map<String, TeamDetailDto> teamMap = new LinkedHashMap<>();

            for (Map<String, Object> row : rows) {
                String teamName = (String) row.get("teamName");
                String statType = row.get("statType").toString();
                int statValue = ((Number) row.get("statValue")).intValue();
                int inning = ((Number) row.get("inning")).intValue();
                int runs = ((Number) row.get("runs")).intValue();

                TeamDetailDto team = teamMap.computeIfAbsent(teamName, name -> TeamDetailDto.builder()
                        .teamName(name)
                        .totalStats(new HashMap<>())
                        .innings(new ArrayList<>())
                        .build());

                team.getTotalStats().put(statType, statValue); // statType: R, H, E, B

                // 이닝별 점수는 이닝 번호 기준으로 1번만 넣기
                boolean alreadyExists = team.getInnings().stream().anyMatch(i -> i.getInning() == inning);
                if (!alreadyExists) {
                    team.getInnings().add(InningScoreDto.builder().inning(inning).runs(runs).build());
                }
            }

            gameBuilder.teams(new ArrayList<>(teamMap.values()));
            return gameBuilder.build();
        }*/
