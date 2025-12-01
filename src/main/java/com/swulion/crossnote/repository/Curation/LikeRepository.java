package com.swulion.crossnote.repository.Curation;

import com.swulion.crossnote.entity.Curation.Like;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    // 상세보기
    boolean existsByUserAndTargetTypeAndTargetId(User user, ScrapTargetType targetType, Long targetId);

    // 좋아요 토글
    Optional<Like> findByUserAndTargetTypeAndTargetId(User user, ScrapTargetType targetType, Long targetId);

    // 큐레이션의 좋아요 개수 조회
    long countByTargetTypeAndTargetId(ScrapTargetType targetType, Long targetId);
}