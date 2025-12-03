package com.swulion.crossnote.dto.Column;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ColumnReadResponseDto {
    Long columnId;
    Long authorId;
    String title;
    String imageUrl;
    Boolean isBestColumn;
    Integer likeCount;
    Integer commentCount;
    String categoryId1;
    String categoryId2;
    String categoryId3;
}
