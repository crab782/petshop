use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn get_products(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_product_detail(
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
        web::scope("/api/products")
            .route("", web::get().to(get_products))
            .route("/{id}", web::get().to(get_product_detail))
    );
}