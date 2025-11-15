package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Curation.CurationDetailDto;
import com.swulion.crossnote.dto.Curation.CurationFeedDto;
import com.swulion.crossnote.dto.Curation.CurationToggleResponseDto;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.service.BestColumnCurationService;
import com.swulion.crossnote.service.CurationService;
import com.swulion.crossnote.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/curation")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;
    private final BestColumnCurationService bestColumnCurationService;

    /*
     개인화 큐레이션 피드 조회 (홈) API
     [GET] /curation/personal
     */
    @GetMapping("/personal")
    public ResponseEntity<List<CurationFeedDto>> getPersonilzedFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<CurationFeedDto> curationFeedDtos = curationService.getPersonalizedFeed(user);
        return ResponseEntity.ok(curationFeedDtos);
    }

    /*
     전체 큐레이션 피드 조회 API
     [GET] /curation
     */
    @GetMapping
    public ResponseEntity<Page<CurationFeedDto>> getAllCurationFeed(
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String curationType,
            @RequestParam(required = false) String query) {
        Page<CurationFeedDto> curationFeedDtos = curationService.getAllCurationFeed(categoryId, curationType, query, pageable);
        return ResponseEntity.ok(curationFeedDtos);
    }

    /*
     큐레이션 상세 조회 API
     [GET] /curation/{curationId}
     */
    @GetMapping("/{curationId}")
    public ResponseEntity<CurationDetailDto> getCurationDetail(
            @PathVariable Long curationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        CurationDetailDto curationDetailDtos = curationService.getCurationDetail(curationId, user);
        return ResponseEntity.ok(curationDetailDtos);
    }

    /*
     큐레이션 좋아요 토글 API
     [POST] /curation/{curationId}/like
     */
    @PostMapping("/{curationId}/like")
    public ResponseEntity<CurationToggleResponseDto> toggleCurationLike(
            @PathVariable Long curationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        CurationToggleResponseDto responseDtos = curationService.toggleCurationLike(curationId, user);
        return ResponseEntity.ok(responseDtos);
    }

    /*
     큐레이션 스크랩 토글 API
     [POST] /curation/{curationId}/scrap
     */
    @PostMapping("/{curationId}/scrap")
    public ResponseEntity<CurationToggleResponseDto> toggleCurationScrap(
            @PathVariable Long curationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        CurationToggleResponseDto responseDtos = curationService.toggleCurationScrap(curationId, user);
        return ResponseEntity.ok(responseDtos);
    }

    /*
     * [테스트용] 큐레이션 생성 스케줄러를 수동으로 실행
     * [POST] /curation/test/run-batch
     */
    @PostMapping("/test/run-batch")
    public ResponseEntity<String> runDailyCurationBatch() {
        log.warn("=== [TEST] 수동 큐레이션 생성 작업을 시작합니다. ===");
        try {
            // (CurationService의 스케줄링 메서드를 직접 호출)
            curationService.createDailyCurations();
            log.warn("=== [TEST] 수동 큐레이션 생성 작업 완료. ===");
            return ResponseEntity.ok("수동 큐레이션 배치 작업 완료.");

        } catch (Exception e) {
            log.error("=== [TEST] 수동 큐레이션 생성 중 오류 발생 ===", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("배치 작업 실패: " + e.getMessage());
        }
    }

    /* [테스트용] 베스트 칼럼 선정 로직을 수동으로 실행하는 API 추가
     * [POST] /curation/test/run-best-column
     */
    @PostMapping("/test/run-best-column")
    public ResponseEntity<String> runBestColumnCurator() {
        log.warn("=== [TEST] 베스트 칼럼 선정 작업을 시작합니다. ===");
        try {
            bestColumnCurationService.curateBestColumns();
            log.warn("=== [TEST] 베스트 칼럼 선정 작업 완료. ===");
            return ResponseEntity.ok("수동 베스트 칼럼 선정 작업 완료.");

        } catch (Exception e) {
            log.error("=== [TEST] 베스트 칼럼 선정 중 오류 발생 ===", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("베스트 칼럼 선정 작업 실패: " + e.getMessage());
        }
    }
}
