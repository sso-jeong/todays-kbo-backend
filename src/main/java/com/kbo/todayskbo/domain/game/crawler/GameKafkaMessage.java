package com.kbo.todayskbo.domain.game.crawler;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;

@Data
public class GameKafkaMessage {

    // common
    private Long gameId;
    private String awayTeamName;
    private String homeTeamName;

    //1. game-inning-scores gameId, teamName, inning, runs, awayTeamName, homeTeamName
    private String teamName;
    private Long inning;
    private Long runs;

    //2. game-result-meta
    private LocalDate gameDate;
    private String stadium;
    private Integer homeScore;
    private Integer awayScore;
    private String winner;
    private String loser;
    private String save;

    // 3. game-total-stats
    private String type;
    private Integer value;


}