-- =========================================
-- Migration: Add audit columns to user_admin and trade_chats
-- Adds created_at and updated_at columns to support AbstractEntity pattern
-- =========================================

-- Add audit columns to user_admin
ALTER TABLE user_admin
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Update existing rows to have created_at = now() if null
UPDATE user_admin
SET created_at = now()
WHERE created_at IS NULL;

-- Make created_at NOT NULL after backfill
ALTER TABLE user_admin
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN created_at SET DEFAULT now();

-- Add audit columns to trade_chats
ALTER TABLE trade_chats
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT now(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Update existing rows to have created_at = timestamp if available, otherwise now()
UPDATE trade_chats
SET created_at = COALESCE(timestamp, now())
WHERE created_at IS NULL;

-- If timestamp column exists and is different from created_at, use it
-- Then make created_at NOT NULL after backfill
ALTER TABLE trade_chats
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN created_at SET DEFAULT now();

