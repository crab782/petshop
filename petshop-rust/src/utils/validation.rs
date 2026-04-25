use validator::Validate;

pub fn validate_email(email: &str) -> bool {
    email.contains('@') && email.contains('.')
}

pub fn validate_phone(phone: &str) -> bool {
    phone.len() >= 11 && phone.chars().all(|c| c.is_ascii_digit())
}

pub fn validate_password(password: &str) -> bool {
    password.len() >= 6
}