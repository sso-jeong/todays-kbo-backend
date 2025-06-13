package com.kbo.todayskbo.domain.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YoutubeVideoDto {
    private String videoId;
    private String title;
    private String link;
    private String thumbnail;
}
