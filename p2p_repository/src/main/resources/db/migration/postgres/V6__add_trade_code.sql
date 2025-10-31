-- Ensure every trade has a unique human-readable code similar to Binance P2P order numbers.
BEGIN;

ALTER TABLE public.trades
    ADD COLUMN IF NOT EXISTS trade_code VARCHAR(32);

-- Backfill existing rows with deterministic values (prefix + zero padded id) so uniqueness is guaranteed.
UPDATE public.trades
SET trade_code = CONCAT('TR', LPAD(id::text, 18, '0'))
WHERE trade_code IS NULL;

-- Enforce not-null going forward.
ALTER TABLE public.trades
    ALTER COLUMN trade_code SET NOT NULL;

-- Create a unique index/constraint if missing.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_trades_trade_code'
          AND conrelid = 'public.trades'::regclass
    ) THEN
        ALTER TABLE public.trades
            ADD CONSTRAINT uk_trades_trade_code UNIQUE (trade_code);
    END IF;
END $$;

COMMIT;
