package com.kbo.todayskbo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDtoResponse {

    private String gameId;
    private LocalDate gameDate;
    private String stadium;
    private String winner;
    private String statusLabel;

    private String homeTeamName;
    private Integer homeTeamScore;
    private String homeStarterName;
    private String homeCurrentPitcherName;

    private String awayTeamName;
    private Integer awayTeamScore;
    private String awayStarterName;
    private String awayCurrentPitcherName;

    private String winPitcherName;
    private String losePitcherName;

    private boolean cancel;
    private boolean isHome;

    private String weekday;

}
