package com.swulion.crossnote.controller;

import com.swulion.crossnote.dto.NotificationGetDto;
import com.swulion.crossnote.entity.Notification;
import com.swulion.crossnote.service.CustomUserDetails;
import com.swulion.crossnote.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationGetDto>> getNotificationAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        List<NotificationGetDto> notifications = notificationService.getNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationGetDto>> getNotificationUnread(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        List<NotificationGetDto> notifications = notificationService.getUnreadNotifications(userId);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/me/count")
    public ResponseEntity<Integer> getNotificationCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        Integer count = notificationService.getUnreadNotificationCount(userId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<?> readNotification(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long notificationId) {
        Long userId = userDetails.getUser().getUserId();
        notificationService.readNotification(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<?> deleteNotification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        notificationService.deleteNotification(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/readAll")
    public ResponseEntity<?> readAllNotification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        notificationService.readAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
}
