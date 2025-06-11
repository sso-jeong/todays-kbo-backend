package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.repository.game.GamePitcherResultRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePitcherResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gameId")  // 외래키 이름 명시
    private Game game;
}
