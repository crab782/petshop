use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = reviews)]
pub struct Review {
    pub id: i32,
    pub user_id: i32,
    pub merchant_id: i32,
    pub service_id: Option<i32>,
    pub appointment_id: Option<i32>,
    pub product_id: Option<i32>,
    pub rating: i32,
    pub comment: Option<String>,
    pub reply: Option<String>,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = reviews)]
pub struct NewReview {
    pub user_id: i32,
    pub merchant_id: i32,
    pub service_id: Option<i32>,
    pub appointment_id: Option<i32>,
    pub product_id: Option<i32>,
    pub rating: i32,
    pub comment: Option<String>,
}
