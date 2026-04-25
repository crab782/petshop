use chrono::NaiveDateTime;
use diesel::prelude::*;
use serde::{Deserialize, Serialize};

#[derive(Queryable, Selectable, Serialize, Deserialize, Debug, Clone)]
#[diesel(table_name = announcements)]
pub struct Announcement {
    pub id: i32,
    pub title: String,
    pub content: String,
    pub status: String,
    pub created_at: NaiveDateTime,
    pub updated_at: NaiveDateTime,
}

#[derive(Insertable, Serialize, Deserialize)]
#[diesel(table_name = announcements)]
pub struct NewAnnouncement {
    pub title: String,
    pub content: String,
    pub status: String,
}