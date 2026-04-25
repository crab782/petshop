CREATE TABLE product_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    merchant_id INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    shipping_address VARCHAR(255),
    logistics_company VARCHAR(255),
    tracking_number VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_orders_user_id ON product_orders(user_id);
CREATE INDEX idx_product_orders_merchant_id ON product_orders(merchant_id);
CREATE INDEX idx_product_orders_status ON product_orders(status);
CREATE INDEX idx_product_orders_created_at ON product_orders(created_at);
