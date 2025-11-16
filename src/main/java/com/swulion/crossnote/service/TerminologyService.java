package com.swulion.crossnote.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TerminologyService {

    // categoryId -> set of terms (Long key)
    private final Map<Long, Set<String>> termDictByCategory = new HashMap<>();

    // 임계값: 용어 개수 기준 또는 비율(둘 중 하나 만족하면 LEVEL_2)
    private static final double DENSITY_THRESHOLD = 0.02; // 2%
    private static final int ABS_TERM_COUNT_THRESHOLD = 2; // 전문용어 최소 2개 이상 등장 시 LEVEL_2

    @PostConstruct
    public void init() {
        try {
            loadDictionariesFromResources();
            log.info("Terminology dictionaries loaded for {} categories", termDictByCategory.size());
        } catch (Exception e) {
            log.error("전문용어 사전 로드 실패", e);
        }
    }

    private void loadDictionariesFromResources() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:terminology/*.txt");

        for (Resource res : resources) {
            String filename = Objects.requireNonNull(res.getFilename()); // e.g. "7_philosophy.txt"
            // parse id from filename
            String idStr = filename.split("_")[0];
            long categoryId;
            try {
                categoryId = Long.parseLong(idStr);
            } catch (NumberFormatException ex) {
                log.warn("Skipping terms file with unexpected name: {}", filename);
                continue;
            }

            Set<String> terms = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // 쉼표가 아닌 줄 바꿈 기준
                    String normalized = normalizeTerm(line);
                    if (!normalized.isEmpty()) terms.add(normalized);
                }
            }
            termDictByCategory.put(categoryId, terms);
            log.info("Loaded {} terms for categoryId={} (file={})", terms.size(), categoryId, filename);
        }
    }

    // 단순 정규화: 공백/구두점 제거 + toLowerCase (매칭을 위해)
    private String normalizeTerm(String t) {
        if (t == null) return "";
        return t.trim().replaceAll("[\\p{Punct}]+", "").toLowerCase();
    }

    /*
     * 원문 텍스트 내 전문 용어 등장 횟수와 농도를 계산
     * @param sourceText 원문 텍스트
     * @param categoryIds 분석에 사용할 모든 카테고리 ID 목록 (메인 + 크로스)
     * @return TermCountResult (용어 개수와 밀도)
     */
    public TermCountResult analyzeSourceText(String sourceText, List<Long> categoryIds) {
        if (sourceText == null || sourceText.isBlank() || categoryIds.isEmpty())
            return new TermCountResult(0, 0);

        // 1. 텍스트 정규화 및 토큰 수 계산
        String normalizedText = sourceText.toLowerCase();

        // 띄어쓰기를 기준으로 전체 단어 수를 계산
        String[] tokens = normalizedText.split("\\s+");
        int tokenCount = Math.max(tokens.length, 1);

        // 2. 검색할 용어 집합 생성
        Set<String> termsToCheck = new HashSet<>();
        for (Long id : categoryIds) {
            termsToCheck.addAll(termDictByCategory.getOrDefault(id, Collections.emptySet()));
        }

        // 3. 용어 등장 횟수 카운트 로직
        int foundTermCount = 0;
        for (String term : termsToCheck) {
            if (term.isEmpty()) continue;

            String normalizedTerm = normalizeTerm(term);

            int lastIndex = 0;
            while(lastIndex != -1){
                lastIndex = normalizedText.indexOf(normalizedTerm, lastIndex);
                if(lastIndex != -1){
                    foundTermCount++; // 등장 횟수 증가
                    lastIndex += normalizedTerm.length(); // 다음 검색 위치로 이동
                }
            }
        }

        double density = (double) foundTermCount / tokenCount;
        log.info("Analysis: Found {} terms, Total tokens: {}, Density: {}", foundTermCount, tokenCount, density);
        return new TermCountResult(foundTermCount, density);
    }

    // helper
    public static class TermCountResult {
        private final int count;
        private final double density;
        public TermCountResult(int count, double density) {
            this.count = count;
            this.density = density;
        }
        public int getCount() { return count; }
        public double getDensity() { return density; }
    }
}