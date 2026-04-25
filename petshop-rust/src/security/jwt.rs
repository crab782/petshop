use crate::error::AppError;
use chrono::{Duration, Utc};
use jsonwebtoken::{decode, encode, Algorithm, DecodingKey, EncodingKey, Header, Validation};
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Claims {
    pub sub: String,
    pub role: String,
    pub exp: usize,
    pub iat: usize,
}

pub fn generate_token(user_id: i32, role: &str, secret: &str) -> Result<String, AppError> {
    let expiration = Utc::now()
        .checked_add_signed(Duration::hours(24))
        .expect("valid timestamp")
        .timestamp() as usize;

    let claims = Claims {
        sub: user_id.to_string(),
        role: role.to_string(),
        exp: expiration,
        iat: Utc::now().timestamp() as usize,
    };

    let header = Header::new(Algorithm::HS256);
    encode(&header, &claims, EncodingKey::from_secret(secret.as_bytes()))
        .map_err(|e| AppError::InternalError(e.to_string()))
}

pub fn validate_token(token: &str, secret: &str) -> Result<Claims, AppError> {
    let validation = Validation::new(Algorithm::HS256);
    decode::<Claims>(
        token,
        &DecodingKey::from_secret(secret.as_bytes()),
        &validation,
    )
    .map(|data| data.claims)
    .map_err(|e| AppError::Unauthorized(e.to_string()))
}
