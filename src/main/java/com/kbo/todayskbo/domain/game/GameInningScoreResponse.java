package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.domain.team.Team;
import lombok.*;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@Builder
public class GameInningScoreResponse {

    private Long id;

    private String game;

    private String team;

    private Long inning;

    private Long runs;
}