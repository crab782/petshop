CREATE TABLE merchants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    logo VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_merchants_name ON merchants(name);
CREATE INDEX idx_merchants_phone ON merchants(phone);
CREATE INDEX idx_merchants_email ON merchants(email);
CREATE INDEX idx_merchants_status ON merchants(status);
