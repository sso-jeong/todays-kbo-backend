package com.kbo.todayskbo.domain.team;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponseDto {
    private Long id;
    private String name;
    private String code;
    private String stadium;
    private String coach;
    private String championships;

    public static TeamResponseDto fromEntity(Team team) {
        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .code(team.getCode())
                .stadium(team.getStadium())
                .coach(team.getCoach())
                .championships(team.getChampionships())
                .build();
    }
}