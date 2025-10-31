-- ============================
-- Migration: Currency master data
-- ============================

CREATE TABLE IF NOT EXISTS currencies (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    network VARCHAR(100),
    icon_url VARCHAR(255),
    decimal_places SMALLINT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE OR REPLACE FUNCTION update_currencies_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_currencies_updated_at
BEFORE UPDATE ON currencies
FOR EACH ROW
EXECUTE FUNCTION update_currencies_updated_at();

-- Seed token data
INSERT INTO currencies (type, code, name, network, decimal_places, display_order)
VALUES
    ('TOKEN', 'USDT', 'Tether USD', 'TRON', 6, 1),
    ('TOKEN', 'USDC', 'USD Coin', 'Ethereum', 6, 2),
    ('TOKEN', 'BTC', 'Bitcoin', 'Bitcoin', 8, 3),
    ('TOKEN', 'ETH', 'Ethereum', 'Ethereum', 8, 4),
    ('TOKEN', 'BNB', 'Binance Coin', 'BNB Smart Chain', 8, 5),
    ('TOKEN', 'SOL', 'Solana', 'Solana', 9, 6),
    ('TOKEN', 'TRX', 'Tron', 'TRON', 6, 7)
ON CONFLICT (code) DO NOTHING;

-- Seed fiat data
INSERT INTO currencies (type, code, name, decimal_places, display_order)
VALUES
    ('FIAT', 'VND', 'Vietnamese Dong', 0, 1),
    ('FIAT', 'USD', 'United States Dollar', 2, 2),
    ('FIAT', 'EUR', 'Euro', 2, 3),
    ('FIAT', 'JPY', 'Japanese Yen', 0, 4),
    ('FIAT', 'SGD', 'Singapore Dollar', 2, 5)
ON CONFLICT (code) DO NOTHING;
