package ru.practicum.notifications.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.notifications.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
	}
