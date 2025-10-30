-- Sample migration for framework data module testing
CREATE TABLE IF NOT EXISTS p2p_framework_data_probe (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
