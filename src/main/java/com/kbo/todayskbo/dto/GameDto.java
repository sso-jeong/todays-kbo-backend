package com.kbo.todayskbo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class GameDto {

        private String gameId;
        private String superCategoryId;
        private String upperCategoryId;
        private String upperCategoryName;
        private String categoryId;
        private String categoryName;

        private LocalDate gameDate;
        private LocalDateTime gameDateTime;

        private boolean timeTbd;
        private String stadium;
        private String title;
        private String winner;

        private String homeTeamCode;
        private String homeTeamName;
        private Integer homeTeamScore;
        private List<String> homeTeamScoreByInning;
        private List<Integer> homeTeamRheb;
        private String homeTeamShortName;
        private String homeTeamFullName;
        private String homeTeamEmblemUrl;
        private String homeStarterName;
        private String homeCurrentPitcherName;

        private String awayTeamCode;
        private String awayTeamName;
        private Integer awayTeamScore;
        private List<String> awayTeamScoreByInning;
        private List<Integer> awayTeamRheb;
        private String awayTeamShortName;
        private String awayTeamFullName;
        private String awayTeamEmblemUrl;
        private String awayStarterName;
        private String awayCurrentPitcherName;

        private String winPitcherName;
        private String losePitcherName;

        private String statusCode;
        private Integer statusNum;
        private String statusInfo;

        private boolean cancel;
        private boolean suspended;
        private boolean hasVideo;
        private boolean gameOnAir;
        private boolean widgetEnable;
        private boolean emptyScoreBeforeResult;
        private boolean reversedHomeAway;
        private boolean scheduledTv;
        private boolean enablePreview;
        private boolean isHome;

        private String roundCode;
        private String roundName;
        private Integer roundGameNo;
        private Integer seriesGameNo;
        private String pool;
        private String broadChannel;
        private String info;
        private String generalTitle;
        private String generalInfo3;
        private String seasonCode;
        private Integer seasonYear;
        private Integer dheader;

        private Integer pv;
        private Integer cv;
        private Integer peak;
        private String ptsFlag;
        private String weekday;

        private GameCenterUrl gameCenterUrl;
        private CommentInfo commentInfo;

        // 내부 클래스들
        @Data
        public static class GameCenterUrl {
                private String baseUrl;
                private String cheerTabUrl;
        }

        @Data
        public static class CommentInfo {
                private String title;
                private String description;
                private String templateId;
                private String categoryLogo;
                private boolean hasTeamInfo;
                private Expose expose;

                @Data
                public static class Expose {
                        private boolean state;
                        private LocalDateTime endDateTime;
                }
        }
}
