package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GameRepository extends JpaRepository<Game, String> {

    List<Game> findByGameDate(LocalDate gameDate);

}