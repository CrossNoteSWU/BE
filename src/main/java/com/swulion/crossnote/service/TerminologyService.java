package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Curation.AiGeneratedContentDto;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class TerminologyService {

    // 전문 용어 사전
    private static final Set<String> TECH_TERMS = new HashSet<>(Arrays.asList(
            "알고리즘", "데이터베이스", "인공지능", "머신러닝", "딥러닝",
            "클라우드", "블록체인", "유전체", "양자", "통계학"
    ));

    /*
     * 텍스트 내 전문 용어 농도를 계산
     * @param text AI가 생성한 텍스트
     * @return 0.0 ~ 1.0
     */
    public double calculateTerminologyDensity(String text) {
        if (text == null || text.isBlank()) return 0.0;

        String[] words = text.split("\\s+");
        long termCount = Arrays.stream(words)
                .filter(TECH_TERMS::contains)
                .count();

        return (double) termCount / words.length;
    }

    /*
     * 전문 용어 농도 기반 Level 결정
     * @param text AI가 생성한 텍스트
     * @return LEVEL_1(기초) 또는 LEVEL_2(심화)
     */
    public CurationLevel determineCurationLevel(String text) {
        double density = calculateTerminologyDensity(text);
        log.info("전문 용어 농도: {}", density);

        // 예시 기준: 0.05 이상이면 LEVEL_2, 아니면 LEVEL_1
        if (density >= 0.05) {
            return CurationLevel.LEVEL_2;
        } else {
            return CurationLevel.LEVEL_1;
        }
    }

    /*
     * AiGeneratedContentDto에 Level 반영
     */
    public AiGeneratedContentDto assignLevel(AiGeneratedContentDto contentDto) {
        CurationLevel level = determineCurationLevel(contentDto.getDescription());
        contentDto.setCurationLevel(level);
        return contentDto;
    }
}
