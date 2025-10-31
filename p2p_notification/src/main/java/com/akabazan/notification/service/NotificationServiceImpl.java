package com.akabazan.notification.service;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.notification.dto.NotificationResult;
import com.akabazan.notification.entity.Notification;
import com.akabazan.notification.repository.NotificationRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.akabazan.notification.enums.NotificationType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                    UserRepository userRepository,
                                    CurrentUserService currentUserService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public void notifyUser(UUID userId, NotificationType type, String message) {
      if (message == null || message.isBlank()) return;
        userRepository.findById(userId).ifPresent(user -> {
            Notification n = new Notification();
            n.setUser(user);
            n.setType(type == null ? NotificationType.GENERIC : type);
            n.setMessage(message);
            notificationRepository.save(n);
        });
    }

       @Override
    public void notifyUsers(List<UUID> userIds, NotificationType type, String message) {
        if (message == null || message.isBlank() || userIds == null || userIds.isEmpty()) return;
        List<User> users = userRepository.findAllById(userIds.stream().distinct().collect(Collectors.toList()));
        if (users.isEmpty()) return;
        for (User u : users) {
            Notification n = new Notification();
            n.setUser(u);
            n.setType(type == null ? NotificationType.GENERIC : type);
            n.setMessage(message);
            notificationRepository.save(n);
        }
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
        result.setCreatedAt(notification.getCreatedAt());
        return result;
    }
}
