package com.swulion.crossnote.repository.Curation;

import com.swulion.crossnote.entity.Curation.Scrap;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // 상세보기
    boolean existsByUserAndTargetTypeAndTargetId(User user, ScrapTargetType targetType, Long targetId);

    // 스크랩 토글
    Optional<Scrap> findByUserAndTargetTypeAndTargetId(User user, ScrapTargetType targetType, Long targetId);
}
