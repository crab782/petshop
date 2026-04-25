use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = merchants)]
pub struct Merchant {
    pub id: i32,
    pub name: String,
    pub contact_person: String,
    pub phone: String,
    pub email: Option<String>,
    pub password: String,
    pub address: String,
    pub logo: Option<String>,
    pub status: String,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = merchants)]
pub struct NewMerchant {
    pub name: String,
    pub contact_person: String,
    pub phone: String,
    pub email: Option<String>,
    pub password: String,
    pub address: String,
    pub logo: Option<String>,
}
