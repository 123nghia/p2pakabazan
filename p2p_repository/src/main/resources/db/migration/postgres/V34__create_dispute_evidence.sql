-- ============================
-- Migration: Create Dispute Evidence Table
-- ============================
CREATE TABLE IF NOT EXISTS dispute_evidence (
    id UUID PRIMARY KEY,
    dispute_id UUID NOT NULL REFERENCES disputes(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_dispute_evidence_dispute_id ON dispute_evidence(dispute_id);
