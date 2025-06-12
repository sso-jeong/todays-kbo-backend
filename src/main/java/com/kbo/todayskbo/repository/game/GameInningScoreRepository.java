package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.Game;
import com.kbo.todayskbo.domain.game.GameInningScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameInningScoreRepository extends JpaRepository<GameInningScore, Long> {

    List<GameInningScore> findByGameIdOrderByTeam_NameAscInningAsc(Long gameId);
}
