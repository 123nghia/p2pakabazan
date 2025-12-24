-- Fix incorrect foreign key for disputes.assigned_admin_id

-- 1. Drop the incorrect foreign key referencing users
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS fk_dispute_admin;

-- 2. Reset assigned_admin_id to NULL
-- The existing UUIDs were generated using the USER namespace but user_admin uses the ADMIN namespace.
-- Therefore, the current IDs in this column do not match any record in the user_admin table.
UPDATE disputes SET assigned_admin_id = NULL;

-- 3. Add the correct foreign key referencing user_admin
ALTER TABLE disputes
    ADD CONSTRAINT fk_dispute_admin FOREIGN KEY (assigned_admin_id) REFERENCES user_admin(id) ON DELETE SET NULL;

-- 4. Fix dispute_evidence audit columns (missed in V27)
-- Convert created_by and updated_by from VARCHAR to UUID using the USER namespace (same as V27)
ALTER TABLE dispute_evidence
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;

-- 5. Fix dispute status check constraint mismatch (IN_PROGRESS vs IN_REVIEW)
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS chk_disputes_status_allowed;
ALTER TABLE disputes ADD CONSTRAINT chk_disputes_status_allowed CHECK (status::text = ANY (ARRAY['OPEN'::character varying, 'IN_REVIEW'::character varying, 'RESOLVED'::character varying, 'REJECTED'::character varying]::text[]));

-- 6. Fix dispute outcome check constraint mismatch (APPROVED/DENIED vs BUYER_FAVORED/SELLER_FAVORED)
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS chk_disputes_outcome_allowed;
ALTER TABLE disputes ADD CONSTRAINT chk_disputes_outcome_allowed CHECK ((resolution_outcome IS NULL) OR ((resolution_outcome)::text = ANY ((ARRAY['BUYER_FAVORED'::character varying, 'SELLER_FAVORED'::character varying, 'CANCELLED'::character varying])::text[])));
