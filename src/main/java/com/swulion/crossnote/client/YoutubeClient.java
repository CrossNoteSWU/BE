package com.swulion.crossnote.client;

import com.swulion.crossnote.config.ApiKeys;
import com.swulion.crossnote.dto.Curation.CurationSourceDto;
import com.swulion.crossnote.dto.Curation.YoutubeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service("youtubeClient") // (Bean 이름 지정)
@RequiredArgsConstructor
public class YoutubeClient implements CurationSourceClient {

    private final RestTemplate restTemplate;
    private final ApiKeys apiKeys;
    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v=";

    @Override
    public CurationSourceDto fetchSource(String query) {

        URI uri = UriComponentsBuilder.fromUriString(YOUTUBE_API_URL)
                .queryParam("key", apiKeys.getYoutube()) // (API 키)
                .queryParam("q", query + " 강의") // (검색어 + "강의" 키워드 추가)
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("videoDefinition", "high") // (고화질 우선)
                .queryParam("maxResults", 1)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        try {
            ResponseEntity<YoutubeResponseDto> response = restTemplate.getForEntity(
                    uri, YoutubeResponseDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<YoutubeResponseDto.Item> items = response.getBody().getItems();
                if (!items.isEmpty()) {
                    YoutubeResponseDto.Item item = items.get(0);
                    String title = item.getSnippet().getTitle();
                    String description = item.getSnippet().getDescription();
                    String videoId = item.getId().getVideoId();

                    return CurationSourceDto.builder()
                            .originalText(title + ". " + description)
                            .sourceUrl(YOUTUBE_WATCH_URL + videoId)
                            .imageUrl(item.getSnippet().getThumbnailUrl())
                            .build();
                }
            }
            log.warn("YouTube API 응답에 아이템이 없습니다. 쿼리: {}", query);
            return null;

        } catch (Exception e) {
            log.error("YouTube API 호출 실패: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getSourceType() {
        return "YOUTUBE";
    }
}