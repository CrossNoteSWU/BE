package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.ColumnReadResponseDto;
import com.swulion.crossnote.dto.UserProfileFullResponseDto;
import com.swulion.crossnote.entity.ColumnCategory;
import com.swulion.crossnote.entity.ColumnEntity;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.ColumnCategoryRepository;
import com.swulion.crossnote.repository.ColumnRepository;
import com.swulion.crossnote.repository.UserRepository;
import com.swulion.crossnote.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

	private final UserRepository userRepository;
	private final ColumnRepository columnRepository;
	private final ColumnCategoryRepository columnCategoryRepository;
	private final FollowRepository followRepository;

	public UserProfileFullResponseDto getUserProfileFull(Long currentUserId, Long targetUserId, Pageable pageable) {
		User target = userRepository.findById(targetUserId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + targetUserId));

		// 팔로우 여부 (비로그인 접근 시 currentUserId가 null일 수 있음)
		boolean following = false;
		if (currentUserId != null && !currentUserId.equals(targetUserId)) {
			User current = userRepository.findById(currentUserId)
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + currentUserId));
			following = followRepository.existsByFollowerAndFollowee(current, target);
		}

		// 칼럼 페이지
		Page<ColumnEntity> page = columnRepository.findByColumnAutherIdOrderByCreatedAtDesc(target, pageable);
		List<ColumnReadResponseDto> items = new ArrayList<>();
		for (ColumnEntity entity : page.getContent()) {
			ColumnReadResponseDto dto = new ColumnReadResponseDto();
			dto.setColumnId(entity.getColumnId());
			dto.setAuthorId(entity.getColumnAutherId().getUserId());
			dto.setTitle(entity.getTitle());
			dto.setIsBestColumn(entity.isBestColumn());
			dto.setCommentCount(entity.getCommentCount());
			dto.setLikeCount(entity.getLikeCount());

			List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(entity);
			List<Long> categories = new ArrayList<>();
			for (ColumnCategory columnCategory : columnCategories) {
				categories.add(columnCategory.getCategoryId().getCategoryId());
			}
			Long cat1 = categories.size() > 0 ? categories.get(0) : null;
			Long cat2 = categories.size() > 1 ? categories.get(1) : null;
			Long cat3 = categories.size() > 2 ? categories.get(2) : null;

			dto.setCategoryId1(cat1);
			dto.setCategoryId2(cat2);
			dto.setCategoryId3(cat3);

			items.add(dto);
		}

		return new UserProfileFullResponseDto(
				target.getUserId(),
				target.getName(),
				target.getEmail(),
				target.getProfileImageUrl(),
				target.getFollowersCount(),
				target.getFollowingsCount(),
				following,
				page.getTotalElements(),
				page.getTotalPages(),
				page.hasNext(),
				pageable.getPageNumber(),
				pageable.getPageSize(),
				items
		);
	}
}


