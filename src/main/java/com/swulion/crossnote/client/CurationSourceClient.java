package com.swulion.crossnote.client;

import com.swulion.crossnote.dto.Curation.CurationSourceDto;

/**
 * 모든 API 클라이언트(Naver, Youtube, Dbpia...)가 구현해야 할 인터페이스
 */
public interface CurationSourceClient {

    /**
     * 특정 키워드(분야명)로 소스 1개를 가져옵니다.
     * @param query (예: "IT", "철학")
     * @return 큐레이션 생성에 필요한 재료 DTO
     */
    CurationSourceDto fetchSource(String query);

    /**
     * 이 클라이언트가 어떤 소스 유형을 담당하는지 식별자를 반환합니다.
     * (예: "NEWS", "YOUTUBE")
     */
    String getSourceType();
}