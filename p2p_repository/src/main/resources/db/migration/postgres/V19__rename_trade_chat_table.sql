-- ========================================
-- Migration: Rename trade_chat to trade_chats
-- ========================================

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

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_class
        WHERE relname = 'idx_trade_chat_trade_id'
    ) THEN
        ALTER INDEX idx_trade_chat_trade_id RENAME TO idx_trade_chats_trade_id;
    END IF;
END $$;
