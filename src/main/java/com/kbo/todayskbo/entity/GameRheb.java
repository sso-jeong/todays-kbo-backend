package com.kbo.todayskbo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class GameRheb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameId;
    private boolean isHome;

    private String teamCode;

    private int run;
    private int hit;
    private int error;
    private int baseOnBall;
}
