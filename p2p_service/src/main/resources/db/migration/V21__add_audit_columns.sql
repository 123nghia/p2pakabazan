-- =========================================
-- Migration: Audit columns on core tables
-- Adds created_by / updated_by (nullable) and
-- removes deprecated deleted_at soft-delete columns.
-- =========================================

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE wallets
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE trades
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE disputes
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE fiat_accounts
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE payment_methods
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE wallet_transactions
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;

ALTER TABLE currencies
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL,
    DROP COLUMN IF EXISTS deleted_at;
