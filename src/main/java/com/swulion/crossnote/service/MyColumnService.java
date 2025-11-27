package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Column.ColumnReadResponseDto;
import com.swulion.crossnote.entity.Column.ColumnCategory;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.Column.ColumnCategoryRepository;
import com.swulion.crossnote.repository.Column.ColumnRepository;
import com.swulion.crossnote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnCategoryRepository columnCategoryRepository;
    private final UserRepository userRepository;

    public Page<ColumnReadResponseDto> getMyColumns(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<ColumnEntity> columns = columnRepository.findByColumnAutherIdOrderByCreatedAtDesc(user, pageable);
        
        return columns.map(column -> {
            ColumnReadResponseDto dto = new ColumnReadResponseDto();
            dto.setColumnId(column.getColumnId());
            dto.setAuthorId(column.getColumnAutherId().getUserId());
            dto.setTitle(column.getTitle());
            dto.setIsBestColumn(column.isBestColumn());
            dto.setCommentCount(column.getCommentCount());
            dto.setLikeCount(column.getLikeCount());

            List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(column);
            List<String> categories = new ArrayList<>();
            for (ColumnCategory columnCategory : columnCategories) {
                categories.add(columnCategory.getCategoryId().getCategoryName());
            }
            String cat1 = categories.size() > 0 ? categories.get(0) : null;
            String cat2 = categories.size() > 1 ? categories.get(1) : null;
            String cat3 = categories.size() > 2 ? categories.get(2) : null;

            dto.setCategoryId1(cat1);
            dto.setCategoryId2(cat2);
            dto.setCategoryId3(cat3);

            return dto;
        });
    }
}

