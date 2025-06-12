package com.kbo.todayskbo.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class GameResponseDto {
    private Long id;
    private LocalDate gameDate;
    private String weekday;
    private String homeTeamName;
    private String awayTeamName;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private String stadium;
    private String inningStatus;
    private String highlightUrl;
}