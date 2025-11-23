package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Question.*;
import com.swulion.crossnote.entity.*;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.entity.QA.QuestionCategory;
import com.swulion.crossnote.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerRepository answerRepository;
    private final QuestionCategoryRepository questionCategoryRepository;

    /* 질문 생성 로직 */
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



        return new QuestionResponseDto(questionerId.getUserId(), question.getTitle(), question.getContent(),
                question.getLikeCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);

    }

    /* Question 전체 보기 (홈) */
    public List<QuestionListDto> getQnaHome(){
        List<Question> questions = questionRepository.findAll();
        List<QuestionListDto> questionListDtos = new ArrayList<>();
        for (Question question : questions) {
            List<Answer> answers = answerRepository.findByQuestionId(question);
            questionListDtos.add(
                    new QuestionListDto(
                            question.getQuestionId(),
                            question.getTitle(),
                            question.getContent(),
                            question.getLikeCount(),
                            answers.size()
                    )
            );
        }
        return questionListDtos;
    }

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

        List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(questionUpdateDto.getQuestionId());
        QuestionResponseDto questionResponseDto = getQuestionResponseDto(questionCategories, questionerId, question);

        List<Long> dtoCatIds = questionCategoriesInDto.stream()
                .map(qc -> qc.getCategoryId().getCategoryId())
                .toList();

        List<Long> dbCatIds = questionCategories.stream()
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


            return new QuestionResponseDto(questionerId.getUserId(), question.getTitle(), question.getContent(),
                    question.getLikeCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);

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

        questionRepository.delete(question);
        return "Question 삭제 완료";
    }

    public QuestionDetailGetDto getQuestionDetail(Long userId, Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question Not Found")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );
        User questionerId = question.getQuestionerId();

        List<QuestionCategory> questionCategories = questionCategoryRepository.findAllByQuestionId(question.getQuestionId());
        QuestionResponseDto questionResponseDto = getQuestionResponseDto(questionCategories, questionerId, question);

        List<AnswerResponseDto> answerResponseDtos = new ArrayList<>();
        List<Answer> answers = answerRepository.findAllByQuestionId(question.getQuestionId());
        for (Answer answer : answers) {
            AnswerResponseDto answerResponseDto = new AnswerResponseDto();
            answerResponseDto.setAnswerId(answer.getAnswerId());
            answerResponseDto.setAnswerer(answer.getAnswererID().getName());
            answerResponseDto.setContent(answer.getContent());
            answerResponseDto.setCreatedAt(answer.getCreatedAt());
            answerResponseDto.setUpdatedAt(answer.getUpdatedAt());
            answerResponseDtos.add(answerResponseDto);
        }

        QuestionDetailGetDto questionDetailGetDto = new QuestionDetailGetDto();
        questionDetailGetDto.setQuestion(questionResponseDto);
        questionDetailGetDto.setAnswers(answerResponseDtos);
        return questionDetailGetDto;

    }

    private static QuestionResponseDto getQuestionResponseDto(List<QuestionCategory> questionCategories, User questionerId, Question question) {
        String category1 = !questionCategories.isEmpty()? questionCategories.get(0).getCategoryId().getCategoryName() : null;
        String category2 = questionCategories.size() > 1? questionCategories.get(1).getCategoryId().getCategoryName() : null;
        String category3 = questionCategories.size() > 2? questionCategories.get(2).getCategoryId().getCategoryName() : null;

        QuestionResponseDto questionResponseDto = new QuestionResponseDto(questionerId.getUserId(), question.getTitle(), question.getContent(),
                question.getLikeCount(), question.getCreatedAt(), question.getUpdatedAt(), category1, category2, category3);
        return questionResponseDto;
    }
}
