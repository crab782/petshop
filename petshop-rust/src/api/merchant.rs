use actix_web::{web, HttpResponse};
use serde::{Deserialize, Serialize};
use diesel::prelude::*;
use crate::error::AppError;
use crate::database::pool::DbPool;

#[derive(Debug, Serialize, Deserialize)]
pub struct MerchantProfileResponse {
    pub id: i32,
    pub name: String,
    pub contact_person: String,
    pub phone: String,
    pub email: Option<String>,
    pub address: String,
    pub logo: Option<String>,
    pub status: String,
}

pub async fn get_profile(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": {
            "id": 1,
            "name": "Test Merchant",
            "status": "approved"
        }
    })))
}

pub async fn get_services(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_products(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub async fn get_appointments(
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/merchant")
            .route("/profile", web::get().to(get_profile))
            .route("/services", web::get().to(get_services))
            .route("/products", web::get().to(get_products))
            .route("/appointments", web::get().to(get_appointments))
    );
}