package com.swulion.crossnote.dto.MyPage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateProfileRequestDto {
    private String name;
    private String email; // 로컬 유저만
    private String password; // 로컬 유저만
    private String passwordCheck; // 로컬 유저만
    private LocalDate birthDate;
    private String profileImageUrl;
    private String gender; // 소셜 유저만
}

