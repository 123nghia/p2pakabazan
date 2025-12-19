package com.akabazan.partner.internal.funds;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PartnerFundsService {

    private final BigDecimal initialBalance;

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, LockRecord> locks = new ConcurrentHashMap<>();
    private final Map<String, Object> idempotency = new ConcurrentHashMap<>();

    public PartnerFundsService(@Value("${partner.mock.initial-balance:1000}") BigDecimal initialBalance) {
        this.initialBalance = initialBalance == null ? BigDecimal.valueOf(1000) : initialBalance;
    }

    public BalancesResponse getBalances(String externalUserId, String asset) {
        String userId = requireTrimmed(externalUserId, "externalUserId");
        Account account = accounts.computeIfAbsent(userId, k -> new Account(userId));

        synchronized (account) {
            if (asset != null && !asset.isBlank()) {
                String token = asset.trim().toUpperCase();
                account.ensureAsset(token, initialBalance);
                return BalancesResponse.single(userId, token, account.available.get(token), account.locked.get(token));
            }

            List<BalancesResponse.AssetBalance> balances = new ArrayList<>();
            for (String token : account.available.keySet()) {
                account.ensureAsset(token, initialBalance);
                balances.add(new BalancesResponse.AssetBalance(
                        token,
                        format(account.available.get(token)),
                        format(account.locked.get(token))));
            }
            return new BalancesResponse(userId, balances);
        }
    }

    public LockFundsResponse lock(LockFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (LockFundsResponse) idempotency.computeIfAbsent(requestId, k -> doLock(request));
    }

    public UnlockFundsResponse unlock(UnlockFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (UnlockFundsResponse) idempotency.computeIfAbsent(requestId, k -> doUnlock(request));
    }

    public TransferFundsResponse transfer(TransferFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (TransferFundsResponse) idempotency.computeIfAbsent(requestId, k -> doTransfer(request));
    }

    private LockFundsResponse doLock(LockFundsRequest request) {
        String userId = requireTrimmed(request.externalUserId(), "externalUserId");
        String asset = requireTrimmed(request.asset(), "asset").toUpperCase();
        BigDecimal amount = parsePositiveAmount(request.amount(), "amount");

        Account account = accounts.computeIfAbsent(userId, k -> new Account(userId));
        String lockId = request.requestId(); // deterministic for idempotency in the mock

        synchronized (account) {
            account.ensureAsset(asset, initialBalance);
            BigDecimal available = account.available.get(asset);
            if (available.compareTo(amount) < 0) {
                throw new IllegalStateException("INSUFFICIENT_BALANCE");
            }

            account.available.put(asset, available.subtract(amount));
            account.locked.put(asset, account.locked.get(asset).add(amount));

            LockRecord lock = new LockRecord(lockId, userId, asset, amount);
            locks.put(lockId, lock);

            return new LockFundsResponse(
                    request.requestId(),
                    lockId,
                    "LOCKED",
                    format(amount),
                    format(account.available.get(asset)),
                    format(account.locked.get(asset)));
        }
    }

    private UnlockFundsResponse doUnlock(UnlockFundsRequest request) {
        String lockId = requireTrimmed(request.lockId(), "lockId");
        BigDecimal requestedAmount = request.amount() == null || request.amount().isBlank()
                ? null
                : parsePositiveAmount(request.amount(), "amount");

        LockRecord lock = locks.get(lockId);
        if (lock == null) {
            throw new IllegalArgumentException("LOCK_NOT_FOUND");
        }

        Account account = accounts.computeIfAbsent(lock.externalUserId, k -> new Account(lock.externalUserId));

        synchronized (account) {
            account.ensureAsset(lock.asset, initialBalance);

            BigDecimal remaining = lock.remaining;
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                return new UnlockFundsResponse(
                        request.requestId(),
                        lockId,
                        "UNLOCKED",
                        format(BigDecimal.ZERO),
                        format(BigDecimal.ZERO),
                        format(account.available.get(lock.asset)),
                        format(account.locked.get(lock.asset)));
            }

            BigDecimal unlockAmount = requestedAmount == null ? remaining : requestedAmount;
            if (unlockAmount.compareTo(remaining) > 0) {
                throw new IllegalArgumentException("AMOUNT_EXCEEDS_LOCK");
            }

            lock.remaining = remaining.subtract(unlockAmount);
            if (lock.remaining.compareTo(BigDecimal.ZERO) == 0) {
                lock.status = "UNLOCKED";
            }

            account.locked.put(lock.asset, account.locked.get(lock.asset).subtract(unlockAmount));
            account.available.put(lock.asset, account.available.get(lock.asset).add(unlockAmount));

            return new UnlockFundsResponse(
                    request.requestId(),
                    lockId,
                    lock.status,
                    format(unlockAmount),
                    format(lock.remaining),
                    format(account.available.get(lock.asset)),
                    format(account.locked.get(lock.asset)));
        }
    }

    private TransferFundsResponse doTransfer(TransferFundsRequest request) {
        String lockId = requireTrimmed(request.lockId(), "lockId");
        String fromUser = requireTrimmed(request.fromExternalUserId(), "fromExternalUserId");
        String toUser = requireTrimmed(request.toExternalUserId(), "toExternalUserId");
        String asset = requireTrimmed(request.asset(), "asset").toUpperCase();
        BigDecimal amount = parsePositiveAmount(request.amount(), "amount");

        LockRecord lock = locks.get(lockId);
        if (lock == null) {
            throw new IllegalArgumentException("LOCK_NOT_FOUND");
        }
        if (!lock.externalUserId.equals(fromUser)) {
            throw new IllegalArgumentException("LOCK_OWNER_MISMATCH");
        }
        if (!lock.asset.equals(asset)) {
            throw new IllegalArgumentException("LOCK_ASSET_MISMATCH");
        }
        if (lock.remaining.compareTo(amount) < 0) {
            throw new IllegalArgumentException("AMOUNT_EXCEEDS_LOCK");
        }

        Account fromAccount = accounts.computeIfAbsent(fromUser, k -> new Account(fromUser));
        Account toAccount = accounts.computeIfAbsent(toUser, k -> new Account(toUser));

        Object first;
        Object second;
        if (System.identityHashCode(fromAccount) < System.identityHashCode(toAccount)) {
            first = fromAccount;
            second = toAccount;
        } else {
            first = toAccount;
            second = fromAccount;
        }

        synchronized (first) {
            synchronized (second) {
                fromAccount.ensureAsset(asset, initialBalance);
                toAccount.ensureAsset(asset, initialBalance);

                lock.remaining = lock.remaining.subtract(amount);
                lock.status = lock.remaining.compareTo(BigDecimal.ZERO) == 0 ? "RELEASED" : "PARTIAL_RELEASED";

                fromAccount.locked.put(asset, fromAccount.locked.get(asset).subtract(amount));
                toAccount.available.put(asset, toAccount.available.get(asset).add(amount));

                String transferId = "TRANSFER:" + UUID.randomUUID();
                return new TransferFundsResponse(
                        request.requestId(),
                        transferId,
                        "COMPLETED",
                        format(lock.remaining),
                        format(toAccount.available.get(asset)));
            }
        }
    }

    private static BigDecimal parsePositiveAmount(String raw, String fieldName) {
        String value = requireTrimmed(raw, fieldName);
        try {
            BigDecimal amount = new BigDecimal(value).setScale(8, RoundingMode.DOWN);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(fieldName + " must be > 0");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName);
        }
    }

    private static String requireTrimmed(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private static String format(BigDecimal value) {
        if (value == null) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private static final class Account {
        private final String externalUserId;
        private final Map<String, BigDecimal> available = new ConcurrentHashMap<>();
        private final Map<String, BigDecimal> locked = new ConcurrentHashMap<>();

        private Account(String externalUserId) {
            this.externalUserId = externalUserId;
        }

        private void ensureAsset(String asset, BigDecimal initialBalance) {
            available.putIfAbsent(asset, initialBalance);
            locked.putIfAbsent(asset, BigDecimal.ZERO);
        }
    }

    private static final class LockRecord {
        private final String lockId;
        private final String externalUserId;
        private final String asset;
        private final BigDecimal original;
        private BigDecimal remaining;
        private String status;

        private LockRecord(String lockId, String externalUserId, String asset, BigDecimal original) {
            this.lockId = lockId;
            this.externalUserId = externalUserId;
            this.asset = asset;
            this.original = original;
            this.remaining = original;
            this.status = "LOCKED";
        }
    }
}

