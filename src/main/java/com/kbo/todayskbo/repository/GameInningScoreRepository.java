package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.entity.InningScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface GameInningScoreRepository extends JpaRepository<InningScore, Long> {

 //   List<Map<String, Object>> findByGameId(String gameId);
}