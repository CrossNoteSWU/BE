package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Column.*;
import com.swulion.crossnote.entity.Category;
import com.swulion.crossnote.entity.Column.ColumnCategory;
import com.swulion.crossnote.entity.Column.ColumnComment;
import com.swulion.crossnote.entity.Column.ColumnEntity;
import com.swulion.crossnote.entity.Curation.Like;
import com.swulion.crossnote.entity.Curation.Scrap;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.NotificationType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.*;
import com.swulion.crossnote.repository.Column.ColumnCategoryRepository;
import com.swulion.crossnote.repository.Column.ColumnCommentRepository;
import com.swulion.crossnote.repository.Column.ColumnRepository;
import com.swulion.crossnote.repository.Column.ColumnRepositoryImpl;
import com.swulion.crossnote.repository.Curation.LikeRepository;
import com.swulion.crossnote.repository.Curation.ScrapRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColumnService {
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ColumnCategoryRepository columnCategoryRepository;
    private final ColumnCommentRepository columnCommentRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;
    private final ScrapRepository scrapRepository;
    private final ColumnRepositoryImpl columnRepositoryImpl;


    /* 칼럼 생성 */
    @Transactional
    public ColumnDetailResponseDto createColumn(ColumnRequestDto columnRequestDto, Long loginUserId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        ColumnEntity columnEntity = new ColumnEntity();
        columnEntity.setCreatedAt(LocalDateTime.now());
        columnEntity.setUpdatedAt(LocalDateTime.now());
        columnEntity.setLikeCount(0);
        columnEntity.setTitle(columnRequestDto.getTitle());
        columnEntity.setContent(columnRequestDto.getContent());
        columnEntity.setImageUrl(columnRequestDto.getImageUrl());

        User columnAuthorId = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        columnEntity.setColumnAutherId(columnAuthorId);
        columnRepository.save(columnEntity);

        List<Long> categories = getCategories(columnEntity, columnRequestDto);

        return new ColumnDetailResponseDto(
                columnEntity.getColumnAutherId().getUserId(),
                columnEntity.getTitle(),
                columnEntity.getContent(),
                columnEntity.getLikeCount(),
                0,
                columnEntity.isBestColumn(),
                columnEntity.getImageUrl(),
                columnEntity.getCreatedAt(),
                columnEntity.getUpdatedAt(),
                categories.get(0),
                categories.get(1),
                categories.get(2)

        );


    }

    /* 칼럼 삭제 */
    @Transactional
    public Integer deleteColumn(Long columnId) {
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElse(null);

        if (columnEntity == null){
            throw new EntityNotFoundException("Column Not Found");
        }else {

            columnCategoryRepository.deleteByColumnId(columnEntity);
            columnRepository.delete(columnEntity);
            return 1;
        }

    }

    /* 칼럼 수정 */
    @Transactional
    public ColumnDetailResponseDto updateColumn(Long columnId, ColumnRequestDto columnRequestDto, Long loginUserId) {

        User user = userRepository.findById(loginUserId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElse(null);
        if (columnEntity == null){
            throw new EntityNotFoundException("Column Not Found");
        }else {
            columnEntity.setTitle(columnRequestDto.getTitle());
            columnEntity.setContent(columnRequestDto.getContent());
            columnEntity.setImageUrl(columnRequestDto.getImageUrl());
            columnEntity.setUpdatedAt(LocalDateTime.now());
            columnRepository.save(columnEntity);
        }

        List<Long> categories = getCategories(columnEntity, columnRequestDto);

        return new ColumnDetailResponseDto(
                user.getUserId(),
                columnEntity.getTitle(),
                columnEntity.getContent(),
                columnEntity.getLikeCount(),
                columnEntity.getCommentCount(),
                columnEntity.isBestColumn(),
                columnEntity.getImageUrl(),
                columnEntity.getCreatedAt(),
                columnEntity.getUpdatedAt(),
                categories.get(0),
                categories.get(1),
                categories.get(2)

        );
    }

    /* 전체 칼럼 조회 */
    public List<ColumnReadResponseDto> getColumnHome(String sort) {
        List<ColumnEntity> columnEntities;
        if (sort.equals("latest")) {
            columnEntities = columnRepository.findAllByOrderByCreatedAtDesc();
        }else if(sort.equals("popular")){
            columnEntities = columnRepository.findAllByOrderByLikeCountDesc();
        }else if(sort.equals("comment")){
            columnEntities = columnRepository.findAllByOrderByCommentCountDesc();
        }else{
            columnEntities = columnRepository.findAllByOrderByCreatedAtDesc();
        }

        List<ColumnReadResponseDto> columnReadResponseDtos = new ArrayList<>();
        for (ColumnEntity columnEntity : columnEntities) {
            ColumnReadResponseDto columnReadResponseDto = new ColumnReadResponseDto();
            columnReadResponseDto.setColumnId(columnEntity.getColumnId());

            List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(columnEntity);
            List<String> categories = new ArrayList<>();
            for (ColumnCategory columnCategory : columnCategories) {
                Category category = columnCategory.getCategoryId();
                categories.add(category.getCategoryName());
            }

            String cat2 = categories.size() > 1 ? categories.get(1) : null;
            String cat3 = categories.size() > 2 ? categories.get(2) : null;

            columnReadResponseDto.setAuthorId(columnEntity.getColumnAutherId().getUserId());
            columnReadResponseDto.setTitle(columnEntity.getTitle());
            columnReadResponseDto.setContent(columnEntity.getContent());
            columnReadResponseDto.setIsBestColumn(columnEntity.isBestColumn());
            columnReadResponseDto.setCommentCount(columnEntity.getCommentCount());
            columnReadResponseDto.setLikeCount(columnEntity.getLikeCount());
            columnReadResponseDto.setCategoryId1(categories.get(0));
            columnReadResponseDto.setCategoryId2(cat2);
            columnReadResponseDto.setCategoryId3(cat3);
            columnReadResponseDtos.add(columnReadResponseDto);
        }
        return columnReadResponseDtos;

    }

    /* 칼럼 상세 보기 */
    public ColumnDetailGetDto getColumn(Long columnId) {
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElse(null);
        if (columnEntity == null){
            throw new EntityNotFoundException("Column Not Found");
        }
            List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(columnEntity);
            List<Long> categories = new ArrayList<>();
            for (ColumnCategory columnCategory : columnCategories) {
                Category category = columnCategory.getCategoryId();
                categories.add(category.getCategoryId());
            }

            Long cat2 = categories.size() > 1 ? categories.get(1) : null;
            Long cat3 = categories.size() > 2 ? categories.get(2) : null;

            ColumnDetailResponseDto columnDetailResponseDto = new ColumnDetailResponseDto(
                    columnEntity.getColumnAutherId().getUserId(),
                    columnEntity.getTitle(),
                    columnEntity.getContent(),
                    columnEntity.getLikeCount(),
                    columnEntity.getCommentCount(),
                    columnEntity.isBestColumn(),
                    columnEntity.getImageUrl(),
                    columnEntity.getCreatedAt(),
                    columnEntity.getUpdatedAt(),
                    categories.get(0),
                    cat2,
                    cat3
            );

        List<ColumnComment> columnComments = columnCommentRepository.findAllByColumnId(columnEntity);
        List<ColumnCommentGetDto> columnCommentGetDtos = new ArrayList<>();
        for (ColumnComment columnComment : columnComments) {
            ColumnCommentGetDto columnCommentGetDto = new ColumnCommentGetDto();
            columnCommentGetDto.setUserId(columnComment.getUserId().getUserId());
            columnCommentGetDto.setComment(columnComment.getComment());
            columnCommentGetDto.setCreatedAt(columnComment.getCreatedAt());
            columnCommentGetDto.setUpdatedAt(columnComment.getUpdatedAt());
            columnCommentGetDtos.add(columnCommentGetDto);
        }
        ColumnDetailGetDto columnDetailGetDto = new ColumnDetailGetDto();
        columnDetailGetDto.setColumnDetailResponseDto(columnDetailResponseDto);
        columnDetailGetDto.setColumnCommentGetDtos(columnCommentGetDtos);
        return columnDetailGetDto;

    }


    /* 카테고리 추출 method */
    private List<Long> getCategories(ColumnEntity columnEntity, ColumnRequestDto columnRequestDto) {
        Category category1 = categoryRepository.findByCategoryId(columnRequestDto.getCategory1());
        Category category2 = categoryRepository.findByCategoryId(columnRequestDto.getCategory2());
        Category category3 = categoryRepository.findByCategoryId(columnRequestDto.getCategory3());

        ColumnCategory columnCategory1 = new ColumnCategory();
        columnCategory1.setCategoryId(category1);
        columnCategory1.setColumnId(columnEntity);
        columnCategory1.setCreatedAt(LocalDateTime.now());
        columnCategoryRepository.save(columnCategory1);

        ColumnCategory columnCategory2 = null;
        ColumnCategory columnCategory3 = null;
        if(category2 != null){
            columnCategory2 = new ColumnCategory();
            columnCategory2.setCategoryId(category2);
            columnCategory2.setColumnId(columnEntity);
            columnCategory2.setCreatedAt(LocalDateTime.now());
            columnCategoryRepository.save(columnCategory2);
        }
        if(category3 != null){
            columnCategory3 = new ColumnCategory();
            columnCategory3.setCategoryId(category3);
            columnCategory3.setColumnId(columnEntity);
            columnCategory3.setCreatedAt(LocalDateTime.now());
            columnCategoryRepository.save(columnCategory3);
        }

        Long responseCatId2 = (category2 != null) ? category2.getCategoryId() : null;
        Long responseCatId3 = (category3 != null) ? category3.getCategoryId() : null;

        List<Long> categories = new ArrayList<>();
        categories.add(category1.getCategoryId());
        categories.add(responseCatId2);
        categories.add(responseCatId3);

        return categories;
    }

    /* Column 좋아요 */
    @Transactional
    public String likeColumn(Long userId, Long columnId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User Not Found")
        );
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElseThrow(
                () -> new EntityNotFoundException("Column Not Found")
        );

        User author = columnEntity.getColumnAutherId();
        if(author.getUserId().equals(userId)){
            return "내가 작성한 칼럼은 좋아요를 누를 수 없습니다.";
        }
        if(likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.COLUMN, columnId).isPresent()){
            Like like = likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.COLUMN, columnId).orElseThrow(
                    () -> new EntityNotFoundException("Like Not Found")
            );
            likeRepository.delete(like);
            columnEntity.setLikeCount(columnEntity.getLikeCount() - 1);
            if(columnEntity.getLikeCount() < 10 && columnEntity.getCommentCount() < 10 && columnEntity.isBestColumn()){
                columnEntity.setBestColumn(false);
            }
            columnRepository.save(columnEntity);
            return "좋아요 취소";
        }
        Like like = new Like(user, ScrapTargetType.COLUMN, columnId);
        likeRepository.save(like);

        columnEntity.setLikeCount(columnEntity.getLikeCount() + 1);
       if(columnEntity.getLikeCount() >= 10 && columnEntity.getScrapCount() >= 10){
            columnEntity.setBestColumn(true);
        }
        columnRepository.save(columnEntity);

        String message = user.getName() + "님이 내 칼럼에 좋아요를 남겼어요.";
        notificationService.sendNotification(author.getUserId(), user.getUserId(), NotificationType.COLUMN, columnId, message);

        return "좋아요 완료";

    }

    /* Column 스크랩 */
    @Transactional
    public String scrapColumn(Long userId, Long columnId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User Not Found")
        );
        ColumnEntity columnEntity = columnRepository.findById(columnId).orElseThrow(
                () -> new EntityNotFoundException("Column Not Found")
        );

        User author = columnEntity.getColumnAutherId();
        if(author.getUserId().equals(userId)){
            return "내가 작성한 칼럼은 스크랩 할 수 없습니다.";
        }
        if(scrapRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.COLUMN, columnId).isPresent()){
            Scrap scrap = scrapRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.COLUMN, columnId).orElseThrow(
                    () -> new EntityNotFoundException("Scrap Not Found")
            );
            scrapRepository.delete(scrap);
            columnEntity.setScrapCount(columnEntity.getScrapCount() - 1);
            if(columnEntity.getLikeCount() < 10 && columnEntity.getCommentCount() < 10 && columnEntity.isBestColumn()){
                columnEntity.setBestColumn(false);
            }
            columnRepository.save(columnEntity);
            return "스크랩 취소";
        }
        Like like = new Like(user, ScrapTargetType.COLUMN, columnId);
        likeRepository.save(like);

        columnEntity.setScrapCount(columnEntity.getScrapCount() + 1);
        if(columnEntity.getLikeCount() >= 10 && columnEntity.getScrapCount() >= 10){
            columnEntity.setBestColumn(true);
        }
        columnRepository.save(columnEntity);

        return "스크랩 완료";

    }

    /* Column 검색 */
    public List<ColumnReadResponseDto> searchColumn(ColumnSearchDto columnSearchDto) {
        List<ColumnEntity> columnEntities = columnRepositoryImpl.findWithKeyword(columnSearchDto.getCategoryId(), columnSearchDto.getKeyword());
        List<ColumnReadResponseDto> columnReadResponseDtos = new ArrayList<>();
        for(ColumnEntity columnEntity : columnEntities){
            List<ColumnCategory> columnCategories = columnCategoryRepository.findByColumnId(columnEntity);
            ColumnReadResponseDto columnReadResponseDto = new ColumnReadResponseDto();
            columnReadResponseDto.setColumnId(columnEntity.getColumnId());
            columnReadResponseDto.setAuthorId(columnEntity.getColumnAutherId().getUserId());
            columnReadResponseDto.setTitle(columnEntity.getTitle());
            columnReadResponseDto.setContent(columnEntity.getContent());
            columnReadResponseDto.setIsBestColumn(columnEntity.isBestColumn());
            columnReadResponseDto.setLikeCount(columnEntity.getLikeCount());
            columnReadResponseDto.setCommentCount(columnEntity.getCommentCount());
            columnReadResponseDto.setCategoryId1(!columnCategories.isEmpty() ? columnCategories.get(0).getCategoryId().getCategoryName() : null);
            columnReadResponseDto.setCategoryId2(columnCategories.size() > 1 ? columnCategories.get(1).getCategoryId().getCategoryName() : null);
            columnReadResponseDto.setCategoryId3(columnCategories.size() > 2 ? columnCategories.get(2).getCategoryId().getCategoryName() : null);
            columnReadResponseDtos.add(columnReadResponseDto);
        }
        return columnReadResponseDtos;
    }
}
