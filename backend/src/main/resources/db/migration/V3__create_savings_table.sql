CREATE TABLE savings (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    description VARCHAR(500),
    user_id BINARY(16) NOT NULL,
    deposit_transaction_id BINARY(16),
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    CONSTRAINT fk_savings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_savings_deposit_transaction FOREIGN KEY (deposit_transaction_id) REFERENCES transactions(id) ON DELETE SET NULL
);

CREATE INDEX idx_savings_user_id ON savings(user_id);
CREATE INDEX idx_savings_name ON savings(name);
CREATE INDEX idx_savings_type ON savings(type);
CREATE INDEX idx_savings_deposit_transaction ON savings(deposit_transaction_id);
