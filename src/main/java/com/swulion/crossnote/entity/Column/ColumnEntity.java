package com.swulion.crossnote.entity.Column;

import com.swulion.crossnote.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`column`")
public class ColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long columnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User columnAutherId;

    @Column(length = 20)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl = null;

    private Integer likeCount = 0;
    private Integer commentCount = 0;
    private Integer scrapCount = 0;

    private boolean isBestColumn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
