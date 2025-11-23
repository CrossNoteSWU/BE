package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.Column.ColumnCommentCreateDto;
import com.swulion.crossnote.dto.Column.ColumnCommentRequestDto;
import com.swulion.crossnote.dto.Column.ColumnCommentResponseDto;
import com.swulion.crossnote.service.ColumnCommentService;
import com.swulion.crossnote.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/column/comment")
public class ColumnCommentController {
    private final ColumnCommentService columnCommentService;


    @PostMapping("/create")
    public ResponseEntity<ColumnCommentResponseDto> createColumnComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ColumnCommentCreateDto columnCommentCreateDto) {
        Long userId = userDetails.getUser().getUserId();
        ColumnCommentResponseDto columnCommentResponseDto = columnCommentService.createColumnComment(userId, columnCommentCreateDto);
        return ResponseEntity.ok(columnCommentResponseDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<ColumnCommentResponseDto> updateColumnComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ColumnCommentRequestDto columnCommentRequestDto) {
        Long userId = userDetails.getUser().getUserId();
        ColumnCommentResponseDto columnCommentResponseDto = columnCommentService.updateColumnComment(userId, columnCommentRequestDto);
        return ResponseEntity.ok(columnCommentResponseDto);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteColumnComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        Long userId = userDetails.getUser().getUserId();
        String result = columnCommentService.deleteColumnComment(userId, commentId);
        return ResponseEntity.ok(result);
    }
}
