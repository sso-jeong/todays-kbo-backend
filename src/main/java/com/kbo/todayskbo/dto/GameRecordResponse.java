package com.kbo.todayskbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRecordResponse {
    private List<Map<String, String>> etcRecords;
    private Map<String, Object> teamPitchingBoxscore;
}
