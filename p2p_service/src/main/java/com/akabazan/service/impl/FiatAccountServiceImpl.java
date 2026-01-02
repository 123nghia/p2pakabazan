package com.akabazan.service.impl;

import com.akabazan.common.constant.ErrorCode;
import com.akabazan.common.exception.ApplicationException;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.User;
import com.akabazan.service.CurrentUserService;
import com.akabazan.service.FiatAccountService;
import com.akabazan.service.dto.FiatAccountMapper;
import com.akabazan.service.dto.FiatAccountResult;
import com.akabazan.repository.enums.Status;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FiatAccountServiceImpl implements FiatAccountService {

    private final FiatAccountRepository fiatAccountRepository;
    private final CurrentUserService currentUserService;

    public FiatAccountServiceImpl(FiatAccountRepository fiatAccountRepository,
            CurrentUserService currentUserService) {
        this.fiatAccountRepository = fiatAccountRepository;
        this.currentUserService = currentUserService;
    }

    @Override
    public List<FiatAccountResult> getCurrentUserAccounts() {
        UUID userId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        return fiatAccountRepository.findActiveByUserId(userId)
                .stream()
                .map(FiatAccountMapper::toResult)
                .collect(Collectors.toList());
    }

    @Override
    public FiatAccountResult createFiatAccount(FiatAccountResult request) {
        validateInput(request);

        User currentUser = currentUserService.getCurrentUser()
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));

        fiatAccountRepository.findByUserAndBankNameAndAccountNumberAndAccountHolder(
                currentUser,
                request.getBankName(),
                request.getAccountNumber(),
                request.getAccountHolder())
                .ifPresent(acc -> {
                    throw new ApplicationException(ErrorCode.FIAT_ACCOUNT_ALREADY_EXISTS);
                });

        FiatAccount account = new FiatAccount();
        account.setUser(currentUser);
        account.setBankName(request.getBankName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountHolder(request.getAccountHolder());
        account.setBranch(request.getBranch());
        account.setPaymentType(request.getPaymentType());
        account.setStatus(Status.ACTIVE);

        return FiatAccountMapper.toResult(fiatAccountRepository.save(account));
    }

    @Override
    public FiatAccountResult updateFiatAccount(FiatAccountResult request) {
        validateInput(request);

        FiatAccount account = fiatAccountRepository.findActiveById(request.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.FIAT_ACCOUNT_NOT_FOUND));

        // Check ownership
        UUID currentUserId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));
        if (!account.getUser().getId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }

        account.setBankName(request.getBankName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountHolder(request.getAccountHolder());
        account.setBranch(request.getBranch());
        account.setPaymentType(request.getPaymentType());

        return FiatAccountMapper.toResult(fiatAccountRepository.save(account));
    }

    @Override
    public void deleteFiatAccount(UUID id) {
        FiatAccount account = fiatAccountRepository.findActiveById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.FIAT_ACCOUNT_NOT_FOUND));

        // Check ownership
        UUID currentUserId = currentUserService.getCurrentUserId()
                .orElseThrow(() -> new ApplicationException(ErrorCode.UNAUTHORIZED));
        if (!account.getUser().getId().equals(currentUserId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN);
        }
        account.setStatus(Status.INACTIVE);
        account.setDeletedAt(java.time.LocalDateTime.now());
        fiatAccountRepository.save(account);
    }

    private void validateInput(FiatAccountResult request) {
        if (isBlank(request.getBankName())
                || isBlank(request.getAccountNumber())
                || isBlank(request.getAccountHolder())
                || isBlank(request.getPaymentType())) {
            throw new ApplicationException(ErrorCode.INVALID_FIAT_ACCOUNT_INPUT);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
