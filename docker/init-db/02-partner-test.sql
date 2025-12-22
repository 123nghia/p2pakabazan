-- Create partner_test database + schema for partner_mock demo
-- This script is executed by the Postgres Docker image on first initialization only.

-- Create database if it doesn't exist (psql meta-command)
SELECT 'CREATE DATABASE partner_test'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'partner_test')\gexec

\connect partner_test

SET timezone = 'UTC';
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Partner wallet table: available + locked balances per user & asset
CREATE TABLE IF NOT EXISTS partner_wallet_balances (
    external_user_id   varchar(128)  NOT NULL,
    asset              varchar(32)   NOT NULL,
    available_balance  numeric(38,8) NOT NULL DEFAULT 0,
    locked_balance     numeric(38,8) NOT NULL DEFAULT 0,
    updated_at         timestamptz   NOT NULL DEFAULT now(),
    CONSTRAINT pk_partner_wallet_balances PRIMARY KEY (external_user_id, asset),
    CONSTRAINT ck_partner_wallet_available_nonneg CHECK (available_balance >= 0),
    CONSTRAINT ck_partner_wallet_locked_nonneg CHECK (locked_balance >= 0)
);

-- Lock records: track per-lock remaining amount for unlock/transfer flows
CREATE TABLE IF NOT EXISTS partner_wallet_locks (
    lock_id           varchar(128)  NOT NULL,
    external_user_id  varchar(128)  NOT NULL,
    asset             varchar(32)   NOT NULL,
    original_amount   numeric(38,8) NOT NULL,
    remaining_amount  numeric(38,8) NOT NULL,
    status            varchar(32)   NOT NULL,
    created_at        timestamptz   NOT NULL DEFAULT now(),
    updated_at        timestamptz   NOT NULL DEFAULT now(),
    CONSTRAINT pk_partner_wallet_locks PRIMARY KEY (lock_id),
    CONSTRAINT ck_partner_lock_original_pos CHECK (original_amount > 0),
    CONSTRAINT ck_partner_lock_remaining_nonneg CHECK (remaining_amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_partner_wallet_locks_user_asset
    ON partner_wallet_locks (external_user_id, asset);

-- Seed demo data
INSERT INTO partner_wallet_balances (external_user_id, asset, available_balance, locked_balance)
VALUES
    ('user_1001', 'USDT', 100, 0),
    ('user_1001', 'BNB', 100, 0)
ON CONFLICT (external_user_id, asset) DO NOTHING;

