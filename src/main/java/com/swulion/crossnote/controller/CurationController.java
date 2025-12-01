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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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
            @RequestParam(required = false) List<Long> categoryId,
            @RequestParam(required = false) List<String> curationType,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDetails.getUser();
        Page<CurationFeedDto> curationFeedDtos = curationService.getAllCurationFeed(categoryId, curationType, query, pageable, user);
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

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

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

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDetails.getUser();
        CurationToggleResponseDto responseDtos = curationService.toggleCurationScrap(curationId, user);
        return ResponseEntity.ok(responseDtos);
    }

    /*
     [테스트용] 큐레이션 116개 강제 생성 (배치 수동 실행)
     [POST] /curation/test/generate
    */
    @PostMapping("/test/generate")
    public String manualGenerateCuration() {
        log.warn("큐레이션 생성 강제 시작");

        // 비동기 처리: 별도의 스레드에게 일을 시키고, 이 메서드는 즉시 리턴
        CompletableFuture.runAsync(() -> {
            try {
                curationService.scheduleDailyCurationCreation();
            } catch (Exception e) {
                log.error("수동 생성 중 에러 발생", e);
            }
        });
        return "큐레이션 생성 작업이 백그라운드에서 시작 (완료까지 약 5~6분 소요, 로그 확인 필요)";
    }

    // 초기화용. 큐레이션 및 관련 데이터 지우기 - 큐레이션 담당자만 사용!!
//    @DeleteMapping("/test/delete-all")
//    public ResponseEntity<String> deleteAllCurations() {
//        try {
//            curationService.deleteAllCurations();
//            return ResponseEntity.ok("큐레이션 삭제");
//        } catch (Exception e) {
//            log.error("초기화 실패", e);
//            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
//        }
//    }

    /*
     [테스트용] 베스트 칼럼 선정 로직을 수동으로 실행하는 API 추가
     [POST] /curation/test/run-best-column
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
