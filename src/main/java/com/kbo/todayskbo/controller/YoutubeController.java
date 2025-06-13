package com.kbo.todayskbo.controller;

import com.kbo.todayskbo.domain.game.YoutubeVideoDto;
import com.kbo.todayskbo.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/highlights")
public class YoutubeController {

    private final YoutubeService youtubeService;

    @Value("${youtube.keywordSuffix}")
    private String suffix;

    @GetMapping
    public List<YoutubeVideoDto> search(
            @RequestParam String awayTeamName,
            @RequestParam String homeTeamName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate gameDate) throws IOException {


        String formattedDate = String.format("%d/%d", gameDate.getMonthValue(), gameDate.getDayOfMonth());
        String keyword = String.format("%s vs %s %s %s", awayTeamName, homeTeamName, formattedDate, suffix);

        return youtubeService.searchVideo(keyword);
    }

}