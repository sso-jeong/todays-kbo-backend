package com.kbo.todayskbo.domain.game;

import lombok.Data;

@Data
public class GameKafkaMessage {
    private String gameDate;        // 날짜
    private String weekday;         // 요일
    private String awayTeamName;
    private String homeTeamName;
    private Integer awayScore;
    private Integer homeScore;
    private String status;
    private String stadium;
    private String inningStatus;
    private String highlightUrl;
}