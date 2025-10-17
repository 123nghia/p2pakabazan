-- Allow system-generated trade chat messages
ALTER TABLE trade_chats
    ALTER COLUMN sender_id DROP NOT NULL;
