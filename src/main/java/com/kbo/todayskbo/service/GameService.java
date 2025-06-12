package com.kbo.todayskbo.service;

import com.kbo.todayskbo.domain.game.*;
import com.kbo.todayskbo.domain.team.Team;
import com.kbo.todayskbo.repository.game.GameInningScoreRepository;
import com.kbo.todayskbo.repository.game.GameRepository;
import com.kbo.todayskbo.repository.game.GameStatRepository;
import com.kbo.todayskbo.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class GameService {

        private final GameRepository gameRepository;
        private final GameInningScoreRepository gameInningScoreRepository;
        private final TeamRepository teamRepository;

        private final GameStatRepository gameStatRepository;

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
        }
}
