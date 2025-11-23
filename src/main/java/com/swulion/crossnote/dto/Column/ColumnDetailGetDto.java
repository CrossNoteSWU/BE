package com.swulion.crossnote.dto.Column;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ColumnDetailGetDto {
    private ColumnDetailResponseDto columnDetailResponseDto;
    private List<ColumnCommentGetDto> columnCommentGetDtos;
}
