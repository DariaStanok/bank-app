package ru.practicum.notifications.service;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.notifications.model.Notification;
import ru.practicum.notifications.repository.NotificationRepository;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.web.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class NotificationsServiceImpl implements NotificationsService {

	  private final NotificationRepository notificationRepository;
	  private final ModelMapper modelMapper;

	  @Override
	    public void send(SendNotificationRequest notificationRequest) {
	        if (notificationRequest == null|| notificationRequest.event() == null || isBlank(notificationRequest.message())) {
	            throw new BadRequestException("VALIDATION_ERROR");
	        }
	        if (notificationRequest.userId() == null && isBlank(notificationRequest.operationId())) {
	            throw new BadRequestException("USER_OR_OPERATION_REQUIRED");
	        }

	        Notification notification = modelMapper.map(notificationRequest, Notification.class);
	        notification.setCreatedAt(Instant.now());
	        notificationRepository.save(notification);
	    }

	    @Override
	    public List<SendNotificationRequest> getRecent(Long userId, int limit) {
	        if (userId == null) {
	            throw new BadRequestException("USER_ID_REQUIRED");
	        }
	        int lim = (limit <= 0) ? 10 : Math.min(limit, 100);

	        List<Notification> items = notificationRepository
	                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, lim));

	        return items.stream()
	                .map(n -> modelMapper.map(n, SendNotificationRequest.class))
	                .toList();
	    }

	    private static boolean isBlank(String s) {
	        return s == null || s.trim().isEmpty();
	    }
}