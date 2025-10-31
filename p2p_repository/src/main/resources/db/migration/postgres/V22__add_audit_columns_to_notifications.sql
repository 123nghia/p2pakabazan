-- =========================================
-- Migration: Add audit columns to notifications
-- =========================================

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS created_by BIGINT NULL,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT NULL;
