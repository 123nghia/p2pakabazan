-- Add recipient role column to trade chat messages
ALTER TABLE trade_chats
    ADD COLUMN IF NOT EXISTS recipient_role VARCHAR(16);
