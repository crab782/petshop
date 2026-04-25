use actix_web::{web, HttpResponse, HttpRequest};
use serde::{Deserialize, Serialize};
use validator::Validate;
use utoipa::{OpenApi, ToSchema};

use crate::error::AppError;
use crate::models::{Admin, User};
use crate::models::user::NewUser;
use crate::schemas::auth::{LoginRequest, LoginResponse, RegisterRequest};
use crate::security::jwt::{generate_token, validate_token};
use crate::security::password::{hash_password, verify_password};
use crate::database::pool::DbPool;
use crate::database::schema::{users, admins};
use diesel::prelude::*;

#[derive(OpenApi)]
#[openapi(
    paths(login, register, logout, get_current_user),
    components(schemas(AuthResponse, LoginRequest, LoginResponse, RegisterRequest))
)]
pub struct ApiDoc;

#[derive(Debug, Serialize, Deserialize, ToSchema)]
pub struct AuthResponse {
    pub success: bool,
    pub message: String,
    pub data: Option<LoginResponse>,
}

#[utoipa::path(
    post,
    path = "/auth/login",
    request_body = LoginRequest,
    responses(
        (status = 200, description = "登录成功", body = AuthResponse),
        (status = 401, description = "认证失败")
    )
)]
pub async fn login(
    pool: web::Data<DbPool>,
    body: web::Json<LoginRequest>,
) -> Result<HttpResponse, AppError> {
    let conn = &mut pool.get().map_err(|e| AppError::DatabaseError(e.to_string()))?;

    let user_result = users::table
        .filter(users::phone.eq(&body.login_identifier))
        .or_filter(users::email.eq(&body.login_identifier))
        .first::<User>(conn);

    match user_result {
        Ok(user) => {
            let is_valid = verify_password(&body.password, &user.password)?;
            if !is_valid {
                return Err(AppError::Unauthorized("密码错误".to_string()));
            }

            let secret = std::env::var("JWT_SECRET").unwrap_or_else(|_| "default_secret".to_string());
            let token = generate_token(user.id, "user", &secret)?;

            Ok(HttpResponse::Ok().json(AuthResponse {
                success: true,
                message: "登录成功".to_string(),
                data: Some(LoginResponse {
                    token,
                    user_id: user.id,
                    username: user.username,
                }),
            }))
        }
        Err(_) => {
            let admin_result = admins::table
                .filter(admins::username.eq(&body.login_identifier))
                .first::<Admin>(conn);

            match admin_result {
                Ok(admin) => {
                    let is_valid = verify_password(&body.password, &admin.password)?;
                    if !is_valid {
                        return Err(AppError::Unauthorized("密码错误".to_string()));
                    }

                    let secret = std::env::var("JWT_SECRET").unwrap_or_else(|_| "default_secret".to_string());
                    let token = generate_token(admin.id, "admin", &secret)?;

                    Ok(HttpResponse::Ok().json(AuthResponse {
                        success: true,
                        message: "管理员登录成功".to_string(),
                        data: Some(LoginResponse {
                            token,
                            user_id: admin.id,
                            username: admin.username,
                        }),
                    }))
                }
                Err(_) => Err(AppError::Unauthorized("用户不存在".to_string())),
            }
        }
    }
}

#[utoipa::path(
    post,
    path = "/auth/register",
    request_body = RegisterRequest,
    responses(
        (status = 200, description = "注册成功"),
        (status = 400, description = "请求参数错误")
    )
)]
pub async fn register(
    pool: web::Data<DbPool>,
    body: web::Json<RegisterRequest>,
) -> Result<HttpResponse, AppError> {
    let conn = &mut pool.get().map_err(|e| AppError::DatabaseError(e.to_string()))?;

    let existing_user = users::table
        .filter(users::phone.eq(&body.phone))
        .first::<User>(conn);

    if existing_user.is_ok() {
        return Err(AppError::BadRequest("手机号已被注册".to_string()));
    }

    if let Some(ref email) = body.email {
        let existing_email = users::table
            .filter(users::email.eq(email))
            .first::<User>(conn);

        if existing_email.is_ok() {
            return Err(AppError::BadRequest("邮箱已被注册".to_string()));
        }
    }

    let hashed_password = hash_password(&body.password)
        .map_err(|e| AppError::InternalError(e.to_string()))?;

    let username = body.username.clone().unwrap_or_else(|| body.phone.clone());

    let new_user = NewUser {
        username,
        email: body.email.clone(),
        password: hashed_password,
        phone: body.phone.clone(),
        avatar: None,
    };

    diesel::insert_into(users::table)
        .values(&new_user)
        .execute(conn)
        .map_err(|e| AppError::DatabaseError(e.to_string()))?;

    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "注册成功"
    })))
}

#[utoipa::path(
    post,
    path = "/auth/logout",
    responses(
        (status = 200, description = "登出成功")
    )
)]
pub async fn logout(_req: HttpRequest) -> Result<HttpResponse, AppError> {
    Ok(HttpResponse::Ok().json(serde_json::json!({
        "success": true,
        "message": "登出成功"
    })))
}

#[utoipa::path(
    get,
    path = "/auth/me",
    responses(
        (status = 200, description = "获取当前用户信息成功"),
        (status = 401, description = "未认证")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn get_current_user(
    req: HttpRequest,
    pool: web::Data<DbPool>,
) -> Result<HttpResponse, AppError> {
    let auth_header = req
        .headers()
        .get("Authorization")
        .ok_or_else(|| AppError::Unauthorized("缺少认证信息".to_string()))?
        .to_str()
        .map_err(|_| AppError::Unauthorized("无效的认证头".to_string()))?;

    let token = auth_header.trim_start_matches("Bearer ");
    let secret = std::env::var("JWT_SECRET").unwrap_or_else(|_| "default_secret".to_string());
    let claims = validate_token(token, &secret)?;

    let user_id: i32 = claims.sub.parse().map_err(|_| AppError::Unauthorized("无效的用户ID".to_string()))?;

    let conn = &mut pool.get().map_err(|e| AppError::DatabaseError(e.to_string()))?;

    match claims.role.as_str() {
        "user" => {
            let user = users::table
                .filter(users::id.eq(user_id))
                .first::<User>(conn)
                .map_err(|_| AppError::NotFound("用户不存在".to_string()))?;

            Ok(HttpResponse::Ok().json(serde_json::json!({
                "success": true,
                "data": {
                    "id": user.id,
                    "username": user.username,
                    "email": user.email,
                    "phone": user.phone,
                    "avatar": user.avatar,
                    "status": user.status,
                    "role": "user"
                }
            })))
        }
        "admin" => {
            let admin = admins::table
                .filter(admins::id.eq(user_id))
                .first::<Admin>(conn)
                .map_err(|_| AppError::NotFound("管理员不存在".to_string()))?;

            Ok(HttpResponse::Ok().json(serde_json::json!({
                "success": true,
                "data": {
                    "id": admin.id,
                    "username": admin.username,
                    "email": admin.email,
                    "role": "admin"
                }
            })))
        }
        _ => Err(AppError::Unauthorized("无效的角色".to_string())),
    }
}

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/auth")
            .route("/login", web::post().to(login))
            .route("/register", web::post().to(register))
            .route("/logout", web::post().to(logout))
            .route("/me", web::get().to(get_current_user))
    );
}