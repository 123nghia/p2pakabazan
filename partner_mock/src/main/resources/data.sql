-- Seed demo partner wallet data

INSERT INTO partner_wallet_balances (external_user_id, asset, available_balance, locked_balance)
VALUES
    ('user_1001', 'USDT', 100, 0),
    ('user_1001', 'BNB', 100, 0)
ON CONFLICT (external_user_id, asset) DO NOTHING;

