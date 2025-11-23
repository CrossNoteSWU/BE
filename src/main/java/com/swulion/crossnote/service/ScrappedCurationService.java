package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.MyPage.ScrappedCurationDto;
import com.swulion.crossnote.entity.Curation.Curation;
import com.swulion.crossnote.entity.Curation.Scrap;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.Curation.CurationType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.Curation.CurationRepository;
import com.swulion.crossnote.repository.Curation.ScrapRepository;
import com.swulion.crossnote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrappedCurationService {

    private final ScrapRepository scrapRepository;
    private final CurationRepository curationRepository;
    private final UserRepository userRepository;

    public List<ScrappedCurationDto> getScrappedCurations(Long userId, CurationType curationType, String field) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Scrap> scraps = scrapRepository.findByUserAndTargetTypeOrderByCreatedAtDesc(user, ScrapTargetType.CURATION);

        return scraps.stream()
                .map(scrap -> {
                    Curation curation = curationRepository.findById(scrap.getTargetId())
                            .orElse(null);
                    if (curation == null) {
                        return null;
                    }
                    
                    // 필터링: curationType
                    if (curationType != null && curation.getCurationType() != curationType) {
                        return null;
                    }
                    
                    // 필터링: field (카테고리명)
                    if (field != null && !field.isEmpty()) {
                        String categoryName = curation.getCategory().getCategoryName();
                        if (!categoryName.equals(field)) {
                            return null;
                        }
                    }
                    
                    return new ScrappedCurationDto(
                            scrap.getId(),
                            curation.getId(),
                            curation.getCurationType(),
                            curation.getCategory().getCategoryName(),
                            curation.getTitle(),
                            curation.getImageUrl(),
                            curation.getDescription()
                    );
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelScrap(Long userId, Long scrapId) {
        Scrap scrap = scrapRepository.findById(scrapId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스크랩입니다."));

        if (!scrap.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 스크랩만 취소할 수 있습니다.");
        }

        // 큐레이션 스크랩 수 감소
        if (scrap.getTargetType() == ScrapTargetType.CURATION) {
            Curation curation = curationRepository.findById(scrap.getTargetId())
                    .orElse(null);
            if (curation != null) {
                curation.decrementScrapCount();
            }
        }

        scrapRepository.delete(scrap);
    }
}

