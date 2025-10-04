package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.DisputeRepository;
import com.akabazan.repository.TradeRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.entity.Dispute;
import com.akabazan.repository.entity.Dispute.DisputeStatus;
import com.akabazan.repository.entity.Dispute.ResolutionOutcome;
import com.akabazan.repository.entity.Trade;
import com.akabazan.repository.constant.TradeStatus;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.DisputeService;
import com.akabazan.service.NotificationService;
import com.akabazan.service.dto.DisputeMapper;
import com.akabazan.service.dto.DisputeResult;
import com.akabazan.service.order.support.SellerFundsManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DisputeServiceImpl implements DisputeService {

    private final TradeRepository tradeRepository;
    private final DisputeRepository disputeRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    private final SellerFundsManager sellerFundsManager;


    public DisputeServiceImpl(TradeRepository tradeRepository,
                              DisputeRepository disputeRepository,
                              UserRepository userRepository,
                              CurrentUserService currentUserService,
                              NotificationService notificationService,  
                              SellerFundsManager sellerFundsManager) {
        this.tradeRepository = tradeRepository;
        this.disputeRepository = disputeRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
        this.sellerFundsManager = sellerFundsManager;
    }

    @Override
    @Transactional
    public DisputeResult openDispute(Long tradeId, String reason, String evidence) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        User currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!isParticipant(trade, currentUser)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        if (trade.getStatus() == TradeStatus.DISPUTED) {
            throw new ApplicationException(ErrorCode.ALREADY_IN_DISPUTE);
        }

        trade.setStatus(TradeStatus.DISPUTED);
        tradeRepository.save(trade);

        Dispute dispute = new Dispute();
        dispute.setTrade(trade);
        dispute.setReason(reason);
        dispute.setEvidence(evidence);
        dispute.setStatus(DisputeStatus.OPEN);

        Dispute saved = disputeRepository.save(dispute);

        notifyOnOpen(trade, saved);

        return DisputeMapper.toResult(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisputeResult> getDisputesByTrade(Long tradeId) {
        tradeRepository.findById(tradeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TRADE_NOT_FOUND));

        return disputeRepository.findByTradeId(tradeId)
                .stream()
                .map(DisputeMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisputeResult> getDisputes(DisputeStatus status, boolean onlyAssignedToCurrentAdmin) {
        List<Dispute> disputes;

        if (onlyAssignedToCurrentAdmin) {
            User current = currentUserService.getCurrentUser()
                    .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
            ensureStaff(current);
            disputes = status != null
                    ? disputeRepository.findByStatusAndAssignedAdminOrderByCreatedAtDesc(status, current)
                    : disputeRepository.findByAssignedAdminOrderByCreatedAtDesc(current);
        } else if (status != null) {
            disputes = disputeRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            disputes = disputeRepository.findAll();
        }

        return disputes.stream().map(DisputeMapper::toResult).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DisputeResult assignToCurrentAdmin(Long disputeId) {
        User admin = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        ensureStaff(admin);
        return assign(disputeId, admin);
    }

    @Override
    @Transactional
    public DisputeResult assignToAdmin(Long disputeId, Long adminId) {
        User requester = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        ensureAdmin(requester);

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        ensureStaff(admin);

        return assign(disputeId, admin);
    }

    @Override
    @Transactional
    public DisputeResult resolveDispute(Long disputeId, String outcome, String resolutionNote) {
        Dispute dispute = loadDispute(disputeId);
        User admin = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        ensureStaff(admin);
        ensureAssigned(dispute, admin);

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }

        ResolutionOutcome resolutionOutcome = toOutcome(outcome);

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolutionOutcome(resolutionOutcome);
        dispute.setResolutionNote(resolutionNote);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setAssignedAdmin(admin);

        updateTradeAfterResolution(dispute.getTrade(), resolutionOutcome);

        switch (resolutionOutcome) {
        case BUYER_FAVORED -> {
        // Giải phóng coin cho buyer
        sellerFundsManager.releaseToBuyer(dispute.getTrade());
        }
        case SELLER_FAVORED -> {
        // Trả coin lại cho seller
        sellerFundsManager.refundToSeller(dispute.getTrade());
        }
        }
        Dispute saved = disputeRepository.save(dispute);

        notifyParticipants(dispute, String.format("Dispute #%d resolved in favour of %s", dispute.getId(), resolutionOutcome.name()));

        return DisputeMapper.toResult(saved);
    }

    @Override
    @Transactional
    public DisputeResult rejectDispute(Long disputeId, String resolutionNote) {
        Dispute dispute = loadDispute(disputeId);
        User admin = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        ensureStaff(admin);
        ensureAssigned(dispute, admin);

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }

        dispute.setStatus(DisputeStatus.REJECTED);
        dispute.setResolutionOutcome(ResolutionOutcome.CANCELLED);
        dispute.setResolutionNote(resolutionNote);
        dispute.setResolvedAt(LocalDateTime.now());

        Trade trade = dispute.getTrade();
        if (trade.getStatus() == TradeStatus.DISPUTED) {
            trade.setStatus(TradeStatus.PAID);
            tradeRepository.save(trade);
        }

        Dispute saved = disputeRepository.save(dispute);

        notifyParticipants(dispute, String.format("Dispute #%d was rejected", dispute.getId()));

        return DisputeMapper.toResult(saved);
    }

    private DisputeResult assign(Long disputeId, User admin) {
        Dispute dispute = loadDispute(disputeId);
        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }
        dispute.setAssignedAdmin(admin);
        dispute.setStatus(DisputeStatus.IN_REVIEW);

        Dispute saved = disputeRepository.save(dispute);

        notificationService.notifyUser(admin.getId(),
                String.format("Dispute #%d has been assigned to you", dispute.getId()));
        notifyParticipants(dispute, String.format("Dispute #%d is under review by %s", dispute.getId(), admin.getEmail()));

        return DisputeMapper.toResult(saved);
    }

    private Dispute loadDispute(Long disputeId) {
        return disputeRepository.findByIdWithTrade(disputeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DISPUTE_NOT_FOUND));
    }

    private void ensureStaff(User user) {
        if (user.getRole() != User.Role.ADMIN && user.getRole() != User.Role.MODERATOR) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
    }

    private void ensureAdmin(User user) {
        if (user.getRole() != User.Role.ADMIN) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
    }

    private boolean isParticipant(Trade trade, User user) {
        return trade.getBuyer().getId().equals(user.getId()) || trade.getSeller().getId().equals(user.getId());
    }

    private void ensureAssigned(Dispute dispute, User admin) {
        if (dispute.getAssignedAdmin() == null || !dispute.getAssignedAdmin().getId().equals(admin.getId())) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
    }

    private ResolutionOutcome toOutcome(String outcome) {
        if (outcome == null) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }
        try {
            return ResolutionOutcome.valueOf(outcome.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ApplicationException(ErrorCode.INVALID_DISPUTE_STATUS);
        }
    }

    private void updateTradeAfterResolution(Trade trade, ResolutionOutcome outcome) {
        switch (outcome) {
            case BUYER_FAVORED -> trade.setStatus(TradeStatus.COMPLETED);
            case SELLER_FAVORED -> trade.setStatus(TradeStatus.CANCELLED);
            case CANCELLED -> trade.setStatus(TradeStatus.PAID);
        }
        tradeRepository.save(trade);
    }

    private void notifyOnOpen(Trade trade, Dispute dispute) {
        String message = String.format("Trade #%d has a new dispute (#%d)", trade.getId(), dispute.getId());
        notificationService.notifyUsers(List.of(trade.getBuyer().getId(), trade.getSeller().getId()), message);

        List<User> admins = userRepository.findByRole(User.Role.ADMIN);
        List<Long> adminIds = admins.stream().map(User::getId).toList();
        notificationService.notifyUsers(adminIds, message + ". Please review");

        List<User> moderators = userRepository.findByRole(User.Role.MODERATOR);
        List<Long> moderatorIds = moderators.stream().map(User::getId).toList();
        notificationService.notifyUsers(moderatorIds, message + ". Please review");
    }

    private void notifyParticipants(Dispute dispute, String message) {
        Trade trade = dispute.getTrade();
        notificationService.notifyUsers(List.of(trade.getBuyer().getId(), trade.getSeller().getId()), message);
    }
}
