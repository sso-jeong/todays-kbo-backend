package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.dto.GameDto;
import com.kbo.todayskbo.dto.GameDtoResponse;
import com.kbo.todayskbo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GameRepository extends JpaRepository<Game, String> {
    @Query("SELECT new com.kbo.todayskbo.dto.GameDtoResponse(" +
            "g.gameId, " +
            "g.gameDate, " +
            "g.stadium, " +
            "g.winner, " +
            "g.statusLabel, " +
            "g.homeTeamName, " +
            "g.homeTeamScore, " +
            "g.homeStarterName, " +
            "g.homeCurrentPitcherName, " +
            "g.awayTeamName, " +
            "g.awayTeamScore, " +
            "g.awayStarterName, " +
            "g.awayCurrentPitcherName, " +
            "g.winPitcherName, " +
            "g.losePitcherName, " +
            "g.cancel, " +
            "g.reversedHomeAway, " +
            "g.weekday" +
            ") " +
            "FROM Game g WHERE g.gameDate = :gameDate")
    List<GameDtoResponse> findByGameDate(LocalDate gameDate);


}