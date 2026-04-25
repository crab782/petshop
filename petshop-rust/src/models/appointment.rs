use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = appointments)]
pub struct Appointment {
    pub id: i32,
    pub user_id: i32,
    pub merchant_id: i32,
    pub service_id: i32,
    pub pet_id: i32,
    pub appointment_time: NaiveDateTime,
    pub status: String,
    pub total_price: f64,
    pub notes: Option<String>,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = appointments)]
pub struct NewAppointment {
    pub user_id: i32,
    pub merchant_id: i32,
    pub service_id: i32,
    pub pet_id: i32,
    pub appointment_time: NaiveDateTime,
    pub status: String,
    pub total_price: f64,
    pub notes: Option<String>,
}
