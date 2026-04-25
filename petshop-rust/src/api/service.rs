use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn get_services(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_service_detail(
    pool: web::Data<DbPool>,
    path: web::Path<i32>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": {
            "id": path.into_inner()
        }
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/services")
            .route("", web::get().to(get_services))
            .route("/{id}", web::get().to(get_service_detail))
    );
}