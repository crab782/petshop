use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = addresses)]
pub struct Address {
    pub id: i32,
    pub user_id: i32,
    pub receiver_name: String,
    pub phone: String,
    pub province: String,
    pub city: String,
    pub district: String,
    pub detail_address: String,
    pub is_default: bool,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = addresses)]
pub struct NewAddress {
    pub user_id: i32,
    pub receiver_name: String,
    pub phone: String,
    pub province: String,
    pub city: String,
    pub district: String,
    pub detail_address: String,
    pub is_default: bool,
}