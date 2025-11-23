package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.MyPage.KnowledgeReportResponseDto;
import com.swulion.crossnote.dto.MyPage.MyProfileResponseDto;
import com.swulion.crossnote.dto.MyPage.UpdateProfileRequestDto;
import com.swulion.crossnote.entity.*;
import com.swulion.crossnote.entity.Curation.CurationLevel;
import com.swulion.crossnote.repository.CategoryRepository;
import com.swulion.crossnote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final KnowledgeReportService knowledgeReportService;

    @Transactional(readOnly = true)
    public MyProfileResponseDto getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 지식 리포트 조회
        KnowledgeReportResponseDto knowledgeReport = knowledgeReportService.getKnowledgeReport(userId);
        
        return new MyProfileResponseDto(
                user.getUserId(),
                user.getName(),
                user.getProfileImageUrl(),
                user.getFollowersCount(),
                user.getFollowingsCount(),
                knowledgeReport.getScores(),
                knowledgeReport.getChartData()
        );
    }

    @Transactional(readOnly = true)
    public com.swulion.crossnote.dto.MyPage.UserPreferencesResponseDto getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<String> interestNames = new ArrayList<>();
        List<String> expertiseNames = new ArrayList<>();

        for (UserCategoryPreference pref : user.getPreferences()) {
            if (pref.getPreferenceType() == PreferenceType.INTEREST) {
                interestNames.add(pref.getCategory().getCategoryName());
            } else if (pref.getPreferenceType() == PreferenceType.EXPERTISE) {
                expertiseNames.add(pref.getCategory().getCategoryName());
            }
        }

        return new com.swulion.crossnote.dto.MyPage.UserPreferencesResponseDto(
                interestNames,
                expertiseNames,
                user.getCurationLevel()
        );
    }

    public void updateProfile(Long userId, UpdateProfileRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 기본 정보 수정
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

        // 관심분야 수정
        if (request.getInterestNames() != null) {
            // 기존 관심분야 삭제
            user.getPreferences().removeIf(p -> p.getPreferenceType() == PreferenceType.INTEREST);
            userRepository.save(user);
            userRepository.flush();

            // 새로운 관심분야 추가
            for (String categoryName : request.getInterestNames()) {
                Category category = categoryRepository.findByCategoryName(categoryName);
                if (category == null) {
                    throw new IllegalArgumentException("존재하지 않는 카테고리 이름: " + categoryName);
                }
                UserCategoryPreference preference = UserCategoryPreference.create(user, category, PreferenceType.INTEREST);
                user.getPreferences().add(preference);
            }
        }

        // 전문분야 수정
        if (request.getExpertiseNames() != null) {
            // 기존 전문분야 삭제
            user.getPreferences().removeIf(p -> p.getPreferenceType() == PreferenceType.EXPERTISE);
            userRepository.save(user);
            userRepository.flush();

            // 새로운 전문분야 추가
            for (String categoryName : request.getExpertiseNames()) {
                Category category = categoryRepository.findByCategoryName(categoryName);
                if (category == null) {
                    throw new IllegalArgumentException("존재하지 않는 카테고리 이름: " + categoryName);
                }
                // 전문분야는 상위 카테고리 선택 불가
                if (category.getParentCategoryId() == null) {
                    throw new IllegalArgumentException("상위 카테고리는 전문분야로 선택할 수 없습니다: " + categoryName);
                }
                UserCategoryPreference preference = UserCategoryPreference.create(user, category, PreferenceType.EXPERTISE);
                user.getPreferences().add(preference);
            }
        }

        // 큐레이션 수준 수정
        if (request.getCurationLevel() != null) {
            try {
                CurationLevel level = CurationLevel.valueOf(request.getCurationLevel());
                user.setCurationLevel(level);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 큐레이션 수준입니다. LEVEL_1 또는 LEVEL_2를 입력하세요.");
            }
        }

        userRepository.save(user);
    }
}

