package com.swulion.crossnote.dto.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnCommentRequestDto {
    Long commentId;
    String content;
}
