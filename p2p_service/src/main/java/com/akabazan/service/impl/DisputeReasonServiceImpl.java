package com.akabazan.service.impl;

import com.akabazan.repository.DisputeReasonRepository;
import com.akabazan.repository.entity.DisputeReason;
import com.akabazan.service.DisputeReasonService;
import com.akabazan.service.dto.response.DisputeReasonResponse;
import com.akabazan.service.mapper.DisputeReasonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DisputeReasonServiceImpl implements DisputeReasonService {

    private final DisputeReasonRepository disputeReasonRepository;
    private final DisputeReasonMapper disputeReasonMapper;

    @Override
    public List<DisputeReasonResponse> getDisputeReasons(String role) {
        List<DisputeReason> disputeReasons;
        if (StringUtils.hasText(role)) {
            disputeReasons = disputeReasonRepository.findAllByRole(role);
        } else {
            disputeReasons = disputeReasonRepository.findAll();
        }
        return disputeReasonMapper.toDto(disputeReasons);
    }
}
