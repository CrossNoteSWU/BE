package com.swulion.crossnote.dto.MyPage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KnowledgeReportResponseDto {
    // 6개 상위 분야별 점수 (0-50)
    private Map<String, Integer> scores; // key: 상위 분야명, value: 점수 (0-50)
    
    // 차트 데이터용 (0, 10, 20, 30, 40, 50 스케일)
    private Map<String, Integer> chartData; // 실제 점수를 스케일에 맞게 변환
}

