package com.kbo.todayskbo.repository;

import com.kbo.todayskbo.dto.GameRecordResponse;
import com.kbo.todayskbo.entity.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRecordRepository extends JpaRepository<GameRecord, Long> {
    Optional<GameRecord> findByGameId(String gameId);
}
