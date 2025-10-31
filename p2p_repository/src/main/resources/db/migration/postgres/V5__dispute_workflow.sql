-- 1) Users.role hardening
BEGIN;

ALTER TABLE public.users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'TRADER';

-- If the column existed before and had NULLs, backfill them.
UPDATE public.users SET role = 'TRADER' WHERE role IS NULL;

-- Optional: constrain valid roles (adjust list to your app)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_users_role_allowed'
          AND conrelid = 'public.users'::regclass
    ) THEN
        ALTER TABLE public.users
            ADD CONSTRAINT chk_users_role_allowed
            CHECK (role IN ('TRADER','ADMIN','SUPPORT'));
    END IF;
END $$;

-- Force explicit role on future inserts.
ALTER TABLE public.users
    ALTER COLUMN role DROP DEFAULT;

COMMIT;


-- 2) Disputes additions + FK + index + timestamps
BEGIN;

ALTER TABLE public.disputes
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    ADD COLUMN IF NOT EXISTS assigned_admin_id BIGINT,
    ADD COLUMN IF NOT EXISTS resolution_note TEXT,
    ADD COLUMN IF NOT EXISTS resolution_outcome VARCHAR(20),
    ADD COLUMN IF NOT EXISTS resolved_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- Optional: constrain allowed values
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_disputes_status_allowed'
          AND conrelid = 'public.disputes'::regclass
    ) THEN
        ALTER TABLE public.disputes
            ADD CONSTRAINT chk_disputes_status_allowed
            CHECK (status IN ('OPEN','IN_PROGRESS','RESOLVED','REJECTED'));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_disputes_outcome_allowed'
          AND conrelid = 'public.disputes'::regclass
    ) THEN
        ALTER TABLE public.disputes
            ADD CONSTRAINT chk_disputes_outcome_allowed
            CHECK (resolution_outcome IS NULL OR resolution_outcome IN ('APPROVED','DENIED','PARTIAL'));
    END IF;
END $$;

-- FK (choose delete behavior to taste: SET NULL keeps disputes if a user is removed)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_dispute_admin'
          AND conrelid = 'public.disputes'::regclass
    ) THEN
        ALTER TABLE public.disputes
            ADD CONSTRAINT fk_dispute_admin
            FOREIGN KEY (assigned_admin_id)
            REFERENCES public.users(id)
            ON DELETE SET NULL;
    END IF;
END $$;

COMMIT;

-- Recommended in production (outside transaction) for large tables:
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_disputes_assigned_admin ON public.disputes(assigned_admin_id);
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_disputes_status ON public.disputes(status);


-- 3) Notifications table + indexes
BEGIN;

CREATE TABLE IF NOT EXISTS public.notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES public.users(id),
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ
);

-- Base index
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON public.notifications(user_id);

COMMIT;

-- Useful indexes for common queries (do these CONCURRENTLY in prod):
-- Unread & not deleted per user
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_notifications_unread_active
--   ON public.notifications(user_id)
--   WHERE is_read = FALSE AND deleted_at IS NULL;

-- Recent items per user (if you sort/paginate by time)
-- CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_notifications_user_created_at
--   ON public.notifications(user_id, created_at DESC);


-- 4) Updated-at triggers (optional but handy)

-- Creates a generic function once per DB
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_proc WHERE proname = 'set_updated_at_tz'
    ) THEN
        CREATE OR REPLACE FUNCTION public.set_updated_at_tz()
        RETURNS TRIGGER AS $f$
        BEGIN
            NEW.updated_at := NOW();
            RETURN NEW;
        END;
        $f$ LANGUAGE plpgsql;
    END IF;
END $$;

-- Attach trigger to disputes.updated_at
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'trg_disputes_set_updated_at'
    ) THEN
        CREATE TRIGGER trg_disputes_set_updated_at
        BEFORE UPDATE ON public.disputes
        FOR EACH ROW
        EXECUTE FUNCTION public.set_updated_at_tz();
    END IF;
END $$;

-- Attach trigger to notifications.updated_at
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger WHERE tgname = 'trg_notifications_set_updated_at'
    ) THEN
        CREATE TRIGGER trg_notifications_set_updated_at
        BEFORE UPDATE ON public.notifications
        FOR EACH ROW
        EXECUTE FUNCTION public.set_updated_at_tz();
    END IF;
END $$;
