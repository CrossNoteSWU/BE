package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.Question.AnswerCreateDto;
import com.swulion.crossnote.dto.Question.AnswerResponseDto;
import com.swulion.crossnote.dto.Question.AnswerUpdateDto;
import com.swulion.crossnote.entity.Curation.Like;
import com.swulion.crossnote.entity.Curation.ScrapTargetType;
import com.swulion.crossnote.entity.NotificationType;
import com.swulion.crossnote.entity.QA.Answer;
import com.swulion.crossnote.entity.QA.Question;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.QnA.AnswerRepository;
import com.swulion.crossnote.repository.Curation.LikeRepository;
import com.swulion.crossnote.repository.QnA.QuestionRepository;
import com.swulion.crossnote.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final LikeRepository likeRepository;

    @Transactional
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
            String message = user.getName() + "님이 내 QnA에 답글을 남겼어요.";
            notificationService.sendNotification(questionUserId, userId, NotificationType.ANSWER, question.getQuestionId(), message);
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

    @Transactional
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

    @Transactional
    public String deleteAnswer(Long userId, Long answerId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new RuntimeException("Answer not found")
        );

        User answerUser = answer.getAnswererID();
        if (!user.equals(answerUser)) {
            return ("작성자만 삭제할 수 있습니다.");
        }
        answerRepository.delete(answer);
        Question question = answer.getQuestionId();
        question.setAnswerCount(question.getAnswerCount() - 1);
        return "Answer 삭제 완료";

    }

    @Transactional
    public String likeAnswer(Long userId, Long answerId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User Not Found")
        );
        Answer answer = answerRepository.findById(answerId).orElseThrow(
                () -> new EntityNotFoundException("Answer Not Found")
        );

        User author = answer.getAnswererID();
        if(author.getUserId().equals(userId)){
            return "내가 작성한 답변에 좋아요를 누를 수 없습니다.";
        }
        if(likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.ANSWER, answerId).isPresent()){
            Like like = likeRepository.findByUserAndTargetTypeAndTargetId(user, ScrapTargetType.ANSWER, answerId).orElseThrow(
                    () -> new EntityNotFoundException("Like Not Found")
            );
            likeRepository.delete(like);
            answer.setLikeCount(answer.getLikeCount() - 1);
            answerRepository.save(answer);
            return "좋아요 취소";
        }
        Like like = new Like(user, ScrapTargetType.ANSWER, answerId);
        likeRepository.save(like);

        answer.setLikeCount(answer.getLikeCount() + 1);
        answerRepository.save(answer);

        String message = user.getName() + "님이 내가 남긴 답글에 좋아요를 남겼어요.";
        notificationService.sendNotification(author.getUserId(), user.getUserId(), NotificationType.ANSWER, answerId, message);

        return "좋아요 완료";

    }
}
