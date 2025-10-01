package com.akabazan.service.impl;

import com.akabazan.repository.UserRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.service.AuthService;
import com.akabazan.service.ExternalUserIntegrationService;
import com.akabazan.service.dto.IntegrationSyncCommand;
import com.akabazan.service.dto.IntegrationSyncResult;
import com.akabazan.service.dto.IntegrationWalletData;
import jakarta.transaction.Transactional;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExternalUserIntegrationServiceImpl implements ExternalUserIntegrationService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AuthService authService;

    public ExternalUserIntegrationServiceImpl(UserRepository userRepository,
                                              WalletRepository walletRepository,
                                              AuthService authService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public IntegrationSyncResult syncUserAndWallet(IntegrationSyncCommand command) {
        String email = command.getEmail();
        IntegrationWalletData walletData = command.getWalletData();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        Objects.requireNonNull(walletData, "Wallet data must not be null");
        walletData.requireValid();

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> createNewUser(normalizedEmail, command.getKycStatus()));

        if (command.getKycStatus() != null && user.getKycStatus() != command.getKycStatus()) {
            user.setKycStatus(command.getKycStatus());
            userRepository.save(user);
        }

        Wallet wallet = synchronizeWallet(user, walletData);

        String token = authService.issueToken(user.getId());

        User hydratedUser = userRepository.findByIdWithWallets(user.getId()).orElse(user);
        Wallet hydratedWallet = walletRepository.findByUserIdAndToken(user.getId(), wallet.getToken())
                .orElse(wallet);

        return new IntegrationSyncResult(
                hydratedUser,
                hydratedWallet.getId(),
                hydratedWallet.getToken(),
                hydratedWallet.getAddress(),
                hydratedWallet.getBalance(),
                hydratedWallet.getAvailableBalance(),
                token);
    }

    private User createNewUser(String email, User.KycStatus kycStatus) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(UUID.randomUUID().toString());
        if (kycStatus != null) {
            newUser.setKycStatus(kycStatus);
        }
        return userRepository.save(newUser);
    }

    private Wallet synchronizeWallet(User user, IntegrationWalletData walletData) {
        Optional<Wallet> walletOpt = walletRepository.findByUserIdAndToken(user.getId(), walletData.getToken());
        Wallet wallet = walletOpt.orElseGet(() -> {
            Wallet created = new Wallet();
            created.setUser(user);
            created.setToken(walletData.getToken());
            user.getWallets().add(created);
            return created;
        });

        wallet.setAddress(walletData.getAddress());
        double balance = walletData.getBalance() == null ? 0.0 : walletData.getBalance();
        wallet.setBalance(balance);
        if (walletData.getAvailableBalance() != null) {
            wallet.setAvailableBalance(walletData.getAvailableBalance());
        } else if (walletOpt.isEmpty()) {
            wallet.setAvailableBalance(balance);
        }

        return walletRepository.save(wallet);
    }
}
