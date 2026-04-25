use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = crate::schema::products)]
pub struct Product {
    pub id: i32,
    pub merchant_id: i32,
    pub name: String,
    pub description: Option<String>,
    pub price: f64,
    pub stock: i32,
    pub image: Option<String>,
    pub status: String,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = crate::schema::products)]
pub struct NewProduct {
    pub merchant_id: i32,
    pub name: String,
    pub description: Option<String>,
    pub price: f64,
    pub stock: i32,
    pub image: Option<String>,
}
