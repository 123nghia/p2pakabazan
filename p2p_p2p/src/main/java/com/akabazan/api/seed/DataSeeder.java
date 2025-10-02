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
     
            // Seller
            User seller1 = new User();
            seller1.setEmail("dungne@gmail.com");
            seller1.setPassword("123");
            seller1.setKycStatus(User.KycStatus.VERIFIED);
            userRepository.save(seller1);

            Wallet sellerWallet1 = new Wallet();
            sellerWallet1.setUser(seller1);
            sellerWallet1.setToken("USDT");
            sellerWallet1.setAvailableBalance(500);
            walletRepository.save(sellerWallet1);

            // Buyer
            User buyer1 = new User();
            buyer1.setEmail("dungne1@gmail.com");
            buyer1.setPassword("123");
            buyer1.setKycStatus(User.KycStatus.VERIFIED);
            userRepository.save(buyer1);

            Wallet buyerWallet1 = new Wallet();
            buyerWallet1.setUser(buyer1);
            buyerWallet1.setToken("USDT");
            buyerWallet1.setAvailableBalance(500);
            walletRepository.save(buyerWallet1);

            System.out.println("Seeded users and wallets successfully.");
        }
    }
}
