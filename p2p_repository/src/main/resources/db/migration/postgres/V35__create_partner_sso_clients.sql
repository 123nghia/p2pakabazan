-- =========================================
-- Migration: Create partner_sso_clients table
-- Used to store partner credentials for SSO HMAC authentication
-- =========================================

CREATE TABLE IF NOT EXISTS partner_sso_clients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partner_id VARCHAR(50) NOT NULL,
    shared_secret VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_partner_sso_clients_partner_id
    ON partner_sso_clients(partner_id);

