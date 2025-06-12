package com.kbo.todayskbo.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamDetailDto {
    private String teamName;
    private Map<String, Integer> totalStats;
    private List<InningScoreDto> innings;
}