package com.kbo.todayskbo.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class Game {

    @Id
    private String gameId;


    private String categoryId;
    private String categoryName;
    private LocalDate gameDate;
    private LocalDateTime gameDateTime;
    private String stadium;

    private String homeTeamCode;
    private String homeTeamName;
    private Integer homeTeamScore;

    private String awayTeamCode;

    private String awayTeamName;
    private Integer awayTeamScore;

    private String winner;
    private String statusCode;
    private String statusInfo;

    private boolean cancel;
    private boolean suspended;
    private boolean hasVideo;
    private boolean gameOnAir;

    private String roundCode;
    private boolean reversedHomeAway;
    private String homeStarterName;

    private String awayStarterName;

    private String winPitcherName;
    private String losePitcherName;
    private String homeCurrentPitcherName;

    private String awayCurrentPitcherName;

    private String stadiumBroadcastChannel;

    private String weekday;
}
