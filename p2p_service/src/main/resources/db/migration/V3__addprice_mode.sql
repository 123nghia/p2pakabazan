-- =========================
-- V3: Thêm cột price_mode vào bảng orders
-- =========================
ALTER TABLE orders
ADD COLUMN price_mode VARCHAR(50) NOT NULL DEFAULT 'normal';
