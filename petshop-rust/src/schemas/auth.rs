use serde::{Deserialize, Serialize};
use validator::Validate;
use utoipa::ToSchema;

#[derive(Deserialize, Serialize, Validate, ToSchema)]
pub struct LoginRequest {
    #[validate(length(min = 1, message = "登录标识不能为空"))]
    pub login_identifier: String,
    #[validate(length(min = 6, message = "密码长度至少6位"))]
    pub password: String,
}

#[derive(Serialize, ToSchema)]
pub struct LoginResponse {
    pub token: String,
    pub user_id: i32,
    pub username: String,
}

#[derive(Deserialize, Serialize, Validate, ToSchema)]
pub struct RegisterRequest {
    #[validate(length(min = 11, max = 11, message = "手机号必须是11位"))]
    pub phone: String,
    #[validate(length(min = 6, message = "密码长度至少6位"))]
    pub password: String,
    pub username: Option<String>,
    #[validate(email(message = "邮箱格式不正确"))]
    pub email: Option<String>,
}
