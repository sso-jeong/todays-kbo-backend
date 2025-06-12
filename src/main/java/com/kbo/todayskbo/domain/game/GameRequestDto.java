package com.kbo.todayskbo.domain.game;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GameRequestDto {
    private LocalDate gameDate;
    private String weekday;
    private Long homeTeamId;
    private Long awayTeamId;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private String inningStatus;
    private String highlightUrl;
}