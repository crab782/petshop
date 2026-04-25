use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = crate::schema::pets)]
pub struct Pet {
    pub id: i32,
    pub user_id: i32,
    pub name: String,
    pub pet_type: String,
    pub breed: Option<String>,
    pub age: Option<i32>,
    pub gender: Option<String>,
    pub avatar: Option<String>,
    pub description: Option<String>,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = crate::schema::pets)]
pub struct NewPet {
    pub user_id: i32,
    pub name: String,
    pub pet_type: String,
    pub breed: Option<String>,
    pub age: Option<i32>,
    pub gender: Option<String>,
    pub avatar: Option<String>,
    pub description: Option<String>,
}
