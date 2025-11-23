package com.swulion.crossnote.dto.MyPage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MyProfileResponseDto {
    private Long userId;
    private String name;
    private String profileImageUrl;
    private long followersCount;
    private long followingsCount;
    
    // 지식 리포트 데이터
    private Map<String, Integer> knowledgeScores; // 6개 상위 분야별 점수 (0-50)
    private Map<String, Integer> knowledgeChartData; // 차트 데이터용 (0, 10, 20, 30, 40, 50 스케일)
}

