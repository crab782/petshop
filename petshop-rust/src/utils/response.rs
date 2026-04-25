use serde::Serialize;

#[derive(Serialize)]
pub struct ApiResponse<T: Serialize> {
    pub success: bool,
    pub message: String,
    pub data: Option<T>,
}

impl<T: Serialize> ApiResponse<T> {
    pub fn success(data: T) -> Self {
        Self {
            success: true,
            message: "操作成功".to_string(),
            data: Some(data),
        }
    }

    pub fn success_with_message(message: &str, data: T) -> Self {
        Self {
            success: true,
            message: message.to_string(),
            data: Some(data),
        }
    }

    pub fn error(message: &str) -> Self {
        Self {
            success: false,
            message: message.to_string(),
            data: None,
        }
    }
}

#[derive(Serialize)]
pub struct PageResponse<T: Serialize> {
    pub success: bool,
    pub data: Vec<T>,
    pub total: i64,
    pub page: i32,
    pub page_size: i32,
}

impl<T: Serialize> PageResponse<T> {
    pub fn new(data: Vec<T>, total: i64, page: i32, page_size: i32) -> Self {
        Self {
            success: true,
            data,
            total,
            page,
            page_size,
        }
    }
}