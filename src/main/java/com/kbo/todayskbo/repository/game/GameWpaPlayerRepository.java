package com.kbo.todayskbo.repository.game;

import com.kbo.todayskbo.domain.game.GamePitcherResult;
import com.kbo.todayskbo.domain.game.GameWpaPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameWpaPlayerRepository extends JpaRepository<GameWpaPlayer, Long> {
}
