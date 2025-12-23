-- Track per-user read timestamp for trade chats
CREATE TABLE IF NOT EXISTS trade_chat_reads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trade_id UUID NOT NULL REFERENCES trades(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_read_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    CONSTRAINT uq_trade_chat_read UNIQUE (trade_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_trade_chat_read_user ON trade_chat_reads(user_id);
CREATE INDEX IF NOT EXISTS idx_trade_chat_read_trade ON trade_chat_reads(trade_id);
