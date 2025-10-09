-- ========================================
-- Migration: Create wallet_transactions table
-- ========================================

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(32) NOT NULL,
    amount NUMERIC(30, 8) NOT NULL,
    balance_before NUMERIC(30, 8) NOT NULL,
    balance_after NUMERIC(30, 8) NOT NULL,
    available_before NUMERIC(30, 8) NOT NULL,
    available_after NUMERIC(30, 8) NOT NULL,
    performed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reference_type VARCHAR(64),
    reference_id BIGINT,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_wallet_transactions_wallet_id ON wallet_transactions(wallet_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_user_id ON wallet_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transactions_reference ON wallet_transactions(reference_type, reference_id);
