use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn get_cart(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn add_to_cart(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "添加成功"
    })))
}

pub async fn update_cart(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "更新成功"
    })))
}

pub async fn delete_cart_item(
    pool: web::Data<DbPool>,
    path: web::Path<i32>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "删除成功"
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/user/cart")
            .route("", web::get().to(get_cart))
            .route("", web::post().to(add_to_cart))
            .route("", web::put().to(update_cart))
            .route("/{id}", web::delete().to(delete_cart_item))
    );
}