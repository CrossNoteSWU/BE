package com.swulion.crossnote.dto.Curation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // (필요 없는 필드는 무시)
public class YoutubeResponseDto {

    private List<Item> items;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private Id id;
        private Snippet snippet;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Id {
        private String videoId;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        private String title;
        private String description;
        private Map<String, Thumbnail> thumbnails;

        public String getThumbnailUrl() {
            if (thumbnails != null) {
                if (thumbnails.containsKey("high")) return thumbnails.get("high").getUrl();
                if (thumbnails.containsKey("medium")) return thumbnails.get("medium").getUrl();
                if (thumbnails.containsKey("default")) return thumbnails.get("default").getUrl();
            }
            return null;
        }
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        private String url;
    }
}