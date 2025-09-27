package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.TradeChatRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.TradeChat;
import com.akabazan.service.TradeChatService;
import com.akabazan.service.dto.TradeChatDTO;
import com.akabazan.service.dto.TradeChatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeChatServiceImpl implements TradeChatService {

    private final TradeRepository tradeRepository;
    private final TradeChatRepository tradeChatRepository;

    public TradeChatServiceImpl(TradeRepository tradeRepository,
                                TradeChatRepository tradeChatRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeChatRepository = tradeChatRepository;
    }

    @Override
    @Transactional
    public TradeChatDTO sendMessage(Long tradeId, String message) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        TradeChat chat = new TradeChat();
        chat.setTrade(trade);
        chat.setSenderId(getCurrentUserId());
        chat.setMessage(message);
        chat.setTimestamp(LocalDateTime.now());

        return TradeChatMapper.toDTO(tradeChatRepository.save(chat));
    }

    @Override
    public List<TradeChatDTO> getMessages(Long tradeId) {
        return tradeChatRepository.findByTradeIdOrderByTimestampAsc(tradeId)
                .stream()
                .map(TradeChatMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
