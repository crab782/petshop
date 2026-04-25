use std::env;

#[derive(Debug, Clone)]
pub struct Config {
    pub app: AppConfig,
    pub database: DatabaseConfig,
    pub security: SecurityConfig,
}

#[derive(Debug, Clone)]
pub struct AppConfig {
    pub port: u16,
}

#[derive(Debug, Clone)]
pub struct DatabaseConfig {
    pub url: String,
}

#[derive(Debug, Clone)]
pub struct SecurityConfig {
    pub jwt_secret: String,
}

impl Config {
    pub fn load() -> Self {
        Self {
            app: AppConfig {
                port: env::var("PORT")
                    .unwrap_or_else(|_| "8080".to_string())
                    .parse()
                    .unwrap_or(8080),
            },
            database: DatabaseConfig {
                url: env::var("DATABASE_URL")
                    .expect("DATABASE_URL must be set"),
            },
            security: SecurityConfig {
                jwt_secret: env::var("JWT_SECRET")
                    .expect("JWT_SECRET must be set"),
            },
        }
    }
}