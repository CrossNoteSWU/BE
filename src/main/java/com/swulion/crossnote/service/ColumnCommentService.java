package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Column.ColumnCommentCreateDto;
import com.swulion.crossnote.dto.Column.ColumnCommentRequestDto;
import com.swulion.crossnote.dto.Column.ColumnCommentResponseDto;
import com.swulion.crossnote.entity.Column.ColumnComment;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import com.swulion.crossnote.entity.NotificationType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.ColumnCommentRepository;
import com.swulion.crossnote.repository.ColumnRepository;
import com.swulion.crossnote.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ColumnCommentService {

    private final ColumnCommentRepository columnCommentRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ColumnCommentResponseDto createColumnComment(Long userId, ColumnCommentCreateDto columnCommentCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );

        ColumnEntity column = columnRepository.findById(columnCommentCreateDto.getColumnId()).orElseThrow(
                ()-> new RuntimeException("Column not found")
        );
        ColumnComment columnComment = new ColumnComment();
        columnComment.setUserId(user);
        columnComment.setColumnId(column);
        columnComment.setComment(columnCommentCreateDto.getComment());
        columnComment.setCreatedAt(LocalDateTime.now());
        columnCommentRepository.save(columnComment);

        column.setCommentCount(column.getCommentCount() + 1);
        if(column.getLikeCount() >= 10 && column.getCommentCount() >= 10){
            column.setBestColumn(true);
        }
        columnRepository.save(column);
        Long columnWriterId = column.getColumnAutherId().getUserId();
        String message = user.getName() + "님이 내 칼럼에 댓글을 남겼어요.";
        notificationService.sendNotification(columnWriterId, userId, NotificationType.COLUMN, column.getColumnId(), message);

        ColumnCommentResponseDto columnCommentResponseDto = new ColumnCommentResponseDto();
        columnCommentResponseDto.setColumnCommentId(columnComment.getCommentId());
        columnCommentResponseDto.setUserId(userId);
        columnCommentResponseDto.setColumnId(columnCommentCreateDto.getColumnId());
        columnCommentResponseDto.setComment(columnCommentCreateDto.getComment());
        columnCommentResponseDto.setCreatedAt(columnComment.getCreatedAt());
        return columnCommentResponseDto;
    }

    public ColumnCommentResponseDto updateColumnComment(Long userId, ColumnCommentRequestDto columnCommentRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
        if (!userId.equals(user.getUserId())) {
            throw new RuntimeException("댓글 작성자 본인만 수정할 수 있습니다.");
        }

        ColumnComment columnComment = columnCommentRepository.findById(columnCommentRequestDto.getCommentId()).orElseThrow(
                ()-> new RuntimeException("Comment not found")
        );
        columnComment.setComment(columnCommentRequestDto.getContent());
        columnComment.setUpdatedAt(LocalDateTime.now());
        columnCommentRepository.save(columnComment);

        ColumnCommentResponseDto columnCommentResponseDto = new ColumnCommentResponseDto();
        columnCommentResponseDto.setColumnCommentId(columnComment.getCommentId());
        columnCommentResponseDto.setUserId(userId);
        columnCommentResponseDto.setColumnId(columnComment.getCommentId());
        columnCommentResponseDto.setComment(columnComment.getComment());
        columnCommentResponseDto.setUpdatedAt(columnComment.getUpdatedAt());
        return columnCommentResponseDto;

    }

    public String deleteColumnComment(Long userId, Long columnCommentId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new RuntimeException("User not found")
        );
        ColumnComment columnComment = columnCommentRepository.findById(columnCommentId).orElseThrow(
                ()-> new RuntimeException("Comment not found")
        );
        if (!(user.equals(columnComment.getUserId()))) {
            throw new RuntimeException("댓글 작성자 본인만 삭제할 수 있습니다.");
        }
        columnCommentRepository.delete(columnComment);
        ColumnEntity column = columnComment.getColumnId();
        column.setCommentCount(column.getCommentCount() - 1);
        if(column.getLikeCount() < 10 && column.getCommentCount() < 10){
            column.setBestColumn(true);
        }
        columnRepository.save(column);

        return "삭제 완료";
    }

}
