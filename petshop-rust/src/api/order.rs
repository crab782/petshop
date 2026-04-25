use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn get_orders(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_order_detail(
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

pub async fn create_order(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "订单创建成功"
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/user/orders")
            .route("", web::get().to(get_orders))
            .route("", web::post().to(create_order))
            .route("/{id}", web::get().to(get_order_detail))
    );
}