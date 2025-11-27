package com.swulion.crossnote.repository.Column;

import com.swulion.crossnote.entity.Column.ColumnCategory;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnCategoryRepository extends JpaRepository<ColumnCategory, Long> {
    List<ColumnCategory> findByColumnId(ColumnEntity columnId);

    void deleteByColumnId(ColumnEntity columnId);

    // 특정 카테고리에 속한 컬럼 매핑 조회
    List<ColumnCategory> findByCategoryId(com.swulion.crossnote.entity.Category category);

}
