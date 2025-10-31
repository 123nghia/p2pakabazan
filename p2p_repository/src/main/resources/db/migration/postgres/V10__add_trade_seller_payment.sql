ALTER TABLE trades
    ADD COLUMN IF NOT EXISTS seller_fiat_account_id BIGINT,
    ADD COLUMN IF NOT EXISTS seller_bank_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS seller_account_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS seller_account_holder VARCHAR(150),
    ADD COLUMN IF NOT EXISTS seller_bank_branch VARCHAR(150),
    ADD COLUMN IF NOT EXISTS seller_payment_type VARCHAR(50);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_trades_seller_fiat_account'
          AND table_name = 'trades'
    ) THEN
        ALTER TABLE trades
            ADD CONSTRAINT fk_trades_seller_fiat_account
            FOREIGN KEY (seller_fiat_account_id) REFERENCES fiat_accounts(id)
            ON DELETE SET NULL;
    END IF;
END;
$$;
