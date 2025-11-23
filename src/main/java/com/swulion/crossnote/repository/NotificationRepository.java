package com.swulion.crossnote.repository;

import com.swulion.crossnote.entity.Notification;
import com.swulion.crossnote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiverOrderByCreatedAtDesc(User receiver);

    List<Notification> findAllByReceiverAndIsReadFalse(User receiver);

    void deleteAllByReceiver(User user);

    List<Notification> findTop20ByReceiverOrderByCreatedAtDesc(User user);
}
