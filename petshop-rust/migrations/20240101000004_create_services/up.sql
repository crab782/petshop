CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration INT NOT NULL,
    image VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'enabled',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);

CREATE INDEX idx_services_merchant_id ON services(merchant_id);
CREATE INDEX idx_services_name ON services(name);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_services_price ON services(price);
