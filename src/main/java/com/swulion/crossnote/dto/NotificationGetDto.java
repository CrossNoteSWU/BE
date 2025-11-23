package com.swulion.crossnote.dto;

import com.swulion.crossnote.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationGetDto {
    private Long notificationId;
    private String receiverName;
    private String actorName;
    private String message;
    private NotificationType targetType;
    private Long targetId;
    private boolean isRead;
    private LocalDateTime createdAt;

}
