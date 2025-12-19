-- =========================================
-- Migration: Create external_user_mappings table
-- Used to map (partnerId, externalUserId) -> internal users.id
-- =========================================

CREATE TABLE IF NOT EXISTS external_user_mappings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id VARCHAR(50) NOT NULL,
    external_user_id VARCHAR(120) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    external_username VARCHAR(120),
    external_email VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_external_user_mappings_partner_external_user
    ON external_user_mappings(partner_id, external_user_id);

CREATE INDEX IF NOT EXISTS idx_external_user_mappings_user_id
    ON external_user_mappings(user_id);

