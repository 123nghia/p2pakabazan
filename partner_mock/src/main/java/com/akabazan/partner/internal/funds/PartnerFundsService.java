package com.akabazan.partner.internal.funds;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerFundsService {

    private static final List<String> DEFAULT_ASSETS = List.of("USDT", "BNB");

    private final JdbcTemplate jdbcTemplate;
    private final BigDecimal initialBalance;

    private final java.util.Map<String, Object> idempotency = new ConcurrentHashMap<>();

    public PartnerFundsService(JdbcTemplate jdbcTemplate,
                               @Value("${partner.mock.initial-balance:100}") BigDecimal initialBalance) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
        this.initialBalance = initialBalance == null ? BigDecimal.valueOf(100) : initialBalance;
    }

    @Transactional
    public BalancesResponse getBalances(String externalUserId, String asset) {
        String userId = requireTrimmed(externalUserId, "externalUserId");

        if (asset != null && !asset.isBlank()) {
            String token = asset.trim().toUpperCase();
            ensureWalletRow(userId, token);
            WalletBalance wallet = getWallet(userId, token);
            return BalancesResponse.single(userId, token, wallet.availableBalance(), wallet.lockedBalance());
        }

        ensureDefaultWalletRows(userId);

        List<BalancesResponse.AssetBalance> balances = jdbcTemplate.query(
                "SELECT asset, available_balance, locked_balance FROM partner_wallet_balances WHERE external_user_id = ? ORDER BY asset",
                (rs, rowNum) -> new BalancesResponse.AssetBalance(
                        rs.getString("asset"),
                        format(rs.getBigDecimal("available_balance")),
                        format(rs.getBigDecimal("locked_balance"))),
                userId);

        return new BalancesResponse(userId, balances);
    }

    @Transactional
    public LockFundsResponse lock(LockFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (LockFundsResponse) idempotency.computeIfAbsent(requestId, k -> doLock(request));
    }

    @Transactional
    public UnlockFundsResponse unlock(UnlockFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (UnlockFundsResponse) idempotency.computeIfAbsent(requestId, k -> doUnlock(request));
    }

    @Transactional
    public TransferFundsResponse transfer(TransferFundsRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        String requestId = requireTrimmed(request.requestId(), "requestId");
        return (TransferFundsResponse) idempotency.computeIfAbsent(requestId, k -> doTransfer(request));
    }

    private LockFundsResponse doLock(LockFundsRequest request) {
        String userId = requireTrimmed(request.externalUserId(), "externalUserId");
        String asset = requireTrimmed(request.asset(), "asset").toUpperCase();
        BigDecimal amount = parsePositiveAmount(request.amount(), "amount");

        String lockId = request.requestId(); // deterministic for idempotency in the mock

        ensureWalletRow(userId, asset);

        WalletBalance walletBefore = getWalletForUpdate(userId, asset);

        int inserted = jdbcTemplate.update(
                """
                INSERT INTO partner_wallet_locks (lock_id, external_user_id, asset, original_amount, remaining_amount, status)
                VALUES (?, ?, ?, ?, ?, 'LOCKED')
                ON CONFLICT (lock_id) DO NOTHING
                """,
                lockId, userId, asset, amount, amount);

        if (inserted == 0) {
            LockRecord existing = getLock(lockId);
            if (!existing.externalUserId().equals(userId)) {
                throw new IllegalArgumentException("LOCK_OWNER_MISMATCH");
            }
            if (!existing.asset().equals(asset)) {
                throw new IllegalArgumentException("LOCK_ASSET_MISMATCH");
            }
            return new LockFundsResponse(
                    request.requestId(),
                    lockId,
                    existing.status(),
                    format(existing.originalAmount()),
                    format(walletBefore.availableBalance()),
                    format(walletBefore.lockedBalance()));
        }

        if (walletBefore.availableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("INSUFFICIENT_BALANCE");
        }

        jdbcTemplate.update(
                "UPDATE partner_wallet_balances SET available_balance = available_balance - ?, locked_balance = locked_balance + ?, updated_at = now() WHERE external_user_id = ? AND asset = ?",
                amount, amount, userId, asset);

        WalletBalance walletAfter = new WalletBalance(
                walletBefore.availableBalance().subtract(amount),
                walletBefore.lockedBalance().add(amount));

        return new LockFundsResponse(
                request.requestId(),
                lockId,
                "LOCKED",
                format(amount),
                format(walletAfter.availableBalance()),
                format(walletAfter.lockedBalance()));
    }

    private UnlockFundsResponse doUnlock(UnlockFundsRequest request) {
        String lockId = requireTrimmed(request.lockId(), "lockId");
        BigDecimal requestedAmount = request.amount() == null || request.amount().isBlank()
                ? null
                : parsePositiveAmount(request.amount(), "amount");

        LockRecord lock = getLockForUpdate(lockId);

        BigDecimal unlockAmount = requestedAmount == null ? lock.remainingAmount() : requestedAmount;
        if (unlockAmount.compareTo(lock.remainingAmount()) > 0) {
            throw new IllegalArgumentException("AMOUNT_EXCEEDS_LOCK");
        }

        ensureWalletRow(lock.externalUserId(), lock.asset());
        WalletBalance walletBefore = getWalletForUpdate(lock.externalUserId(), lock.asset());

        if (walletBefore.lockedBalance().compareTo(unlockAmount) < 0) {
            throw new IllegalStateException("INSUFFICIENT_LOCKED_BALANCE");
        }

        BigDecimal newRemaining = lock.remainingAmount().subtract(unlockAmount);
        String newStatus = newRemaining.compareTo(BigDecimal.ZERO) == 0 ? "RELEASED" : "PARTIAL_RELEASED";

        jdbcTemplate.update(
                "UPDATE partner_wallet_balances SET available_balance = available_balance + ?, locked_balance = locked_balance - ?, updated_at = now() WHERE external_user_id = ? AND asset = ?",
                unlockAmount, unlockAmount, lock.externalUserId(), lock.asset());

        jdbcTemplate.update(
                "UPDATE partner_wallet_locks SET remaining_amount = ?, status = ?, updated_at = now() WHERE lock_id = ?",
                newRemaining, newStatus, lockId);

        WalletBalance walletAfter = new WalletBalance(
                walletBefore.availableBalance().add(unlockAmount),
                walletBefore.lockedBalance().subtract(unlockAmount));

        return new UnlockFundsResponse(
                request.requestId(),
                lockId,
                newStatus,
                format(unlockAmount),
                format(newRemaining),
                format(walletAfter.availableBalance()),
                format(walletAfter.lockedBalance()));
    }

    private TransferFundsResponse doTransfer(TransferFundsRequest request) {
        String lockId = requireTrimmed(request.lockId(), "lockId");
        String fromUser = requireTrimmed(request.fromExternalUserId(), "fromExternalUserId");
        String toUser = requireTrimmed(request.toExternalUserId(), "toExternalUserId");
        String asset = requireTrimmed(request.asset(), "asset").toUpperCase();
        BigDecimal amount = parsePositiveAmount(request.amount(), "amount");

        LockRecord lock = getLockForUpdate(lockId);
        if (!lock.externalUserId().equals(fromUser)) {
            throw new IllegalArgumentException("LOCK_OWNER_MISMATCH");
        }
        if (!lock.asset().equals(asset)) {
            throw new IllegalArgumentException("LOCK_ASSET_MISMATCH");
        }
        if (lock.remainingAmount().compareTo(amount) < 0) {
            throw new IllegalArgumentException("AMOUNT_EXCEEDS_LOCK");
        }

        ensureWalletRow(fromUser, asset);
        ensureWalletRow(toUser, asset);

        String firstUser = fromUser.compareTo(toUser) <= 0 ? fromUser : toUser;
        String secondUser = firstUser.equals(fromUser) ? toUser : fromUser;

        WalletBalance firstWallet = getWalletForUpdate(firstUser, asset);
        WalletBalance secondWallet = secondUser.equals(firstUser) ? firstWallet : getWalletForUpdate(secondUser, asset);

        WalletBalance fromWalletBefore = fromUser.equals(firstUser) ? firstWallet : secondWallet;
        WalletBalance toWalletBefore = fromUser.equals(firstUser) ? secondWallet : firstWallet;

        if (fromWalletBefore.lockedBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("INSUFFICIENT_LOCKED_BALANCE");
        }

        BigDecimal newRemaining = lock.remainingAmount().subtract(amount);
        String newStatus = newRemaining.compareTo(BigDecimal.ZERO) == 0 ? "RELEASED" : "PARTIAL_RELEASED";

        jdbcTemplate.update(
                "UPDATE partner_wallet_locks SET remaining_amount = ?, status = ?, updated_at = now() WHERE lock_id = ?",
                newRemaining, newStatus, lockId);

        jdbcTemplate.update(
                "UPDATE partner_wallet_balances SET locked_balance = locked_balance - ?, updated_at = now() WHERE external_user_id = ? AND asset = ?",
                amount, fromUser, asset);

        jdbcTemplate.update(
                "UPDATE partner_wallet_balances SET available_balance = available_balance + ?, updated_at = now() WHERE external_user_id = ? AND asset = ?",
                amount, toUser, asset);

        WalletBalance toWalletAfter = new WalletBalance(
                toWalletBefore.availableBalance().add(amount),
                toWalletBefore.lockedBalance());

        String transferId = "TRANSFER:" + UUID.randomUUID();
        return new TransferFundsResponse(
                request.requestId(),
                transferId,
                "COMPLETED",
                format(newRemaining),
                format(toWalletAfter.availableBalance()));
    }

    private void ensureDefaultWalletRows(String externalUserId) {
        for (String asset : DEFAULT_ASSETS) {
            ensureWalletRow(externalUserId, asset);
        }
    }

    private void ensureWalletRow(String externalUserId, String asset) {
        jdbcTemplate.update(
                """
                INSERT INTO partner_wallet_balances (external_user_id, asset, available_balance, locked_balance)
                VALUES (?, ?, ?, 0)
                ON CONFLICT (external_user_id, asset) DO NOTHING
                """,
                externalUserId, asset, initialBalance);
    }

    private WalletBalance getWallet(String externalUserId, String asset) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT available_balance, locked_balance FROM partner_wallet_balances WHERE external_user_id = ? AND asset = ?",
                    (rs, rowNum) -> new WalletBalance(
                            rs.getBigDecimal("available_balance"),
                            rs.getBigDecimal("locked_balance")),
                    externalUserId, asset);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("WALLET_NOT_FOUND");
        }
    }

    private WalletBalance getWalletForUpdate(String externalUserId, String asset) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT available_balance, locked_balance FROM partner_wallet_balances WHERE external_user_id = ? AND asset = ? FOR UPDATE",
                    (rs, rowNum) -> new WalletBalance(
                            rs.getBigDecimal("available_balance"),
                            rs.getBigDecimal("locked_balance")),
                    externalUserId, asset);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("WALLET_NOT_FOUND");
        }
    }

    private LockRecord getLock(String lockId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT lock_id, external_user_id, asset, original_amount, remaining_amount, status FROM partner_wallet_locks WHERE lock_id = ?",
                    (rs, rowNum) -> new LockRecord(
                            rs.getString("lock_id"),
                            rs.getString("external_user_id"),
                            rs.getString("asset"),
                            rs.getBigDecimal("original_amount"),
                            rs.getBigDecimal("remaining_amount"),
                            rs.getString("status")),
                    lockId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("LOCK_NOT_FOUND");
        }
    }

    private LockRecord getLockForUpdate(String lockId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT lock_id, external_user_id, asset, original_amount, remaining_amount, status FROM partner_wallet_locks WHERE lock_id = ? FOR UPDATE",
                    (rs, rowNum) -> new LockRecord(
                            rs.getString("lock_id"),
                            rs.getString("external_user_id"),
                            rs.getString("asset"),
                            rs.getBigDecimal("original_amount"),
                            rs.getBigDecimal("remaining_amount"),
                            rs.getString("status")),
                    lockId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("LOCK_NOT_FOUND");
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

    private record WalletBalance(BigDecimal availableBalance, BigDecimal lockedBalance) {}

    private record LockRecord(String lockId,
                              String externalUserId,
                              String asset,
                              BigDecimal originalAmount,
                              BigDecimal remainingAmount,
                              String status) {}
}

