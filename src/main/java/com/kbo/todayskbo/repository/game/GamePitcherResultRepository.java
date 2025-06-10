package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.GameHighlightPlay;
import com.kbo.todayskbo.domain.game.GamePitcherResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePitcherResultRepository extends JpaRepository<GamePitcherResult, Long> {
}
