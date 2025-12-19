-- =========================================
-- Migration: Add funds lock id columns (for partner/exchange integrations)
-- - orders.funds_lock_id: lock reference for SELL orders (order-level escrow)
-- - trades.funds_lock_id: lock reference for BUY order trades (trade-level escrow)
-- =========================================

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS funds_lock_id VARCHAR(200);

ALTER TABLE trades
    ADD COLUMN IF NOT EXISTS funds_lock_id VARCHAR(200);

CREATE INDEX IF NOT EXISTS idx_orders_funds_lock_id
    ON orders(funds_lock_id);

CREATE INDEX IF NOT EXISTS idx_trades_funds_lock_id
    ON trades(funds_lock_id);

