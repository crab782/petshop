use actix_web::{web, HttpResponse};
use serde::{Deserialize, Serialize};
use diesel::prelude::*;
use crate::error::AppError;
use crate::database::pool::DbPool;
use crate::models::user::User;
use crate::models::pet::Pet;

#[derive(Debug, Serialize, Deserialize)]
pub struct ProfileResponse {
    pub id: i32,
    pub username: String,
    pub email: Option<String>,
    pub phone: String,
    pub avatar: Option<String>,
}

pub async fn get_profile(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    let conn = &mut pool.get().map_err(|e| AppError::DatabaseError(e.to_string()))?;

    let _users = users::table
        .filter(users::id.eq(1))
        .first::<User>(conn)
        .map_err(|_| AppError::NotFound("用户不存在".to_string()))?;

    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": {
            "id": 1,
            "username": "test",
            "phone": "13800138000"
        }
    })))
}

pub async fn update_profile(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "更新成功"
    })))
}

pub async fn get_pets(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn add_pet(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "添加成功"
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/user")
            .route("/profile", web::get().to(get_profile))
            .route("/profile", web::put().to(update_profile))
            .route("/pets", web::get().to(get_pets))
            .route("/pets", web::post().to(add_pet))
    );
}