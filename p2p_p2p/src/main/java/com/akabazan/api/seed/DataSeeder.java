package com.akabazan.api.seed;

import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public DataSeeder(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Seller
            User seller = new User();
            seller.setEmail("seller@test.p2p");
            seller.setPassword("password123");
            seller.setKycStatus(User.KycStatus.VERIFIED);
            userRepository.save(seller);

            Wallet sellerWallet = new Wallet();
            sellerWallet.setUser(seller);
            sellerWallet.setToken("USDT");
            sellerWallet.setAvailableBalance(1000);
            walletRepository.save(sellerWallet);

            // Buyer
            User buyer = new User();
            buyer.setEmail("buyer@test.p2p");
            buyer.setPassword("{noop}123456");
            buyer.setKycStatus(User.KycStatus.VERIFIED);
            userRepository.save(buyer);

            Wallet buyerWallet = new Wallet();
            buyerWallet.setUser(buyer);
            buyerWallet.setToken("USDT");
            buyerWallet.setAvailableBalance(500);
            walletRepository.save(buyerWallet);

            System.out.println("Seeded users and wallets successfully.");
        }
    }
}
