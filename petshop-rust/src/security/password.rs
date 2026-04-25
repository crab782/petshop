use crate::error::AppError;
use bcrypt::{hash, verify, DEFAULT_COST};

pub fn hash_password(password: &str) -> Result<String, AppError> {
    hash(password, DEFAULT_COST).map_err(|e| AppError::InternalError(e.to_string()))
}

pub fn verify_password(password: &str, hashed: &str) -> Result<bool, AppError> {
    verify(password, hashed).map_err(|e| AppError::InternalError(e.to_string()))
}
