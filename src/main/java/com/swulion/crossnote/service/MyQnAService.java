package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.MyPage.MyQnAResponseDto;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.repository.QnA.AnswerRepository;
import com.swulion.crossnote.repository.QnA.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyQnAService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public List<MyQnAResponseDto> getMyQnA(Long userId, String type) {
        List<Question> questions;
        
        if ("question".equals(type)) {
            // 본인이 작성한 질문만 조회
            questions = questionRepository.findAll().stream()
                    .filter(q -> q.getQuestionerId() != null && q.getQuestionerId().getUserId().equals(userId))
                    .collect(Collectors.toList());
        } else if ("answer".equals(type)) {
            // 본인이 답변한 질문들만 조회
            List<Answer> myAnswers = answerRepository.findAll().stream()
                    .filter(a -> a.getAnswererID() != null && a.getAnswererID().getUserId().equals(userId))
                    .collect(Collectors.toList());
            
            Set<Long> questionIds = myAnswers.stream()
                    .map(a -> a.getQuestionId().getQuestionId())
                    .collect(Collectors.toSet());
            
            questions = questionRepository.findAllById(questionIds);
        } else {
            // 전체 조회: 본인이 작성한 질문 + 본인이 답변한 질문
            // 본인이 작성한 질문
            List<Question> myQuestions = questionRepository.findAll().stream()
                    .filter(q -> q.getQuestionerId() != null && q.getQuestionerId().getUserId().equals(userId))
                    .collect(Collectors.toList());
            
            // 본인이 답변한 질문
            List<Answer> myAnswers = answerRepository.findAll().stream()
                    .filter(a -> a.getAnswererID() != null && a.getAnswererID().getUserId().equals(userId))
                    .collect(Collectors.toList());
            
            Set<Long> answeredQuestionIds = myAnswers.stream()
                    .map(a -> a.getQuestionId().getQuestionId())
                    .collect(Collectors.toSet());
            
            // 합치고 중복 제거
            Set<Long> allQuestionIds = myQuestions.stream()
                    .map(Question::getQuestionId)
                    .collect(Collectors.toSet());
            allQuestionIds.addAll(answeredQuestionIds);
            
            questions = questionRepository.findAllById(allQuestionIds);
        }

        return questions.stream()
                .map(question -> {
                    List<Answer> answers = answerRepository.findByQuestionId(question);
                    List<MyQnAResponseDto.AnswerDto> answerDtos = answers.stream()
                            .map(answer -> new MyQnAResponseDto.AnswerDto(
                                    answer.getAnswerId(),
                                    answer.getContent(),
                                    answer.getCreatedAt(),
                                    answer.getAnswererID() != null ? answer.getAnswererID().getUserId() : null
                            ))
                            .collect(Collectors.toList());

                    return new MyQnAResponseDto(
                            question.getQuestionId(),
                            question.getTitle(),
                            question.getContent(),
                            question.getCreatedAt(),
                            answerDtos
                    );
                })
                .collect(Collectors.toList());
    }
}

