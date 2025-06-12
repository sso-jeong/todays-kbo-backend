package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.domain.team.Team;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameInningScore {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gameId")  // 외래키 이름 명시
    private Game game;

    @ManyToOne
    @JoinColumn(name = "teamId")  // 외래키 이름 명시
    private Team team;

    private Long inning;

    private Long runs;
}