-- =========================
-- 1. Tạo bảng fiat_accounts
-- =========================
CREATE TABLE fiat_accounts (
    id BIGSERIAL PRIMARY KEY,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(100) NOT NULL,
    account_holder VARCHAR(100) NOT NULL,
    branch VARCHAR(150),
    payment_type VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fiat_accounts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Trigger để cập nhật updated_at khi có UPDATE
CREATE OR REPLACE FUNCTION update_fiat_accounts_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_fiat_accounts_updated_at
BEFORE UPDATE ON fiat_accounts
FOR EACH ROW
EXECUTE FUNCTION update_fiat_accounts_updated_at();

-- =========================
-- 2. Thêm cột fiat_account_id vào orders
-- =========================
ALTER TABLE orders
ADD COLUMN fiat_account_id BIGINT NULL;

-- =========================
-- 3. Tạo khóa ngoại giữa orders và fiat_accounts
-- =========================
ALTER TABLE orders
ADD CONSTRAINT fk_orders_fiat_account
FOREIGN KEY (fiat_account_id) REFERENCES fiat_accounts(id);
