use chrono::{DateTime, NaiveDate, NaiveDateTime, Utc};

pub fn now() -> NaiveDateTime {
    Utc::now().naive_utc()
}

pub fn today() -> NaiveDate {
    Utc::now().date().naive_utc()
}

pub fn format_datetime(dt: &NaiveDateTime) -> String {
    dt.format("%Y-%m-%d %H:%M:%S").to_string()
}

pub fn format_date(d: &NaiveDate) -> String {
    d.format("%Y-%m-%d").to_string()
}