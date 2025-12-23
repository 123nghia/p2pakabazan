package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.event.ChatMessageEvent;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.TradeChatReadRepository;
import com.akabazan.repository.TradeChatRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.entity.TradeChat;
import com.akabazan.repository.entity.TradeChatRead;
import com.akabazan.repository.entity.User;
import com.akabazan.service.TradeChatService;
import com.akabazan.service.dto.TradeChatResult;
import com.akabazan.service.dto.TradeChatThreadResult;
import com.akabazan.service.dto.TradeMapper;
import com.akabazan.service.dto.TradeResult;
import com.akabazan.service.dto.TradeChatMapper;
import com.akabazan.service.event.ChatEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TradeChatServiceImpl implements TradeChatService {

    private final TradeRepository tradeRepository;
    private final TradeChatRepository tradeChatRepository;
    private final ChatEventPublisher chatEventPublisher;
    private final TradeChatReadRepository tradeChatReadRepository;

    private enum RecipientRole {
        BUYER, SELLER, ALL
    }

    public TradeChatServiceImpl(TradeRepository tradeRepository,
            TradeChatRepository tradeChatRepository,
            ChatEventPublisher chatEventPublisher,
            TradeChatReadRepository tradeChatReadRepository) {
        this.tradeRepository = tradeRepository;
        this.tradeChatRepository = tradeChatRepository;
        this.chatEventPublisher = chatEventPublisher;
        this.tradeChatReadRepository = tradeChatReadRepository;
    }

    @Override
    @Transactional
    public TradeChatResult sendMessage(UUID tradeId, String message, String image) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        TradeChat chat = new TradeChat();
        chat.setTrade(trade);
        chat.setSenderId(getCurrentUserId());
        chat.setMessage(message);
        chat.setImage(image);
        chat.setTimestamp(LocalDateTime.now());
        chat.setRecipientRole(RecipientRole.ALL.name());

        TradeChat savedChat = tradeChatRepository.save(chat);

        // Publish chat message event
        publishChatEvent(savedChat, false);

        return TradeChatMapper.toResult(savedChat);
    }

    private void publishChatEvent(TradeChat chat, boolean isSystemMessage) {
        if (chat == null || chat.getTrade() == null) {
            return;
        }
        Instant timestamp = chat.getTimestamp() != null
                ? chat.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()
                : Instant.now();

        ChatMessageEvent event = new ChatMessageEvent(
                chat.getTrade().getId(),
                chat.getId(),
                chat.getSenderId(),
                chat.getMessage(),
                chat.getImage(),
                chat.getRecipientRole(),
                isSystemMessage,
                timestamp,
                Instant.now());
        chatEventPublisher.publish(event);
    }

    @Override
    public List<TradeChatResult> getMessages(UUID tradeId) {
        return getMessages(tradeId, null);
    }

    @Override
    public List<TradeChatResult> getMessages(UUID tradeId, LocalDateTime since) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));
        UUID currentUserId = getCurrentUserId();
        RecipientRole viewerRole = determineUserRoleForTrade(trade, currentUserId);
        LocalDateTime lastReadAtBefore = Optional.ofNullable(
                        tradeChatReadRepository.findExisting(tradeId, currentUserId).orElse(null))
                .map(TradeChatRead::getLastReadAt)
                .orElse(LocalDateTime.MIN);

        List<TradeChat> chats;
        if (since != null) {
            chats = tradeChatRepository.findByTradeIdAndTimestampAfterOrderByTimestampAsc(tradeId, since);
        } else {
            chats = tradeChatRepository.findByTradeIdOrderByTimestampAsc(tradeId);
        }

        List<TradeChatResult> results = chats.stream()
                .filter(chat -> isVisibleForRecipient(chat.getRecipientRole(), viewerRole))
                .map(chat -> {
                    TradeChatResult dto = TradeChatMapper.toResult(chat);
                    boolean read = currentUserId.equals(chat.getSenderId())
                            || (chat.getTimestamp() != null && !chat.getTimestamp().isAfter(lastReadAtBefore));
                    dto.setRead(read);
                    return dto;
                })
                .collect(Collectors.toList());

        // auto mark all fetched as read for current user
        upsertReadMark(trade, currentUserId);
        return results;
    }

    private void upsertReadMark(Trade trade, UUID userId) {
        TradeChatRead record = tradeChatReadRepository.findExisting(trade.getId(), userId)
                .orElseGet(() -> {
                    TradeChatRead r = new TradeChatRead();
                    r.setTrade(trade);
                    r.setUserId(userId);
                    return r;
                });
        record.setLastReadAt(LocalDateTime.now());
        tradeChatReadRepository.save(record);
    }

    @Override
    public List<TradeChatThreadResult> getChatThreadsForCurrentUser() {
        UUID userId = getCurrentUserId();
        List<Trade> trades = tradeRepository.findTradesWithChatsByUser(userId);
        if (trades.isEmpty()) {
            return List.of();
        }

        List<UUID> tradeIds = trades.stream()
                .map(Trade::getId)
                .toList();

        Map<UUID, TradeChatResult> lastMessages = tradeChatRepository.findLatestMessagesByTradeIds(tradeIds)
                .stream()
                .collect(Collectors.toMap(
                        chat -> chat.getTrade().getId(),
                        chat -> TradeChatMapper.toResult(chat),
                        (existing, ignored) -> existing));

        List<TradeChatThreadResult> threads = trades.stream()
                .map(trade -> {
                    TradeChatThreadResult thread = new TradeChatThreadResult();
                    TradeResult tradeResult = TradeMapper.toResult(trade);
                    thread.setTrade(tradeResult);
                    String counterpartyName = resolveCounterpartyName(trade, userId);
                    tradeResult.setCounterparty(counterpartyName);
                    thread.setCounterpartyName(counterpartyName);
                    RecipientRole viewerRole = determineUserRoleForTrade(trade, userId);
                    TradeChatResult lastVisibleMessage = resolveLastVisibleMessage(
                            trade.getId(),
                            viewerRole,
                            lastMessages.get(trade.getId()));
                    thread.setLastMessage(lastVisibleMessage);
                    return thread;
                })
                .collect(Collectors.toList());

        threads.sort(Comparator
                .comparing((TradeChatThreadResult thread) -> thread.getLastMessage() != null
                        ? thread.getLastMessage().getTimestamp()
                        : LocalDateTime.MIN)
                .reversed());

        return threads;
    }

    private String resolveCounterpartyName(Trade trade, UUID currentUserId) {
        if (trade == null) {
            return null;
        }
        if (trade.getBuyer() != null && trade.getBuyer().getId().equals(currentUserId)) {
            return extractDisplayName(trade.getSeller());
        }
        if (trade.getSeller() != null && trade.getSeller().getId().equals(currentUserId)) {
            return extractDisplayName(trade.getBuyer());
        }
        return extractDisplayName(trade.getSeller() != null ? trade.getSeller() : trade.getBuyer());
    }

    private RecipientRole determineUserRoleForTrade(Trade trade, UUID userId) {
        if (trade == null || userId == null) {
            return RecipientRole.ALL;
        }
        if (trade.getBuyer() != null && userId.equals(trade.getBuyer().getId())) {
            return RecipientRole.BUYER;
        }
        if (trade.getSeller() != null && userId.equals(trade.getSeller().getId())) {
            return RecipientRole.SELLER;
        }
        return RecipientRole.ALL;
    }

    private boolean isVisibleForRecipient(String recipientRole, RecipientRole viewerRole) {
        if (recipientRole == null || recipientRole.isBlank()) {
            return true;
        }
        if (RecipientRole.ALL.name().equalsIgnoreCase(recipientRole)) {
            return true;
        }
        if (viewerRole == null) {
            return true;
        }
        return recipientRole.equalsIgnoreCase(viewerRole.name());
    }

    private TradeChatResult resolveLastVisibleMessage(UUID tradeId,
            RecipientRole viewerRole,
            TradeChatResult candidate) {
        if (candidate != null && isVisibleForRecipient(candidate.getRecipientRole(), viewerRole)) {
            return candidate;
        }

        TradeChat latestVisible = null;
        List<TradeChat> chats = tradeChatRepository.findByTradeIdOrderByTimestampAsc(tradeId);
        for (TradeChat chat : chats) {
            if (isVisibleForRecipient(chat.getRecipientRole(), viewerRole)) {
                latestVisible = chat;
            }
        }
        return latestVisible != null ? TradeChatMapper.toResult(latestVisible) : null;
    }

    private String extractDisplayName(User user) {
        if (user == null) {
            return null;
        }
        String email = user.getEmail();
        if (email == null) {
            return null;
        }
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private UUID getCurrentUserId() {
        try {
            return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(ErrorCode.USER_NOT_FOUND);
        }
    }
}
