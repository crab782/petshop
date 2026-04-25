use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = notifications)]
pub struct Notification {
    pub id: i32,
    pub user_id: i32,
    pub title: String,
    pub content: Option<String>,
    pub notification_type: Option<String>,
    pub is_read: bool,
    pub created_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = notifications)]
pub struct NewNotification {
    pub user_id: i32,
    pub title: String,
    pub content: Option<String>,
    pub notification_type: Option<String>,
    pub is_read: bool,
}