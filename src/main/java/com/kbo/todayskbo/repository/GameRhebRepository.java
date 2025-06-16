package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.dto.GameDtoResponse;
import com.kbo.todayskbo.dto.GameInningScoreResponse;
import com.kbo.todayskbo.dto.GameRhebResponse;
import com.kbo.todayskbo.entity.GameRheb;
import com.kbo.todayskbo.entity.InningScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface GameRhebRepository extends JpaRepository<GameRheb, Long> {
    List<GameRheb> findByGameId(String gameId);
}