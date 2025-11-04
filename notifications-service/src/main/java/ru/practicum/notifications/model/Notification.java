package ru.practicum.notifications.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.platform.contracts.enums.NotificationEvent;

@Entity
@Table(name = "notifications",
       indexes = {
         @Index(name = "idx_notifications_user_createdat", columnList = "user_id, created_at"),
         @Index(name = "idx_notifications_operation", columnList = "operation_id")
       })
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Notification {

	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Enumerated(EnumType.STRING)
	  @Column(nullable = false)
	  private NotificationEvent event;

	  @Column(nullable = false)
	  private String message;

	  @Column(name = "operation_id")
	  private String operationId;

	  @Column(name = "user_id")
	  private Long userId;

	  @Column(name = "created_at")
	  private Instant createdAt;
}
