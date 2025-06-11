package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.GameInningScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameInningScoreRepository extends JpaRepository<GameInningScore, Long> {
}
