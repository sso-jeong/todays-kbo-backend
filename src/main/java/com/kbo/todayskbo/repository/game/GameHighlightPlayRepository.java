package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.Game;
import com.kbo.todayskbo.domain.game.GameHighlightPlay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameHighlightPlayRepository extends JpaRepository<GameHighlightPlay, Long> {
}
