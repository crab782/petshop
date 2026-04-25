use actix_web::{HttpResponse, ResponseError};
use serde::Serialize;
use thiserror::Error;

#[derive(Error, Debug)]
pub enum AppError {
    #[error("认证失败: {0}")]
    Unauthorized(String),

    #[error("禁止访问: {0}")]
    Forbidden(String),

    #[error("资源未找到: {0}")]
    NotFound(String),

    #[error("请求参数错误: {0}")]
    BadRequest(String),

    #[error("内部服务器错误: {0}")]
    InternalError(String),

    #[error("数据库错误: {0}")]
    DatabaseError(String),
}

#[derive(Serialize)]
struct ErrorResponse {
    success: bool,
    message: String,
    code: String,
}

impl ResponseError for AppError {
    fn error_response(&self) -> HttpResponse {
        let (status, code) = match self {
            AppError::Unauthorized(_) => (actix_web::http::StatusCode::UNAUTHORIZED, "UNAUTHORIZED"),
            AppError::Forbidden(_) => (actix_web::http::StatusCode::FORBIDDEN, "FORBIDDEN"),
            AppError::NotFound(_) => (actix_web::http::StatusCode::NOT_FOUND, "NOT_FOUND"),
            AppError::BadRequest(_) => (actix_web::http::StatusCode::BAD_REQUEST, "BAD_REQUEST"),
            AppError::InternalError(_) => {
                (actix_web::http::StatusCode::INTERNAL_SERVER_ERROR, "INTERNAL_ERROR")
            }
            AppError::DatabaseError(_) => {
                (actix_web::http::StatusCode::INTERNAL_SERVER_ERROR, "DATABASE_ERROR")
            }
        };

        HttpResponse::build(status).json(ErrorResponse {
            success: false,
            message: self.to_string(),
            code: code.to_string(),
        })
    }
}