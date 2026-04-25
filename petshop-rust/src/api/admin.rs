use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn get_users(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_merchants(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_announcements(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/admin")
            .route("/users", web::get().to(get_users))
            .route("/merchants", web::get().to(get_merchants))
            .route("/announcements", web::get().to(get_announcements))
    );
}