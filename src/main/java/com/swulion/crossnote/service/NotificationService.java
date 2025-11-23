package com.swulion.crossnote.service;

import com.swulion.crossnote.dto.NotificationGetDto;
import com.swulion.crossnote.entity.Notification;
import com.swulion.crossnote.entity.NotificationType;
import com.swulion.crossnote.entity.User;
import com.swulion.crossnote.repository.NotificationRepository;
import com.swulion.crossnote.repository.SseEmitterRepository;
import com.swulion.crossnote.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

@RequiredArgsConstructor
public class NotificationService {

    public final SseEmitterRepository sseEmitterRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter();
        sseEmitterRepository.save(userId, emitter);

        emitter.onCompletion(()->sseEmitterRepository.delete(userId));
        try{
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e){
            emitter.onCompletion(()->sseEmitterRepository.delete(userId));
        }
        return emitter;

    };

    @Transactional
    public void sendNotification(Long receiverId, Long actorId,
                                 NotificationType targetType, Long TargetId, String message) {
        User receiver = userRepository.findById(receiverId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        User actor = userRepository.findById(actorId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setContent(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setTargetType(targetType);
        notification.setTargetId(TargetId);
        notification.setActor(actor);
        notificationRepository.save(notification);

        SseEmitter emitter = sseEmitterRepository.get(receiverId);
        if (emitter == null) return;

        try{
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(message));
        }catch (IOException e){
            emitter.onCompletion(()->sseEmitterRepository.delete(receiverId));
        }
    }

    // 나에게 온 알림 전체 보기
    @Transactional
    public List<NotificationGetDto> getNotifications(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new RuntimeException("User not found")
        );

        List <Notification> notifications = notificationRepository.findTop20ByReceiverOrderByCreatedAtDesc(user);
        List<NotificationGetDto> dtos = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationGetDto notificationGetDto = new NotificationGetDto();
            notificationGetDto.setNotificationId(notification.getNotificationId());
            notificationGetDto.setReceiverName(notification.getReceiver().getName());
            notificationGetDto.setActorName(notification.getActor().getName());
            notificationGetDto.setMessage(notification.getContent());
            notificationGetDto.setRead(notification.isRead());
            notificationGetDto.setTargetType(notification.getTargetType());
            notificationGetDto.setTargetId(notification.getTargetId());
            notificationGetDto.setCreatedAt(notification.getCreatedAt());
            dtos.add(notificationGetDto);
        }

        return dtos;
    }

    // 안 읽은 알림 개수 보기
    public int getUnreadNotificationCount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        List<Notification> notifications = notificationRepository.findTop20ByReceiverOrderByCreatedAtDesc(user);
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) count += 1;
        }
        return count;
    }

    // 안 읽은 알림만 보기
    @Transactional
    public List<NotificationGetDto> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        List <Notification> notifications = notificationRepository.findAllByReceiverAndIsReadFalse(user);
        List<NotificationGetDto> dtos = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationGetDto notificationGetDto = new NotificationGetDto();
            notificationGetDto.setNotificationId(notification.getNotificationId());
            notificationGetDto.setReceiverName(notification.getReceiver().getName());
            notificationGetDto.setActorName(notification.getActor().getName());
            notificationGetDto.setMessage(notification.getContent());
            notificationGetDto.setRead(notification.isRead());
            notificationGetDto.setTargetType(notification.getTargetType());
            notificationGetDto.setTargetId(notification.getTargetId());
            notificationGetDto.setCreatedAt(notification.getCreatedAt());
            dtos.add(notificationGetDto);
        }

        return dtos;
    }
    // 알림 개별 읽음 처리
    @Transactional
    public void readNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                ()->new RuntimeException("Notification not found")
        );
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 알림 전체 읽음 처리
    @Transactional
    public void readAllNotifications(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        List<Notification> notifications = notificationRepository.findAllByReceiverOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> {notification.setRead(true);});
        notificationRepository.saveAll(notifications);
    }

    // 알림 전체 삭제
    @Transactional
    public void deleteNotification(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new RuntimeException("User not found")
        );
        notificationRepository.deleteAllByReceiver(user);
    }


}
