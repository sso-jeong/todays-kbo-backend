package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.Game;
import com.kbo.todayskbo.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findById(Long id);
    Optional<Game> findByStatus(String status);

    Optional<Game> findByGameDateAndHomeTeam_NameAndAwayTeam_Name(LocalDate gameDate, String homeTeamName, String awayTeamName);
}
