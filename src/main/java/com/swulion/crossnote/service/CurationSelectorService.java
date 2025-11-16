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
}


