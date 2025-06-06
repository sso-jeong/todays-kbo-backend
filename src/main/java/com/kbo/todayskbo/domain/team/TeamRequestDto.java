package com.kbo.todayskbo.domain.team;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequestDto {
    private String name;
    private String code;
    private String stadium;
    private String coach;
    private String championships;

    public Team toEntity() {
        return Team.builder()
                .name(name)
                .code(code)
                .stadium(stadium)
                .coach(coach)
                .championships(championships)
                .build();
    }
}