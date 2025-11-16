package com.swulion.crossnote.entity.QA;

import com.swulion.crossnote.entity.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "questionCategory")
public class QuestionCategory {

    @Id
    @GeneratedValue
    private Long questionCategoryId;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private Question questionId;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category categoryId;

    private LocalDateTime createdAt;
}
