package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.GamePitcherResult;
import com.kbo.todayskbo.domain.game.GameSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameScheduleRepository extends JpaRepository<GameSchedule, Long> {
}
