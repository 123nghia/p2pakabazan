package com.akabazan.notification.service;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.event.NotificationEvent;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.notification.dto.NotificationResult;
import com.akabazan.notification.entity.Notification;
import com.akabazan.notification.event.NotificationEventPublisher;
import com.akabazan.notification.repository.NotificationRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.akabazan.notification.enums.NotificationType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationEventPublisher notificationEventPublisher;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService,
            NotificationEventPublisher notificationEventPublisher) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @Override
    public void notifyUser(UUID userId, NotificationType type, String message) {
        if (message == null || message.isBlank())
            return;
        userRepository.findById(userId).ifPresent(user -> {
            Notification n = new Notification();
            n.setUser(user);
            n.setType(type == null ? NotificationType.GENERIC : type);
            n.setMessage(message);
            Notification savedNotification = notificationRepository.save(n);

            // Publish notification event
            publishNotificationEvent(savedNotification);
        });
    }

    @Override
    public void notifyUsers(List<UUID> userIds, NotificationType type, String message) {
        if (message == null || message.isBlank() || userIds == null || userIds.isEmpty())
            return;
        List<User> users = userRepository.findAllById(userIds.stream().distinct().collect(Collectors.toList()));
        if (users.isEmpty())
            return;
        for (User u : users) {
            Notification n = new Notification();
            n.setUser(u);
            n.setType(type == null ? NotificationType.GENERIC : type);
            n.setMessage(message);
            Notification savedNotification = notificationRepository.save(n);

            // Publish notification event
            publishNotificationEvent(savedNotification);
        }
    }

    private void publishNotificationEvent(Notification notification) {
        if (notification == null || notification.getUser() == null) {
            return;
        }
        Instant occurredAt = notification.getCreatedAt() != null
                ? notification.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                : Instant.now();

        NotificationEvent event = new NotificationEvent(
                notification.getUser().getId(),
                notification.getId(),
                notification.getType() != null ? notification.getType().name() : NotificationType.GENERIC.name(),
                notification.getMessage(),
                occurredAt);
        notificationEventPublisher.publish(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResult> getCurrentUserNotifications(boolean unreadOnly) {
        User user = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = unreadOnly
                ? notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user)
                : notificationRepository.findByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(this::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOTIFICATION_NOT_FOUND));

        User user = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResult toResult(Notification notification) {
        NotificationResult result = new NotificationResult();
        result.setId(notification.getId());
        result.setMessage(notification.getMessage());
        result.setRead(notification.isRead());
        // Convert UTC to Vietnam timezone (+7)
        if (notification.getCreatedAt() != null) {
            java.time.ZonedDateTime utcTime = notification.getCreatedAt().atZone(java.time.ZoneOffset.UTC);
            java.time.ZonedDateTime vietnamTime = utcTime.withZoneSameInstant(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
            result.setCreatedAt(vietnamTime.toLocalDateTime());
        } else {
            result.setCreatedAt(null);
        }
        return result;
    }
}
