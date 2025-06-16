package com.kbo.todayskbo.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String etcRecordsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String pitchingResultJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String teamPitchingBoxscoreJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String battersBoxscoreJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String scoreBoardJson;
}
