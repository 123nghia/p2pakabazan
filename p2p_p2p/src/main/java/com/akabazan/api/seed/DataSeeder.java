package com.akabazan.api.seed;

import com.akabazan.repository.CurrencyRepository;
import com.akabazan.repository.FiatAccountRepository;
import com.akabazan.repository.OrderRepository;
import com.akabazan.repository.UserRepository;
import com.akabazan.repository.WalletRepository;
import com.akabazan.repository.constant.CurrencyType;
import com.akabazan.repository.constant.OrderStatus;
import com.akabazan.repository.entity.Currency;
import com.akabazan.repository.entity.FiatAccount;
import com.akabazan.repository.entity.Order;
import com.akabazan.repository.entity.User;
import com.akabazan.repository.entity.Wallet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final int TARGET_USER_COUNT = 24;
    private static final int ORDERS_PER_USER = 20;
    private static final String DEMO_PAYMENT_METHOD = "BANK_TRANSFER";
    private static final String EMAIL_DOMAIN = "akabazan.com";
    private static final List<String> FIAT_CODES = List.of("VND", "USD", "EUR", "SGD", "JPY");
    private static final List<String> FIRST_NAMES = List.of(
            "anh", "bao", "chi", "duy", "ha", "khanh", "lam", "minh",
            "ngan", "quyen", "son", "vy");
    private static final List<String> LAST_NAMES = List.of(
            "nguyen", "tran", "le", "pham", "hoang", "vo", "dang", "bui");
    private static final List<String> BANK_NAMES = List.of(
            "Aurora Bank", "Evergreen Bank", "Harmony Bank", "Lighthouse Bank",
            "Mariner Bank", "Summit Bank", "Orchid Bank", "Sunrise Bank",
            "Riverstone Bank", "Bluewave Bank");
    private static final List<String> BRANCH_POOL = List.of(
            "Hanoi Central", "Saigon Plaza", "Da Nang Riverside", "Can Tho Harbour",
            "Hue Citadel", "Hai Phong Dock", "Nha Trang Bay", "Quy Nhon Port",
            "Vung Tau Marina", "Phu Quoc Pearl");

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CurrencyRepository currencyRepository;
    private final FiatAccountRepository fiatAccountRepository;
    private final OrderRepository orderRepository;

    public DataSeeder(
            UserRepository userRepository,
            WalletRepository walletRepository,
            CurrencyRepository currencyRepository,
            FiatAccountRepository fiatAccountRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.currencyRepository = currencyRepository;
        this.fiatAccountRepository = fiatAccountRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Currency> tokens =
                currencyRepository.findAllByTypeAndActiveTrueOrderByDisplayOrderAscCodeAsc(CurrencyType.TOKEN);
        if (tokens.isEmpty()) {
            System.out.println("[DataSeeder] No token master data found. Skipping demo seed.");
            return;
        }

        List<SeedUser> seedUsers = generateSeedUsers();
        int totalOrdersCreated = 0;

        for (int index = 0; index < seedUsers.size(); index++) {
            SeedUser seedUser = seedUsers.get(index);
            User user = upsertUser(seedUser);
            FiatAccount fiatAccount = ensureFiatAccount(user, seedUser);
            initializeWallets(user, tokens, index);
            totalOrdersCreated += seedOrdersForUser(user, fiatAccount, tokens, index);
        }

        System.out.printf(
                Locale.US,
                "[DataSeeder] Prepared %d users with wallets for %d active tokens. Orders created this run: %d (target total %d).%n",
                seedUsers.size(),
                tokens.size(),
                totalOrdersCreated,
                seedUsers.size() * ORDERS_PER_USER);
    }

    private List<SeedUser> generateSeedUsers() {
        List<SeedUser> seedUsers = new ArrayList<>(TARGET_USER_COUNT);
        int branchSize = BRANCH_POOL.size();
        int bankSize = BANK_NAMES.size();

        for (int index = 0; index < TARGET_USER_COUNT; index++) {
            String first = FIRST_NAMES.get(index % FIRST_NAMES.size());
            String last = LAST_NAMES.get((index / FIRST_NAMES.size()) % LAST_NAMES.size());
            String displayFirst = capitalize(first);
            String displayLast = capitalize(last);
            String displayName = displayFirst + " " + displayLast;

            String localPart = String.format(Locale.ROOT, "%s.%s%02d", first, last, index + 1);
            String email = localPart + "@" + EMAIL_DOMAIN;
            String phone = String.format(Locale.ROOT, "0938%06d", index + 1);
            String password = String.format(Locale.ROOT, "{noop}%s%s#%02d", displayFirst, displayLast, index + 1);
            String bankName = BANK_NAMES.get(index % bankSize);
            String accountNumber = String.format(Locale.ROOT, "52%08d", index + 1);
            String branch = BRANCH_POOL.get(index % branchSize);

            seedUsers.add(new SeedUser(email, phone, password, bankName, accountNumber, displayName, branch));
        }
        return seedUsers;
    }

    private User upsertUser(SeedUser seedUser) {
        Optional<User> existing = userRepository.findByEmail(seedUser.email());
        User user = existing.orElseGet(User::new);
        user.setEmail(seedUser.email());
        user.setPhone(seedUser.phone());
        user.setPassword(seedUser.password());
        user.setKycStatus(User.KycStatus.VERIFIED);
        user.setRole(User.Role.TRADER);
        return userRepository.save(user);
    }

    private FiatAccount ensureFiatAccount(User user, SeedUser seedUser) {
        return fiatAccountRepository
                .findByUserAndBankNameAndAccountNumberAndAccountHolder(
                        user, seedUser.bankName(), seedUser.accountNumber(), seedUser.accountHolder())
                .orElseGet(() -> {
                    FiatAccount account = new FiatAccount();
                    account.setUser(user);
                    account.setBankName(seedUser.bankName());
                    account.setAccountNumber(seedUser.accountNumber());
                    account.setAccountHolder(seedUser.accountHolder());
                    account.setBranch(seedUser.branch());
                    account.setPaymentType(DEMO_PAYMENT_METHOD);
                    return fiatAccountRepository.save(account);
                });
    }

    private void initializeWallets(User user, List<Currency> tokens, int userIndex) {
        double baseBalance = 2000 + (userIndex * 200);
        List<Wallet> newWallets = new ArrayList<>();

        for (Currency token : tokens) {
            double tokenOffset = token.getDisplayOrder() != null
                    ? token.getDisplayOrder()
                    : Math.abs(token.getCode().hashCode() % 50);
            double desiredBalance = baseBalance + tokenOffset;

            walletRepository.findByUserIdAndToken(user.getId(), token.getCode())
                    .ifPresentOrElse(existing -> {
                        if (existing.getAvailableBalance() < desiredBalance) {
                            existing.setBalance(desiredBalance);
                            existing.setAvailableBalance(desiredBalance);
                        }
                    }, () -> {
                        Wallet wallet = new Wallet();
                        wallet.setUser(user);
                        wallet.setToken(token.getCode());
                        wallet.setAddress(buildWalletAddress(user, token));
                        wallet.setBalance(desiredBalance);
                        wallet.setAvailableBalance(desiredBalance);
                        newWallets.add(wallet);
                    });
        }

        if (!newWallets.isEmpty()) {
            walletRepository.saveAll(newWallets);
        }
    }

    private int seedOrdersForUser(User user, FiatAccount fiatAccount, List<Currency> tokens, int userIndex) {
        List<Order> existingOrders = orderRepository.findByUserId(user.getId());
        long existingDemoOrders = existingOrders.stream()
                .filter(order -> DEMO_PAYMENT_METHOD.equals(order.getPaymentMethod()))
                .count();
        int remaining = ORDERS_PER_USER - (int) existingDemoOrders;
        if (remaining <= 0) {
            return 0;
        }

        Random random = new Random(Objects.hash(user.getEmail(), userIndex, remaining));
        List<Order> newOrders = new ArrayList<>(remaining);
        int offset = (int) existingDemoOrders;

        for (int i = 0; i < remaining; i++) {
            int orderIndex = offset + i;
            Currency token = tokens.get((userIndex + orderIndex) % tokens.size());

            double amount = round(25 + random.nextDouble() * 475, 4);
            double price = round(15 + random.nextDouble() * 285, 2);
            double totalFiat = round(amount * price, 2);
            double minLimit = Math.min(round(totalFiat * 0.25, 2), totalFiat);
            double maxLimit = Math.max(round(totalFiat * 0.65, 2), minLimit);

            Order order = new Order();
            order.setUser(user);
            order.setFiatAccount(fiatAccount);
            order.setPaymentMethod(DEMO_PAYMENT_METHOD);
            order.setType((orderIndex + userIndex) % 2 == 0 ? "SELL" : "BUY");
            order.setToken(token.getCode());
            order.setAmount(amount);
            order.setAvailableAmount(amount);
            order.setPrice(price);
            order.setMinLimit(minLimit);
            order.setMaxLimit(maxLimit);
            order.setFiat(FIAT_CODES.get((userIndex + orderIndex) % FIAT_CODES.size()));
            order.setStatus(OrderStatus.OPEN.name());
            order.setPriceMode("CUSTOM");
            order.setExpireAt(LocalDateTime.now().plusHours(24 + ((userIndex + orderIndex) % 120)));

            newOrders.add(order);
        }

        if (!newOrders.isEmpty()) {
            orderRepository.saveAll(newOrders);
        }
        return newOrders.size();
    }

    private String buildWalletAddress(User user, Currency token) {
        String emailPrefix = user.getEmail().split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        return (emailPrefix + "-" + token.getCode() + "-wallet").toLowerCase(Locale.ROOT);
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase(Locale.ROOT);
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }

    private record SeedUser(
            String email,
            String phone,
            String password,
            String bankName,
            String accountNumber,
            String accountHolder,
            String branch) {}
}
