package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.DisputeRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.entity.Dispute;
import com.akabazan.repository.entity.Trade;
import com.akabazan.service.DisputeService;
import com.akabazan.service.dto.DisputeDTO;
import com.akabazan.service.dto.DisputeMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DisputeServiceImpl implements DisputeService {

    private final TradeRepository tradeRepository;
    private final DisputeRepository disputeRepository;

    public DisputeServiceImpl(TradeRepository tradeRepository,
                              DisputeRepository disputeRepository) {
        this.tradeRepository = tradeRepository;
        this.disputeRepository = disputeRepository;
    }

    @Override
    @Transactional
    public DisputeDTO openDispute(Long tradeId, String reason, String evidence) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        if (trade.getStatus().name().equals("DISPUTED"))
            throw new ApplicationException(ErrorCode.ALREADY_IN_DISPUTE);

        trade.setStatus(com.akabazan.repository.constant.TradeStatus.DISPUTED);
        tradeRepository.save(trade);

        Dispute dispute = new Dispute();
        dispute.setTrade(trade);
        dispute.setReason(reason);
        dispute.setEvidence(evidence);
        dispute.setCreatedAt(LocalDateTime.now());

        return DisputeMapper.toDTO(disputeRepository.save(dispute));
    }
}
