use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = product_orders)]
pub struct ProductOrder {
    pub id: i32,
    pub user_id: i32,
    pub merchant_id: i32,
    pub total_price: f64,
    pub status: String,
    pub shipping_address: Option<String>,
    pub logistics_company: Option<String>,
    pub tracking_number: Option<String>,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = product_orders)]
pub struct NewProductOrder {
    pub user_id: i32,
    pub merchant_id: i32,
    pub total_price: f64,
    pub status: String,
    pub shipping_address: Option<String>,
}
