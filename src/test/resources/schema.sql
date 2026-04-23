CREATE TABLE IF NOT EXISTS "user" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(255),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "merchant" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    contact_person VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    password VARCHAR(255),
    address VARCHAR(255),
    logo VARCHAR(255),
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "admin" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "service" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2),
    duration INT,
    image VARCHAR(255),
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'enabled',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "product" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2),
    stock INT,
    image VARCHAR(255),
    category VARCHAR(50),
    status VARCHAR(20) DEFAULT 'enabled',
    low_stock_threshold INT DEFAULT 10,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "appointment" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    merchant_id INT,
    service_id INT,
    pet_id INT,
    appointment_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending',
    total_price DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "product_order" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50),
    user_id INT,
    merchant_id INT,
    total_price DECIMAL(10,2),
    freight DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'pending',
    pay_method VARCHAR(20),
    remark TEXT,
    shipping_address TEXT,
    logistics_company VARCHAR(100),
    logistics_number VARCHAR(100),
    transaction_id VARCHAR(100),
    paid_at TIMESTAMP,
    shipped_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "product_order_item" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS "review" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    merchant_id INT,
    service_id INT,
    appointment_id INT,
    rating INT,
    comment TEXT,
    reply_content TEXT,
    reply_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "pet" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(50),
    type VARCHAR(50),
    breed VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    avatar VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "address" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "favorite" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    merchant_id INT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "announcement" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    status VARCHAR(20) DEFAULT 'published',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "notification" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title VARCHAR(255),
    content TEXT,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "merchant_settings" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT,
    business_hours VARCHAR(100),
    notification_settings TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "system_settings" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(100),
    logo VARCHAR(255),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    icp_number VARCHAR(50),
    site_description TEXT,
    site_keywords TEXT,
    footer_text TEXT,
    smtp_host VARCHAR(100),
    smtp_port INT,
    smtp_username VARCHAR(100),
    smtp_password VARCHAR(255),
    sms_api_key VARCHAR(255),
    sms_api_secret VARCHAR(255),
    sms_sign_name VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "category" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    merchant_id INT,
    name VARCHAR(100),
    icon VARCHAR(255),
    sort INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'enabled',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "operation_log" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT,
    action VARCHAR(100),
    target_type VARCHAR(50),
    target_id INT,
    detail TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "role" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    description VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "permission" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "scheduled_task" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    task_type VARCHAR(50),
    cron_expression VARCHAR(100),
    status VARCHAR(20) DEFAULT 'enabled',
    last_run_time TIMESTAMP,
    next_run_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "activity" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(50),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'enabled',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "system_config" (
    id INT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(255),
    logo VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    icp_number VARCHAR(100),
    site_description TEXT,
    site_keywords VARCHAR(500),
    footer_text TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
