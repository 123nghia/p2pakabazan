package com.akabazan.api.mapper;

import com.akabazan.api.dto.NotificationResponse;
import com.akabazan.service.dto.NotificationResult;

import java.util.List;
import java.util.stream.Collectors;

public final class NotificationResponseMapper {

    private NotificationResponseMapper() {
    }

    public static NotificationResponse from(NotificationResult result) {
        NotificationResponse response = new NotificationResponse();
        response.setId(result.getId());
        response.setMessage(result.getMessage());
        response.setRead(result.isRead());
        response.setCreatedAt(result.getCreatedAt());
        return response;
    }

    public static List<NotificationResponse> fromList(List<NotificationResult> results) {
        return results.stream().map(NotificationResponseMapper::from).collect(Collectors.toList());
    }
}
