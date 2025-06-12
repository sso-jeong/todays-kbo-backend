package com.kbo.todayskbo.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDetailResponse {
    private Long gameId;
    private String gameDate;
    private String weekday;
    private String homeTeamName;
    private String awayTeamName;
    private List<TeamDetailDto> teams;
}