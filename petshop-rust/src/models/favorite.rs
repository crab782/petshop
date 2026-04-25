use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = favorites)]
pub struct Favorite {
    pub id: i32,
    pub user_id: i32,
    pub merchant_id: Option<i32>,
    pub service_id: Option<i32>,
    pub product_id: Option<i32>,
    pub created_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = favorites)]
pub struct NewFavorite {
    pub user_id: i32,
    pub merchant_id: Option<i32>,
    pub service_id: Option<i32>,
    pub product_id: Option<i32>,
}