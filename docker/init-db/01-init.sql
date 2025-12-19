-- Initialize database
-- This script runs automatically when the database container starts for the first time

-- Create database if not exists (already created by POSTGRES_DB env var)
-- But we can add custom initialization here

-- Set timezone
SET timezone = 'UTC';

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'Database p2p_trading_dev initialized successfully';
END $$;
