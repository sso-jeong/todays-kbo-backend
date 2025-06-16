package com.kbo.todayskbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRhebResponse {

    private String gameId;
    private int run;
    private int hit;
    private int error;
    private int baseOnBall;
}
