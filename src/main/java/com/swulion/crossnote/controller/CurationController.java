package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Curation.CurationDetailDto;
import com.swulion.crossnote.dto.Curation.CurationFeedDto;
import com.swulion.crossnote.dto.Curation.CurationToggleResponseDto;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.service.CurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curation")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    /*
     개인화 큐레이션 피드 조회 (홈) API
     [GET] /curation/personal
     */
    @GetMapping("/personal")
    public ResponseEntity<List<CurationFeedDto>> getPersonilzedFeed(
            @AuthenticationPrincipal User user) {
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
            @AuthenticationPrincipal User user) {
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
            @AuthenticationPrincipal User user) {
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
            @AuthenticationPrincipal User user) {
        CurationToggleResponseDto responseDtos = curationService.toggleCurationScrap(curationId, user);
        return ResponseEntity.ok(responseDtos);
    }
}
