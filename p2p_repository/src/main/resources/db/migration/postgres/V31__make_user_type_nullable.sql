-- =========================================
-- Migration: Allow NULL for users.type (P2P native users have type=NULL, rel_id=NULL)
-- =========================================

ALTER TABLE users
    ALTER COLUMN type DROP NOT NULL,
    ALTER COLUMN type DROP DEFAULT;

UPDATE users
SET type = NULL
WHERE type = 'P2P'
  AND (rel_id IS NULL OR btrim(rel_id) = '');

