-- ============================
-- Migration: Payment method master data
-- ============================

CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(30) NOT NULL,
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    icon_url VARCHAR(255),
    display_order INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP
);

CREATE OR REPLACE FUNCTION update_payment_methods_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_payment_methods_updated_at
BEFORE UPDATE ON payment_methods
FOR EACH ROW
EXECUTE FUNCTION update_payment_methods_updated_at();

INSERT INTO payment_methods (type, code, name, description, display_order)
VALUES
    ('BANK_TRANSFER', 'BANK_TRANSFER', 'Chuyển khoản ngân hàng', 'Thanh toán qua tài khoản ngân hàng', 1),
    ('E_WALLET', 'MOMO', 'MoMo', 'Thanh toán qua ví điện tử MoMo', 2),
    ('E_WALLET', 'ZALOPAY', 'ZaloPay', 'Thanh toán qua ví điện tử ZaloPay', 3),
    ('E_WALLET', 'VIETTELPAY', 'Viettel Money', 'Thanh toán qua ví điện tử Viettel Money', 4),
    ('CASH', 'CASH', 'Tiền mặt', 'Thanh toán trực tiếp bằng tiền mặt', 5)
ON CONFLICT (code) DO NOTHING;
