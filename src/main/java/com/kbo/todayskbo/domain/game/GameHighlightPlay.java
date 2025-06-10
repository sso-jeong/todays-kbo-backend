package com.kbo.todayskbo.domain.game;

import com.kbo.todayskbo.repository.game.GameHighlightPlayRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameHighlightPlay {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gameId")  // 외래키 이름 명시
    private Game game;

    private Long inning;
}
