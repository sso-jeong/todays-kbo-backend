package com.kbo.todayskbo.domain.team;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String code;

    @Column(length = 50)
    private String stadium;

    @Column(length = 50)
    private String coach;

    @Lob
    private String championships;
}
