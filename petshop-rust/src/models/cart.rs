use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = carts)]
pub struct Cart {
    pub id: i32,
    pub user_id: i32,
    pub product_id: i32,
    pub quantity: i32,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = carts)]
pub struct NewCart {
    pub user_id: i32,
    pub product_id: i32,
    pub quantity: i32,
}
