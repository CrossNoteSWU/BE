package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.Column.ColumnComment;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnCommentRepository extends JpaRepository<ColumnComment, Long> {
    List<ColumnComment> findAllByColumnId(ColumnEntity columnId);
}
