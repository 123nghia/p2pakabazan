DROP TABLE IF EXISTS public.dispute_reasons;

CREATE TABLE public.dispute_reasons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    required_evidence TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    created_by UUID,
    updated_by UUID,
    CONSTRAINT chk_dispute_reasons_role CHECK (role IN ('BUYER', 'SELLER')),
    CONSTRAINT chk_dispute_reasons_priority CHECK (priority IN ('MEDIUM', 'HIGH', 'URGENT'))
);
