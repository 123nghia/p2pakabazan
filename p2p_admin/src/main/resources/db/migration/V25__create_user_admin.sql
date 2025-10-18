CREATE TABLE IF NOT EXISTS user_admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- password: admin (bcrypt)
-- Use Spring's DelegatingPasswordEncoder prefix {bcrypt} to ensure matching
INSERT INTO user_admin (username, password_hash, enabled)
VALUES ('admin', '{bcrypt}$2a$10$H3H2QbH0N3yNf5wJm3p3wO3oQyqTqE2v7mQm2VZx7bG9VgQG3Hc1K', TRUE)
ON CONFLICT (username) DO NOTHING;


