-- =========================================================
-- Migration: Convert legacy BIGINT identifiers to UUID
-- =========================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Ensure consistent table naming (older environments may still use trade_chat)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'trade_chat'
    ) THEN
        ALTER TABLE public.trade_chat RENAME TO trade_chats;
    END IF;
END $$;

-- Drop foreign keys prior to type changes
ALTER TABLE wallet_transactions DROP CONSTRAINT IF EXISTS wallet_transactions_wallet_id_fkey;
ALTER TABLE wallet_transactions DROP CONSTRAINT IF EXISTS wallet_transactions_user_id_fkey;
ALTER TABLE wallet_transactions DROP CONSTRAINT IF EXISTS wallet_transactions_performed_by_fkey;
ALTER TABLE wallets DROP CONSTRAINT IF EXISTS wallets_user_id_fkey;
ALTER TABLE fiat_accounts DROP CONSTRAINT IF EXISTS fk_fiat_accounts_user;
ALTER TABLE users_wallets DROP CONSTRAINT IF EXISTS users_wallets_user_id_fkey;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_user_id_fkey;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS fk_orders_fiat_account;
ALTER TABLE trades DROP CONSTRAINT IF EXISTS trades_order_id_fkey;
ALTER TABLE trades DROP CONSTRAINT IF EXISTS trades_buyer_id_fkey;
ALTER TABLE trades DROP CONSTRAINT IF EXISTS trades_seller_id_fkey;
ALTER TABLE trades DROP CONSTRAINT IF EXISTS fk_trades_seller_fiat_account;
ALTER TABLE trade_chats DROP CONSTRAINT IF EXISTS trade_chats_trade_id_fkey;
ALTER TABLE trade_chats DROP CONSTRAINT IF EXISTS trade_chat_trade_id_fkey;
ALTER TABLE trade_chats DROP CONSTRAINT IF EXISTS trade_chats_sender_id_fkey;
ALTER TABLE trade_chats DROP CONSTRAINT IF EXISTS trade_chat_sender_id_fkey;
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS disputes_trade_id_fkey;
ALTER TABLE disputes DROP CONSTRAINT IF EXISTS fk_dispute_admin;
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_user_id_fkey;

-- Users ---------------------------------------------------
ALTER TABLE users
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE users
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000101', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS users_id_seq;

-- User admin ----------------------------------------------
ALTER TABLE user_admin
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE user_admin
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000102', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4();
DROP SEQUENCE IF EXISTS user_admin_id_seq;

-- Master data tables --------------------------------------
ALTER TABLE currencies
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE currencies
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-00000000010c', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS currencies_id_seq;

ALTER TABLE payment_methods
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE payment_methods
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-00000000010d', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS payment_methods_id_seq;

-- Wallets -------------------------------------------------
ALTER TABLE wallets
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE wallets
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000103', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS wallets_id_seq;

-- Fiat accounts -------------------------------------------
ALTER TABLE fiat_accounts
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE fiat_accounts
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000104', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS fiat_accounts_id_seq;

-- Users wallets bridge ------------------------------------
ALTER TABLE users_wallets
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE users_wallets
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-00000000010b', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS users_wallets_id_seq;

-- Orders --------------------------------------------------
ALTER TABLE orders
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE orders
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000105', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN fiat_account_id TYPE uuid USING CASE WHEN fiat_account_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000104', fiat_account_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS orders_id_seq;

-- Trades --------------------------------------------------
ALTER TABLE trades
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE trades
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000106', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN order_id TYPE uuid USING CASE WHEN order_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000105', order_id::text) END,
    ALTER COLUMN buyer_id TYPE uuid USING CASE WHEN buyer_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', buyer_id::text) END,
    ALTER COLUMN seller_id TYPE uuid USING CASE WHEN seller_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', seller_id::text) END,
    ALTER COLUMN seller_fiat_account_id TYPE uuid USING CASE WHEN seller_fiat_account_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000104', seller_fiat_account_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS trades_id_seq;

-- Trade chats ---------------------------------------------
ALTER TABLE trade_chats
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE trade_chats
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000107', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN trade_id TYPE uuid USING CASE WHEN trade_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000106', trade_id::text) END,
    ALTER COLUMN sender_id TYPE uuid USING CASE WHEN sender_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', sender_id::text) END;
DROP SEQUENCE IF EXISTS trade_chats_id_seq;

-- Disputes ------------------------------------------------
ALTER TABLE disputes
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE disputes
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000108', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN trade_id TYPE uuid USING CASE WHEN trade_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000106', trade_id::text) END,
    ALTER COLUMN assigned_admin_id TYPE uuid USING CASE WHEN assigned_admin_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', assigned_admin_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS disputes_id_seq;

-- Notifications ------------------------------------------
ALTER TABLE notifications
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE notifications
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-000000000109', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS notifications_id_seq;

-- Wallet transactions ------------------------------------
ALTER TABLE wallet_transactions
    ALTER COLUMN id DROP DEFAULT;
ALTER TABLE wallet_transactions
    ALTER COLUMN id TYPE uuid USING uuid_generate_v5('00000000-0000-0000-0000-00000000010a', id::text),
    ALTER COLUMN id SET DEFAULT uuid_generate_v4(),
    ALTER COLUMN wallet_id TYPE uuid USING CASE WHEN wallet_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000103', wallet_id::text) END,
    ALTER COLUMN user_id TYPE uuid USING CASE WHEN user_id IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', user_id::text) END,
    ALTER COLUMN performed_by TYPE uuid USING CASE WHEN performed_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', performed_by::text) END,
    ALTER COLUMN reference_id TYPE uuid USING CASE
        WHEN reference_id IS NULL THEN NULL
        WHEN reference_type ILIKE 'ORDER' THEN uuid_generate_v5('00000000-0000-0000-0000-000000000105', reference_id::text)
        WHEN reference_type ILIKE 'TRADE' THEN uuid_generate_v5('00000000-0000-0000-0000-000000000106', reference_id::text)
        WHEN reference_type ILIKE 'WALLET' THEN uuid_generate_v5('00000000-0000-0000-0000-000000000103', reference_id::text)
        ELSE uuid_generate_v5('00000000-0000-0000-0000-00000000ffff', reference_id::text)
    END,
    ALTER COLUMN created_by TYPE uuid USING CASE WHEN created_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', created_by::text) END,
    ALTER COLUMN updated_by TYPE uuid USING CASE WHEN updated_by IS NULL THEN NULL ELSE uuid_generate_v5('00000000-0000-0000-0000-000000000101', updated_by::text) END;
DROP SEQUENCE IF EXISTS wallet_transactions_id_seq;

-- Re-create foreign keys with UUID columns ----------------
ALTER TABLE wallets
    ADD CONSTRAINT wallets_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE fiat_accounts
    ADD CONSTRAINT fk_fiat_accounts_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE users_wallets
    ADD CONSTRAINT users_wallets_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE orders
    ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_orders_fiat_account FOREIGN KEY (fiat_account_id) REFERENCES fiat_accounts(id);

ALTER TABLE trades
    ADD CONSTRAINT trades_order_id_fkey FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    ADD CONSTRAINT trades_buyer_id_fkey FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT trades_seller_id_fkey FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_trades_seller_fiat_account FOREIGN KEY (seller_fiat_account_id) REFERENCES fiat_accounts(id) ON DELETE SET NULL;

ALTER TABLE trade_chats
    ADD CONSTRAINT trade_chats_trade_id_fkey FOREIGN KEY (trade_id) REFERENCES trades(id) ON DELETE CASCADE,
    ADD CONSTRAINT trade_chats_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE disputes
    ADD CONSTRAINT disputes_trade_id_fkey FOREIGN KEY (trade_id) REFERENCES trades(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_dispute_admin FOREIGN KEY (assigned_admin_id) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE notifications
    ADD CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE wallet_transactions
    ADD CONSTRAINT wallet_transactions_wallet_id_fkey FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    ADD CONSTRAINT wallet_transactions_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT wallet_transactions_performed_by_fkey FOREIGN KEY (performed_by) REFERENCES users(id) ON DELETE SET NULL;
