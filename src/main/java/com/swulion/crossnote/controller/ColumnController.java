package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Column.ColumnDetailGetDto;
import com.swulion.crossnote.dto.Column.ColumnReadResponseDto;
import com.swulion.crossnote.dto.Column.ColumnRequestDto;
import com.swulion.crossnote.dto.Column.ColumnDetailResponseDto;
import com.swulion.crossnote.service.ColumnCommentService;
import com.swulion.crossnote.service.ColumnService;
import com.swulion.crossnote.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/column")
public class ColumnController {

    private final ColumnService columnService;
    private final ColumnCommentService columnCommentService;

    /* Column 홈 */
    @GetMapping("/home")
    public ResponseEntity<List<ColumnReadResponseDto>> getColumnHome(@RequestParam(defaultValue = "latest") String sort) {
        List<ColumnReadResponseDto> columnReadResponseDtos = columnService.getColumnHome(sort);
        return ResponseEntity.ok(columnReadResponseDtos);
    }

    /* Column 생성 */
    @PostMapping("/create")
    public ResponseEntity<ColumnDetailResponseDto> createColumn(@RequestBody ColumnRequestDto columnRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ColumnDetailResponseDto columnDetailResponseDto = columnService.createColumn(columnRequestDto, userDetails.getUser().getUserId());
        return ResponseEntity.ok(columnDetailResponseDto);
    }

    /* Column 삭제 */
    @DeleteMapping("/delete/{columnId}")
    public ResponseEntity<Integer> deleteColumn(@PathVariable Long columnId){
        Integer value = columnService.deleteColumn(columnId);
        return ResponseEntity.ok(value);
    }

    /* Column 상세 보기 */
    @GetMapping("/detail/{columnId}")
    public ResponseEntity<ColumnDetailGetDto> getColumnDetail(@PathVariable Long columnId){
        ColumnDetailGetDto columnDetailGetDto = columnService.getColumn(columnId);
        return ResponseEntity.ok(columnDetailGetDto);
    }

    /* Column 수정 */
    @PatchMapping("/update/{columnId}")
    public ResponseEntity<ColumnDetailResponseDto> updateColumn(@PathVariable Long columnId, @RequestBody ColumnRequestDto columnRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ColumnDetailResponseDto columnDetailResponseDto = columnService.updateColumn(columnId, columnRequestDto, userDetails.getUser().getUserId());
        return ResponseEntity.ok(columnDetailResponseDto);
    }

    /* Column 좋아요 */
    @PatchMapping("/like/{columnId}")
    public ResponseEntity<String> likeColumn(@PathVariable Long columnId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(columnService.likeColumn(columnId, userId));
    }

    /* Column 스크랩 */
    @PatchMapping("/scrap/{columnId}")
    public ResponseEntity<String> scrapColumn(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long columnId) {
        Long userId = userDetails.getUser().getUserId();
        return ResponseEntity.ok(columnService.scrapColumn(columnId, userId));
    }


}
