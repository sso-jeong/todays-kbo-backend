package com.kbo.todayskbo.service;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.kbo.todayskbo.domain.game.YoutubeVideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    public List<YoutubeVideoDto> searchVideo(String keyword) throws IOException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                jsonFactory,
                request -> {})
                .setApplicationName("kbo-highlights")
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey))
                .build();

        YouTube.Search.List search = youtube.search()
                .list("id,snippet");
        search.setQ(keyword);
        search.setMaxResults(5L);
        search.setType("video");

        SearchListResponse response = search.execute();
        List<SearchResult> items = response.getItems();

        List<YoutubeVideoDto> results = new ArrayList<>();
        for (SearchResult item : items) {
            String videoId = item.getId().getVideoId();
            String title = item.getSnippet().getTitle();
            //String thumbnail = item.getSnippet().getThumbnails().getDefault().getUrl();
            String thumbnail = null;
            if (item.getSnippet().getThumbnails() != null &&
                    item.getSnippet().getThumbnails().getDefault() != null) {
                thumbnail = item.getSnippet().getThumbnails().getDefault().getUrl();
            }

            String link = "https://www.youtube.com/watch?v=" + videoId;

            results.add(new YoutubeVideoDto(videoId, title, link, thumbnail));
        }

        return results;
    }
}
