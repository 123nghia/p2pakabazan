package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.NotificationRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.Notification;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.NotificationService;
import com.akabazan.service.dto.NotificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public void notifyUser(Long userId, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        userRepository.findById(userId).ifPresent(user -> {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notificationRepository.save(notification);
        });
    }

    @Override
    public void notifyUsers(List<Long> userIds, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        List<User> users = userRepository.findAllById(userIds.stream().distinct().collect(Collectors.toList()));
        if (users.isEmpty()) {
            return;
        }
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notificationRepository.save(notification);
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
    public void markAsRead(Long notificationId) {
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
