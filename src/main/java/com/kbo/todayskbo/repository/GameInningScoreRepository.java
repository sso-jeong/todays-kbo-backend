package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.dto.GameDtoResponse;
import com.kbo.todayskbo.dto.GameInningScoreResponse;
import com.kbo.todayskbo.entity.InningScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface GameInningScoreRepository extends JpaRepository<InningScore, Long> {
    List<InningScore> findByGameIdOrderByInningAsc(String gameId);



}