"""
测试数据库初始化脚本

用于 CI/CD 环境中初始化测试数据库，包括：
- 创建数据库表结构
- 插入基础测试数据
- 配置测试环境
"""

import argparse
import os
import sys
from pathlib import Path

import pymysql
from pymysql.cursors import DictCursor

DB_CONFIG = {
    "host": os.getenv("DB_HOST", "localhost"),
    "port": int(os.getenv("DB_PORT", "3306")),
    "user": os.getenv("DB_USER", "root"),
    "password": os.getenv("DB_PASSWORD", "123456"),
    "database": os.getenv("DB_NAME", "cg_test"),
    "charset": "utf8mb4",
}


def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG, cursorclass=DictCursor)


def create_tables(conn):
    """创建测试数据库表结构"""
    print("Creating database tables...")
    
    schema_file = Path(__file__).parent.parent.parent / "src" / "test" / "resources" / "schema.sql"
    
    if schema_file.exists():
        with open(schema_file, "r", encoding="utf-8") as f:
            schema_sql = f.read()
        
        cursor = conn.cursor()
        
        for statement in schema_sql.split(";"):
            statement = statement.strip()
            if statement:
                try:
                    cursor.execute(statement)
                except Exception as e:
                    print(f"Warning: {e}")
                    continue
        
        conn.commit()
        cursor.close()
        print("✓ Database tables created successfully")
    else:
        print(f"Warning: Schema file not found at {schema_file}")
        print("Creating basic tables...")
        create_basic_tables(conn)


def create_basic_tables(conn):
    """创建基础表结构"""
    cursor = conn.cursor()
    
    tables = [
        """
        CREATE TABLE IF NOT EXISTS user (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            email VARCHAR(100) NOT NULL UNIQUE,
            phone VARCHAR(20) UNIQUE,
            password VARCHAR(255) NOT NULL,
            avatar VARCHAR(255),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS merchant (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            contact_person VARCHAR(50) NOT NULL,
            phone VARCHAR(20) NOT NULL,
            email VARCHAR(100) NOT NULL,
            password VARCHAR(255) NOT NULL,
            address VARCHAR(255),
            logo VARCHAR(255),
            status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS admin (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            email VARCHAR(100) NOT NULL UNIQUE,
            password VARCHAR(255) NOT NULL,
            role VARCHAR(50) DEFAULT 'admin',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS pet (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            name VARCHAR(50) NOT NULL,
            type VARCHAR(50) NOT NULL,
            breed VARCHAR(50),
            age INT,
            gender ENUM('male', 'female'),
            avatar VARCHAR(255),
            description TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS service (
            id INT AUTO_INCREMENT PRIMARY KEY,
            merchant_id INT NOT NULL,
            name VARCHAR(100) NOT NULL,
            description TEXT,
            price DECIMAL(10, 2) NOT NULL,
            duration INT,
            image VARCHAR(255),
            status ENUM('enabled', 'disabled') DEFAULT 'enabled',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (merchant_id) REFERENCES merchant(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS product (
            id INT AUTO_INCREMENT PRIMARY KEY,
            merchant_id INT NOT NULL,
            name VARCHAR(100) NOT NULL,
            description TEXT,
            price DECIMAL(10, 2) NOT NULL,
            stock INT DEFAULT 0,
            image VARCHAR(255),
            status ENUM('enabled', 'disabled') DEFAULT 'enabled',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (merchant_id) REFERENCES merchant(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS appointment (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            merchant_id INT NOT NULL,
            service_id INT NOT NULL,
            pet_id INT,
            appointment_time DATETIME NOT NULL,
            status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending',
            total_price DECIMAL(10, 2),
            notes TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id),
            FOREIGN KEY (merchant_id) REFERENCES merchant(id),
            FOREIGN KEY (service_id) REFERENCES service(id),
            FOREIGN KEY (pet_id) REFERENCES pet(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS product_order (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            merchant_id INT NOT NULL,
            total_price DECIMAL(10, 2) NOT NULL,
            status ENUM('pending', 'paid', 'shipped', 'completed', 'cancelled') DEFAULT 'pending',
            shipping_address TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id),
            FOREIGN KEY (merchant_id) REFERENCES merchant(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS product_order_item (
            id INT AUTO_INCREMENT PRIMARY KEY,
            order_id INT NOT NULL,
            product_id INT NOT NULL,
            quantity INT NOT NULL,
            price DECIMAL(10, 2) NOT NULL,
            FOREIGN KEY (order_id) REFERENCES product_order(id),
            FOREIGN KEY (product_id) REFERENCES product(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS review (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            merchant_id INT NOT NULL,
            service_id INT,
            appointment_id INT,
            rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
            comment TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id),
            FOREIGN KEY (merchant_id) REFERENCES merchant(id),
            FOREIGN KEY (service_id) REFERENCES service(id),
            FOREIGN KEY (appointment_id) REFERENCES appointment(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS favorite (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            merchant_id INT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id),
            FOREIGN KEY (merchant_id) REFERENCES merchant(id),
            UNIQUE KEY unique_favorite (user_id, merchant_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS address (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            receiver_name VARCHAR(50) NOT NULL,
            phone VARCHAR(20) NOT NULL,
            province VARCHAR(50) NOT NULL,
            city VARCHAR(50) NOT NULL,
            district VARCHAR(50) NOT NULL,
            detail_address VARCHAR(255) NOT NULL,
            is_default BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS notification (
            id INT AUTO_INCREMENT PRIMARY KEY,
            user_id INT NOT NULL,
            title VARCHAR(255) NOT NULL,
            content TEXT,
            type VARCHAR(50),
            is_read BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES user(id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
        """
        CREATE TABLE IF NOT EXISTS announcement (
            id INT AUTO_INCREMENT PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            content TEXT,
            status ENUM('published', 'draft') DEFAULT 'published',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
    ]
    
    for table_sql in tables:
        try:
            cursor.execute(table_sql)
        except Exception as e:
            print(f"Warning creating table: {e}")
    
    conn.commit()
    cursor.close()
    print("✓ Basic tables created successfully")


def insert_test_data(conn):
    """插入基础测试数据"""
    print("Inserting test data...")
    
    cursor = conn.cursor()
    
    test_users = [
        ("testuser1", "test1@example.com", "13800138001", "password123"),
        ("testuser2", "test2@example.com", "13800138002", "password123"),
        ("testuser3", "test3@example.com", "13800138003", "password123"),
    ]
    
    for username, email, phone, password in test_users:
        try:
            cursor.execute(
                """
                INSERT INTO user (username, email, phone, password)
                VALUES (%s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE username=username
                """,
                (username, email, phone, password),
            )
        except Exception as e:
            print(f"Warning inserting user {username}: {e}")
    
    test_merchants = [
        ("测试商家1", "联系人1", "13900139001", "merchant1@example.com", "password123", "地址1", "approved"),
        ("测试商家2", "联系人2", "13900139002", "merchant2@example.com", "password123", "地址2", "approved"),
    ]
    
    for name, contact, phone, email, password, address, status in test_merchants:
        try:
            cursor.execute(
                """
                INSERT INTO merchant (name, contact_person, phone, email, password, address, status)
                VALUES (%s, %s, %s, %s, %s, %s, %s)
                ON DUPLICATE KEY UPDATE name=name
                """,
                (name, contact, phone, email, password, address, status),
            )
        except Exception as e:
            print(f"Warning inserting merchant {name}: {e}")
    
    test_admin = ("admin", "admin@example.com", "admin123")
    try:
        cursor.execute(
            """
            INSERT INTO admin (username, email, password)
            VALUES (%s, %s, %s)
            ON DUPLICATE KEY UPDATE username=username
            """,
            test_admin,
        )
    except Exception as e:
        print(f"Warning inserting admin: {e}")
    
    conn.commit()
    cursor.close()
    print("✓ Test data inserted successfully")


def clean_test_data(conn):
    """清理测试数据"""
    print("Cleaning test data...")
    
    cursor = conn.cursor()
    
    tables = [
        "review",
        "product_order_item",
        "product_order",
        "appointment",
        "favorite",
        "notification",
        "address",
        "pet",
        "product",
        "service",
        "user",
        "merchant",
        "admin",
    ]
    
    for table in tables:
        try:
            cursor.execute(f"DELETE FROM {table}")
        except Exception as e:
            print(f"Warning cleaning table {table}: {e}")
    
    conn.commit()
    cursor.close()
    print("✓ Test data cleaned successfully")


def check_database_connection():
    """检查数据库连接"""
    print("Checking database connection...")
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT 1")
        cursor.close()
        conn.close()
        print("✓ Database connection successful")
        return True
    except Exception as e:
        print(f"✗ Database connection failed: {e}")
        return False


def main():
    parser = argparse.ArgumentParser(description="Initialize test database")
    parser.add_argument(
        "--init-test-db",
        action="store_true",
        help="Initialize test database with tables and test data",
    )
    parser.add_argument(
        "--clean",
        action="store_true",
        help="Clean all test data from database",
    )
    parser.add_argument(
        "--check-connection",
        action="store_true",
        help="Check database connection",
    )
    
    args = parser.parse_args()
    
    if args.check_connection:
        success = check_database_connection()
        sys.exit(0 if success else 1)
    
    if not check_database_connection():
        sys.exit(1)
    
    conn = get_connection()
    
    try:
        if args.clean:
            clean_test_data(conn)
        elif args.init_test_db:
            create_tables(conn)
            insert_test_data(conn)
        else:
            print("No action specified. Use --init-test-db or --clean")
            parser.print_help()
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)
    finally:
        conn.close()
    
    print("\n✓ Test database initialization completed!")


if __name__ == "__main__":
    main()
