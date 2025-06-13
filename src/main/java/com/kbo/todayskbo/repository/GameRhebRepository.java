package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.entity.GameRheb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface GameRhebRepository extends JpaRepository<GameRheb, Long> {

  //  List<Map<String, Object>> findByGameId(String gameId);
}