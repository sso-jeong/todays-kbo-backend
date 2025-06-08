package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.domain.team.Team;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate gameDate;

    private String weekday;

    @ManyToOne
    @JoinColumn(name = "homeTeamId")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "awayTeamId")
    private Team awayTeam;

    private Integer homeScore;
    private Integer awayScore;

    private String stadium;

    private String status;
    private String inningStatus;

    @Lob
    private String highlightUrl;
}
