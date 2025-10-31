-- =========================================
-- Migration: Create users_wallets table
-- =========================================

CREATE TABLE IF NOT EXISTS users_wallets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    balance NUMERIC(30, 8) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by BIGINT NULL,
    updated_by BIGINT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_wallets_user_token
    ON users_wallets(user_id, token);

CREATE INDEX IF NOT EXISTS idx_users_wallets_address
    ON users_wallets(address);

CREATE OR REPLACE FUNCTION update_users_wallets_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'trg_users_wallets_updated_at'
    ) THEN
        CREATE TRIGGER trg_users_wallets_updated_at
        BEFORE UPDATE ON users_wallets
        FOR EACH ROW
        EXECUTE FUNCTION update_users_wallets_updated_at();
    END IF;
END $$;
