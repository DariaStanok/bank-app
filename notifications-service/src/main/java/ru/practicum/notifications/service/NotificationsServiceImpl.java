package ru.practicum.notifications.service;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.practicum.notifications.model.Notification;
import ru.practicum.notifications.repository.NotificationRepository;
import ru.practicum.platform.contracts.notifications.NotificationMessageDto;
import ru.practicum.platform.contracts.notifications.SendNotificationRequest;
import ru.practicum.web.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class NotificationsServiceImpl implements NotificationsService {

	  private final NotificationRepository notificationRepository;
	  private final ModelMapper modelMapper;
	  

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

	    @Override
	    @KafkaListener(
	            topics = "${app.kafka.notifications-topic}",
	            groupId = "${app.kafka.consumer.group-id}"
	    )
		public void handleNotification(NotificationMessageDto msg) {
			if (msg == null || msg.event() == null || isBlank(msg.message())) {
	            throw new BadRequestException("INVALID_NOTIFICATION_MESSAGE");
	        }

	        Notification entity = modelMapper.map(msg, Notification.class);
	        entity.setCreatedAt(
	                msg.at() != null ? msg.at() : Instant.now()
	        );

	        notificationRepository.save(entity);
	       
	    }
	    
	    private static boolean isBlank(String s) {
	        return s == null || s.trim().isEmpty();
	    }

}