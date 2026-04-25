use actix_web::{web, HttpResponse};
use crate::error::AppError;
use crate::database::pool::DbPool;

pub async fn search(
    pool: web::Data<DbPool>,
    query: web::Query<SearchQuery>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": {
            "keyword": query.keyword,
            "results": []
        }
    })))
}

pub async fn get_suggestions(
    pool: web::Data<DbPool>,
    query: web::Query<SuggestionQuery>,
) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "data": []
    })))
}

#[derive(serde::Deserialize)]
pub struct SearchQuery {
    pub keyword: String,
}

#[derive(serde::Deserialize)]
pub struct SuggestionQuery {
    pub keyword: String,
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api/search")
            .route("", web::get().to(search))
            .route("/suggestions", web::get().to(get_suggestions))
    );
}