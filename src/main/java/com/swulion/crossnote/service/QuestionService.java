package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Question.*;
import com.swulion.crossnote.entity.*;
import com.swulion.crossnote.entity.Curation.Like;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.entity.QA.QuestionCategory;
import com.swulion.crossnote.repository.*;
import com.swulion.crossnote.repository.Curation.LikeRepository;
import com.swulion.crossnote.repository.QnA.AnswerRepository;
import com.swulion.crossnote.repository.QnA.QuestionCategoryRepository;
import com.swulion.crossnote.repository.QnA.QuestionRepository;
import com.swulion.crossnote.repository.QnA.QuestionRepositoryImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerRepository answerRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final AnswerService answerService;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;
    private final QuestionRepositoryImpl questionRepositoryImpl;

    /* 질문 생성 로직 */
    @Transactional
    public QuestionResponseDto createQuestion(Long userId, QuestionRequestDto questionRequestDto) {
        Question question = new Question();
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setTitle(questionRequestDto.getTitle());
        question.setContent(questionRequestDto.getContent());
        question.setLikeCount(0);

        User questionerId = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        question.setQuestionerId(questionerId);
        questionRepository.save(question);

        List<Category> categories = getCategories(question, questionRequestDto.getCategory1(), questionRequestDto.getCategory2(), questionRequestDto.getCategory3());

        String category1 = !categories.isEmpty() ? categories.get(0).getCategoryName() : null;
        String category2 = categories.size() > 1 ? categories.get(1).getCategoryName() : null;
        String category3 = categories.size() > 2 ? categories.get(2).getCategoryName() : null;



        return new QuestionResponseDto(question.getQuestionId(),
                questionerId.getUserId(), question.getTitle(), question.getContent(),
                question.getLikeCount(), question.getAnswerCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);

    }

    /* Question 전체 보기 (홈) */
    public List<QuestionResponseDto> getQnaHome(String sort){
        List<Question> questions = questionRepository.findAllByOrderByCreatedAtDesc();
        if(sort.equals("popular")){
            questions = questionRepository.findAllByOrderByLikeCountDesc();
        }
        else if(sort.equals("comment")){
            questions = questionRepository.findAllByOrderByAnswerCountDesc();
        }
        List<QuestionResponseDto> questionListDtos = new ArrayList<>();
        for (Question question : questions) {
            List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(question);
            questionListDtos.add(
                    new QuestionResponseDto(
                            question.getQuestionId(),
                            question.getQuestionerId().getUserId(),
                            question.getTitle(),
                            question.getContent(),
                            question.getLikeCount(),
                            question.getAnswerCount(),
                            question.getCreatedAt(),
                            question.getUpdatedAt(),
                            questionCategories.get(0).getCategoryId().getCategoryName(),
                            questionCategories.size() > 1  ? questionCategories.get(1).getCategoryId().getCategoryName() : null,
                            questionCategories.size() > 2  ? questionCategories.get(2).getCategoryId().getCategoryName() : null
                    )
            );
        }
        return questionListDtos;
    }

    /* Question 수정 */
    @Transactional
    public QuestionResponseDto updateQuestion(Long userId, QuestionUpdateDto questionUpdateDto) {
        User questionerId = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Question question = questionRepository.findById(questionUpdateDto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question Not Found"));

        question.setTitle(questionUpdateDto.getTitle());
        question.setContent(questionUpdateDto.getContent());
        question.setUpdatedAt(LocalDateTime.now());

        List<QuestionCategory> questionCategoriesInDto = new ArrayList<>();
        questionCategoriesInDto.add(questionCategoryRepository.findById(questionUpdateDto.getCategory1()).orElse(null));
        if (questionUpdateDto.getCategory2() != null) questionCategoriesInDto.add(questionCategoryRepository.findById(questionUpdateDto.getCategory2()).orElse(null));
        if (questionUpdateDto.getCategory3() != null) questionCategoriesInDto.add(questionCategoryRepository.findById(questionUpdateDto.getCategory3()).orElse(null));

        List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(question);
        QuestionResponseDto questionResponseDto = getQuestionResponseDto(questionCategories, questionerId, question);

        List<Long> dtoCatIds = questionCategoriesInDto.stream()
                .filter(Objects::nonNull)   // null 제거
                .map(qc -> qc.getCategoryId().getCategoryId())
                .toList();

        List<Long> dbCatIds = questionCategories.stream()
                .filter(Objects::nonNull)
                .map(qc -> qc.getCategoryId().getCategoryId())
                .toList();


        if (dtoCatIds.equals(dbCatIds)) {
            return questionResponseDto;
        }else{
            questionCategoryRepository.deleteByQuestionId(questionUpdateDto.getQuestionId());
            List<Category> categories = getCategories(question, questionUpdateDto.getCategory1(), questionUpdateDto.getCategory2(), questionUpdateDto.getCategory3());

            String category1 = !categories.isEmpty() ? categories.get(0).getCategoryName() : null;
            String category2 = categories.size() > 1 ? categories.get(1).getCategoryName() : null;
            String category3 = categories.size() > 2 ? categories.get(2).getCategoryName() : null;


            return new QuestionResponseDto(question.getQuestionId(), questionerId.getUserId(), question.getTitle(), question.getContent(),
                    question.getLikeCount(), question.getAnswerCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);

        }
    }

    /* Helper Method */
    private List<Category> getCategories(Question question, Long id1, Long id2, Long id3) {

        List<Category> categories = new ArrayList<>();

        if (id1 != null) categories.add(categoryRepository.findByCategoryId(id1));
        if (id2 != null) categories.add(categoryRepository.findByCategoryId(id2));
        if (id3 != null) categories.add(categoryRepository.findByCategoryId(id3));

        for (Category category : categories) {
            QuestionCategory qc = new QuestionCategory();
            qc.setCategoryId(category);
            qc.setQuestionId(question);
            qc.setCreatedAt(LocalDateTime.now());
            questionCategoryRepository.save(qc);
        }

        return categories;
    }

    /* Question 삭제 */
    @Transactional
    public String deleteQuestion(Long userId, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question Not Found")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );
        User questionUser = question.getQuestionerId();
        if(!user.equals(questionUser)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }
        questionCategoryRepository.deleteAllByQuestionId(question);
        questionRepository.delete(question);
        return "Question 삭제 완료";
    }

    /* Question 상세 보기 + Answer */
    public QuestionDetailGetDto getQuestionDetail(Long userId, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question Not Found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );
        User questionerId = question.getQuestionerId();

        List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(question);
        QuestionResponseDto questionResponseDto = getQuestionResponseDto(questionCategories, questionerId, question);

        List<AnswerResponseDto> answerResponseDtos = answerService.getAnswers(question.getQuestionId());

        QuestionDetailGetDto questionDetailGetDto = new QuestionDetailGetDto();
        questionDetailGetDto.setQuestion(questionResponseDto);
        questionDetailGetDto.setAnswers(answerResponseDtos);
        return questionDetailGetDto;

    }

    /* Helper Method */
    private static QuestionResponseDto getQuestionResponseDto(List<QuestionCategory> questionCategories, User questionerId, Question question) {
        String category1 = !questionCategories.isEmpty()? questionCategories.get(0).getCategoryId().getCategoryName() : null;
        String category2 = questionCategories.size() > 1? questionCategories.get(1).getCategoryId().getCategoryName() : null;
        String category3 = questionCategories.size() > 2? questionCategories.get(2).getCategoryId().getCategoryName() : null;

        QuestionResponseDto questionResponseDto = new QuestionResponseDto(question.getQuestionId(), questionerId.getUserId(), question.getTitle(), question.getContent(),
                question.getLikeCount(), question.getAnswerCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);
        return questionResponseDto;
    }

    /* Question 좋아요 */
    @Transactional
    public String likeQuestion(Long userId, Long questionId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User Not Found")
        );
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException("Question Not Found")
        );

        User author = question.getQuestionerId();
        if(author.getUserId().equals(userId)){
            return "내가 작성한 QnA에 좋아요를 누를 수 없습니다.";
        }
        if(likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.QUESTION, questionId).isPresent()){
            Like like = likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.QUESTION, questionId).orElseThrow(
                    () -> new EntityNotFoundException("Like Not Found")
            );
            likeRepository.delete(like);
            question.setLikeCount(question.getLikeCount() - 1);
            questionRepository.save(question);
            return "좋아요 취소";
        }
        Like like = new Like(user, ScrapTargetType.QUESTION, questionId);
        likeRepository.save(like);
        question.setLikeCount(question.getLikeCount() + 1);
        questionRepository.save(question);

        String message = user.getName() + "님이  내 QnA에 좋아요를 남겼어요";
        notificationService.sendNotification(author.getUserId(), user.getUserId(), NotificationType.QUESTION, questionId, message);

        return "좋아요 완료";

    }

    /* Question 검색 */
    public List<QuestionResponseDto> searchQuestion(QuestionSearchDto questionSearchDto){
        List<Question> questions = questionRepositoryImpl.findWithKeyword(questionSearchDto.getCategoryId(), questionSearchDto.getKeyword());
        List<QuestionResponseDto> questionResponseDtos = new ArrayList<>();
        for(Question question : questions){
            List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(question);
            QuestionResponseDto questionResponseDto = new QuestionResponseDto(
                    question.getQuestionId(),
                    question.getQuestionerId().getUserId(),
                    question.getTitle(),
                    question.getContent(),
                    question.getLikeCount(),
                    question.getAnswerCount(),
                    question.getCreatedAt(),
                    question.getUpdatedAt(),
                    questionCategories.get(0).getCategoryId().getCategoryName(),
                    questionCategories.size() > 1  ? questionCategories.get(1).getCategoryId().getCategoryName() : null,
                    questionCategories.size() > 2  ? questionCategories.get(2).getCategoryId().getCategoryName() : null
            );
            questionResponseDtos.add(questionResponseDto);
        }
        return questionResponseDtos;

    }
}
