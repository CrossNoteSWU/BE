package com.swulion.crossnote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserProfileFullResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String profileImageUrl;
	private long followersCount;
	private long followingsCount;
	private boolean following; // 현재 로그인 사용자의 팔로우 여부

	// 칼럼 목록 (페이지네이션)
	private long totalElements;
	private int totalPages;
	private boolean hasNext;
	private int page;
	private int size;
	private List<ColumnReadResponseDto> items;
}


