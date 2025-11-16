package com.swulion.crossnote.service;

import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Column.ColumnCategory;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import com.swulion.crossnote.repository.CategoryRepository;
import com.swulion.crossnote.repository.ColumnCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CurationSelectorService {
	private final CategoryRepository categoryRepository;
	private final ColumnCategoryRepository columnCategoryRepository;

	// 주어진 세부 카테고리명과 일치하는 컬럼(큐레이션) 중 하나의 ID를 무작위 선택.
	//일치 항목이 없으면 빈 Optional.

	public Optional<Long> findRandomCurationIdByCategoryName(String categoryName) {
		if (categoryName == null || categoryName.isBlank()) {
			return Optional.empty();
		}
		Category category = categoryRepository.findByCategoryName(categoryName);
		if (category == null) {
			return Optional.empty();
		}
		List<ColumnCategory> mappings = columnCategoryRepository.findByCategoryId(category);
		if (mappings.isEmpty()) {
			return Optional.empty();
		}
		int idx = ThreadLocalRandom.current().nextInt(mappings.size());
		ColumnEntity column = mappings.get(idx).getColumnId();
		return Optional.ofNullable(column != null ? column.getColumnId() : null);
	}

	/**
	 * 옵션 카테고리가 반드시 '하위 카테고리(세부 분야)'여야 한다는 규칙 검증
	 * - 존재하지 않거나 상위 카테고리(부모가 null)이면 예외
	 */
	public void validateMustBeSubCategory(String categoryName) {
		if (categoryName == null || categoryName.isBlank()) {
			throw new IllegalArgumentException("옵션 카테고리는 반드시 1개의 세부 분야여야 합니다.");
		}
		Category category = categoryRepository.findByCategoryName(categoryName);
		if (category == null) {
			throw new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryName);
		}
		if (category.getParentCategoryId() == null) {
			throw new IllegalArgumentException("상위 카테고리는 사용할 수 없습니다. 세부 분야(하위 카테고리)만 허용됩니다: " + categoryName);
		}
	}
}


