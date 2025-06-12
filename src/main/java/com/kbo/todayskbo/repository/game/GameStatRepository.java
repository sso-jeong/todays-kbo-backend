package com.kbo.todayskbo.repository.game;


import com.kbo.todayskbo.domain.game.GameTotalStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface GameStatRepository extends JpaRepository<GameTotalStat, Long> {
    @Query(value = "SELECT g.id AS gameId, DATE_FORMAT(g.gameDate, '%Y-%m-%d') AS gameDate," +
            "g.weekday AS weekday, g.status, g.stadium, g.homeScore, g.awayScore," +
            "ht.name AS homeTeamName, at.name AS awayTeamName, t.name AS teamName," +
            "gis.inning AS inning,  gis.runs AS runs, gts.statType AS statType, gts.value AS statValue" +
            " FROM Game g" +
            " JOIN Team ht ON g.homeTeamId = ht.id" +
            " JOIN Team at ON g.awayTeamId = at.id" +
            " JOIN GameInningScore gis ON g.id = gis.gameId" +
            " JOIN gameTotalStat gts ON g.id = gts.gameId AND gis.teamId = gts.teamId" +
            " JOIN team t ON gis.teamId = t.id" +
            " WHERE g.id = :gameId ORDER BY t.name ASC, gis.inning ASC", nativeQuery = true)
    List<Map<String, Object>> findFullGameDataByGameId(@Param("gameId") Long gameId);
}
