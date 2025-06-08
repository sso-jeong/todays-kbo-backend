package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameDateAndHomeTeam_NameAndAwayTeam_Name(LocalDate gameDate, String homeTeamName, String awayTeamName);
}
