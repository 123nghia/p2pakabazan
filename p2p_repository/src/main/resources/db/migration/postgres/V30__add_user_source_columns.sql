-- =========================================
-- Migration: Add user source columns
-- - type: identifies user source (e.g. P2P, SAN_A, PARTNER_X)
-- - rel_id: external user id from partner/exchange
-- =========================================

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS type VARCHAR(50) NOT NULL DEFAULT 'P2P',
    ADD COLUMN IF NOT EXISTS rel_id VARCHAR(120);

-- username is now optional in the codebase; keep column but remove legacy uniqueness constraint if it exists
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_username_key;

CREATE INDEX IF NOT EXISTS idx_users_rel_id
    ON users(rel_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_type_rel_id
    ON users(type, rel_id);

