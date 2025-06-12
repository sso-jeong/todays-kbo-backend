package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.domain.game.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDto {
    private Long gameId;
    private String gameDate;
    private String weekday;
    private String status;
    private String stadium;
    private Integer homeScore;
    private Integer awayScore;
    private String homeTeamName;
    private String awayTeamName;
    private String teamName;
    private Integer inning;
    private Integer runs;
    private StatType statType;
    private Integer statValue;
    private List<TeamDetailDto> teams;
}