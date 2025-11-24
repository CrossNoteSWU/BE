package com.swulion.crossnote.dto.MyPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequestDto {
    // 기본 정보
    private String name;
    private String email; // 로컬 유저만
    private String password; // 로컬 유저만
    private String passwordCheck; // 로컬 유저만
    private LocalDate birthDate;
    private String profileImageUrl;
    private String gender; // 소셜 유저만
    
    // 관심분야, 전문분야, 큐레이션 수준
    private List<String> interestNames; // 관심 분야 카테고리 이름 목록
    private List<String> expertiseNames; // 전문 분야 카테고리 이름 목록
    private String curationLevel; // "LEVEL_1" 또는 "LEVEL_2"
}

