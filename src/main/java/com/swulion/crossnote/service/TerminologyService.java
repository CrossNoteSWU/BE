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
            log.error("Failed to load terminology dictionaries", e);
        }
    }

    private void loadDictionariesFromResources() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:terminology/*.txt");

        for (Resource res : resources) {
            String filename = Objects.requireNonNull(res.getFilename()); // e.g. "7_philosophy.txt.txt"
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
                    String normalized = normalizeTerm(line);
                    if (!normalized.isEmpty()) terms.add(normalized);
                }
            }
            termDictByCategory.put(categoryId, terms);
            log.info("Loaded {} terms for categoryId={} (file={})", terms.size(), categoryId, filename);
        }
    }

    // 단순 정규화: 공백/구두점 제거 + toLowerCase
    private String normalizeTerm(String t) {
        if (t == null) return "";
        return t.trim().replaceAll("[\\p{Punct}\\p{IsSpace}]+", "").toLowerCase();
    }

    /**
     * 텍스트 내 전문 용어 개수를 센다. (substring 매칭 방식)
     * - categoryId가 null이면 모든 카테고리(모든 용어)로 체크 가능
     */
    public TermCountResult countTermsInText(String text, Long categoryId) {
        if (text == null || text.isBlank()) return new TermCountResult(0, 0);

        String normalizedText = text.toLowerCase();
        // 간단 방어: Q&A 등 텍스트 길이
        String[] tokens = normalizedText.split("\\s+");
        int tokenCount = Math.max(tokens.length, 1);

        Set<String> termsToCheck;
        if (categoryId == null) {
            termsToCheck = termDictByCategory.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
        } else {
            termsToCheck = termDictByCategory.getOrDefault(categoryId, Collections.emptySet());
        }

        int found = 0;
        for (String term : termsToCheck) {
            if (term.isEmpty()) continue;
            if (normalizedText.contains(term)) {
                found++;
            }
        }

        double density = (double) found / tokenCount;
        return new TermCountResult(found, density);
    }

    /**
     * Step 2: 원문 텍스트의 전문 용어 농도를 계산하여 난이도를 객관적으로 분석합니다.
     * @param sourceText 원문 텍스트
     * @param categoryIds 분석에 사용할 모든 카테고리 ID 목록 (메인 + 크로스)
     * @return TermCountResult (용어 개수와 밀도)
     */
    public TermCountResult analyzeSourceText(String sourceText, List<Long> categoryIds) {
        if (sourceText == null || sourceText.isBlank() || categoryIds.isEmpty())
            return new TermCountResult(0, 0);

        String normalizedText = sourceText.toLowerCase();
        String[] tokens = normalizedText.split("\\s+");
        int tokenCount = Math.max(tokens.length, 1);

        // 두 카테고리의 용어를 합칩니다.
        Set<String> termsToCheck = new HashSet<>();
        for (Long id : categoryIds) {
            termsToCheck.addAll(termDictByCategory.getOrDefault(id, Collections.emptySet()));
        }

        int found = 0;
        for (String term : termsToCheck) {
            if (term.isEmpty()) continue;
            if (normalizedText.contains(term)) {
                found++;
            }
        }

        double density = (double) found / tokenCount;
        // ... 로그 출력
        return new TermCountResult(found, density);
    }

    // helper DTO
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
