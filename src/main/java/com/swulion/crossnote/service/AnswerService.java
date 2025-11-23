package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Question.AnswerCreateDto;
import com.swulion.crossnote.dto.Question.AnswerResponseDto;
import com.swulion.crossnote.dto.Question.AnswerUpdateDto;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.AnswerRepository;
import com.swulion.crossnote.repository.QuestionRepository;
import com.swulion.crossnote.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AnswerResponseDto createAnswer(Long userId, AnswerCreateDto answerCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Question question = questionRepository.findById(answerCreateDto.getQuestionId()).orElseThrow(
                () -> new RuntimeException("Question not found")
        );

        Answer answer = new Answer();
        answer.setAnswererID(user);
        answer.setQuestionId(question);
        answer.setContent(answerCreateDto.getContent());
        answer.setCreatedAt(LocalDateTime.now());
        answerRepository.save(answer);

        question.setAnswerCount(question.getAnswerCount() + 1);
        questionRepository.save(question);

        Long questionUserId = question.getQuestionerId().getUserId();
        if(!user.getUserId().equals(questionUserId)) {
            String message = user.getName() + " 님이 내 질문에 답변을 남겼어요.";
            notificationService.sendNotification(questionUserId, userId, "QnA", question.getQuestionId(), message);
        }


        AnswerResponseDto answerResponseDto = new AnswerResponseDto();
        answerResponseDto.setAnswerer(user.getName());
        answerResponseDto.setAnswerId(answer.getAnswerId());
        answerResponseDto.setContent(answer.getContent());
        answerResponseDto.setCreatedAt(answer.getCreatedAt());
        answerResponseDto.setUpdatedAt(answer.getUpdatedAt());
        return answerResponseDto;
    }

    public List<AnswerResponseDto> getAnswers(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question not found")
        );
        List<Answer> answers = answerRepository.findAllByQuestionId(question);
        List<AnswerResponseDto> answerResponseDtos = new ArrayList<>();
        if (answers.isEmpty()) {
            return answerResponseDtos;
        }else{
            for (Answer answer : answers) {
                AnswerResponseDto answerResponseDto = new AnswerResponseDto();
                answerResponseDto.setAnswerId(answer.getAnswerId());
                answerResponseDto.setAnswerer(answer.getAnswererID().getName());
                answerResponseDto.setContent(answer.getContent());
                answerResponseDto.setCreatedAt(answer.getCreatedAt());
                answerResponseDtos.add(answerResponseDto);
            }
            return answerResponseDtos;
        }
    }

    public AnswerResponseDto updateAnswer(Long userId, AnswerUpdateDto answerUpdateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        Answer answer = answerRepository.findById(answerUpdateDto.getAnswerId()).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );
        User answerUser = answer.getAnswererID();
        if (!user.equals(answerUser)) {
            throw new RuntimeException("작성자만 수정할 수 있습니다.");
        }

        answer.setContent(answerUpdateDto.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        answerRepository.save(answer);
        AnswerResponseDto answerResponseDto = new AnswerResponseDto();
        answerResponseDto.setAnswerId(answer.getAnswerId());
        answerResponseDto.setContent(answer.getContent());
        answerResponseDto.setAnswerer(answerUser.getName());
        answerResponseDto.setCreatedAt(answer.getCreatedAt());
        answerResponseDto.setUpdatedAt(answer.getUpdatedAt());
        return answerResponseDto;
    }

    public String deleteAnswer(Long userId, Long answerId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        User answerUser = answer.getAnswererID();
        if (!user.equals(answerUser)) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }
        answerRepository.delete(answer);
        Question question = answer.getQuestionId();
        question.setAnswerCount(question.getAnswerCount() - 1);
        return "Answer 삭제 완료";

    }
}
