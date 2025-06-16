package com.kbo.todayskbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameInningScoreResponse {

    private String gameId;
    private List<Integer> homeTeamScoreByInning;
    private List<Integer> awayTeamScoreByInning;

}
