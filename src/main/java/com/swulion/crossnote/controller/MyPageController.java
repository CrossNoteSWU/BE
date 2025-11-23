package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.MyPage.*;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.service.CustomUserDetails;
import com.swulion.crossnote.service.KnowledgeReportService;
import com.swulion.crossnote.service.MyPageService;
import com.swulion.crossnote.service.MyQnAService;
import com.swulion.crossnote.service.ScrappedCurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;
    private final KnowledgeReportService knowledgeReportService;
    private final ScrappedCurationService scrappedCurationService;
    private final MyQnAService myQnAService;

    // 6.1 나의 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<MyProfileResponseDto> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(myPageService.getMyProfile(userId));
    }

    // 6.2 정보 수정
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateProfileRequestDto request
    ) {
        Long userId = userDetails.getUser().getUserId();
        myPageService.updateProfile(userId, request);
        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }

    // 6.3 지식 리포트
    @GetMapping("/knowledge-report")
    public ResponseEntity<KnowledgeReportResponseDto> getKnowledgeReport(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(knowledgeReportService.getKnowledgeReport(userId));
    }

    // 6.4 스크랩한 큐레이션 조회
    @GetMapping("/scrapped-curations")
    public ResponseEntity<List<ScrappedCurationDto>> getScrappedCurations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "badge", required = false) String badge, // INSIGHT, CROSSNOTE, BEST_COLUMN
            @RequestParam(value = "field", required = false) String field // 29개 세부 분야 중 1개
    ) {
        Long userId = userDetails.getUser().getUserId();
        CurationType curationType = null;
        if (badge != null && !badge.isEmpty()) {
            try {
                curationType = CurationType.valueOf(badge);
            } catch (IllegalArgumentException e) {
                // 잘못된 badge 값은 무시
            }
        }
        return ResponseEntity.ok(scrappedCurationService.getScrappedCurations(userId, curationType, field));
    }

    // 6.4 스크랩 취소
    @DeleteMapping("/scrapped-curations/{scrapId}")
    public ResponseEntity<String> cancelScrap(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long scrapId
    ) {
        Long userId = userDetails.getUser().getUserId();
        scrappedCurationService.cancelScrap(userId, scrapId);
        return ResponseEntity.ok("스크랩이 취소되었습니다.");
    }

    // 6.5 내가 작성한 QnA 조회
    @GetMapping("/my-qna")
    public ResponseEntity<List<MyQnAResponseDto>> getMyQnA(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "type", required = false) String type // "question", "answer", 또는 전체(null)
    ) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(myQnAService.getMyQnA(userId, type));
    }
}

