package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.MyPage.MyProfileResponseDto;
import com.swulion.crossnote.dto.MyPage.UpdateProfileRequestDto;
import com.swulion.crossnote.entity.LoginType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MyProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        return new MyProfileResponseDto(
                user.getUserId(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getFollowersCount(),
                user.getFollowingsCount()
        );
    }

    public void updateProfile(Long userId, UpdateProfileRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 로컬 유저인 경우
        if (user.getLoginType() == LoginType.LOCAL) {
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                // 이메일 중복 확인
                if (userRepository.existsByEmail(request.getEmail()) && 
                    !user.getEmail().equals(request.getEmail())) {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }
                user.setEmail(request.getEmail());
            }
            if (request.getPassword() != null) {
                if (!request.getPassword().equals(request.getPasswordCheck())) {
                    throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
                }
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (request.getBirthDate() != null) {
                user.setBirthDate(request.getBirthDate());
            }
            if (request.getProfileImageUrl() != null) {
                user.setProfileImageUrl(request.getProfileImageUrl());
            }
        } 
        // 소셜 유저인 경우
        else {
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getGender() != null) {
                try {
                    user.setGender(com.swulion.crossnote.entity.Gender.valueOf(request.getGender()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("유효하지 않은 성별입니다.");
                }
            }
            if (request.getBirthDate() != null) {
                user.setBirthDate(request.getBirthDate());
            }
            if (request.getProfileImageUrl() != null) {
                user.setProfileImageUrl(request.getProfileImageUrl());
            }
        }
    }
}

