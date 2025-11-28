package com.swulion.crossnote.dto.Column;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ColumnSearchDto {
    List<Long> categoryIds;
    String keyword;
}
