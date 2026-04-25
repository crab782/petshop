// @generated automatically by Diesel CLI.

diesel::table! {
    users (id) {
        id -> Integer,
        username -> Varchar,
        email -> Nullable<Varchar>,
        password -> Varchar,
        phone -> Varchar,
        avatar -> Nullable<Varchar>,
        status -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    admins (id) {
        id -> Integer,
        username -> Varchar,
        email -> Nullable<Varchar>,
        password -> Varchar,
        phone -> Nullable<Varchar>,
        avatar -> Nullable<Varchar>,
        status -> Varchar,
        role -> Nullable<Varchar>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    merchants (id) {
        id -> Integer,
        name -> Varchar,
        contact_person -> Varchar,
        phone -> Varchar,
        email -> Nullable<Varchar>,
        password -> Varchar,
        address -> Varchar,
        logo -> Nullable<Varchar>,
        status -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    services (id) {
        id -> Integer,
        merchant_id -> Integer,
        name -> Varchar,
        description -> Nullable<Text>,
        price -> Decimal,
        duration -> Integer,
        image -> Nullable<Varchar>,
        status -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    products (id) {
        id -> Integer,
        merchant_id -> Integer,
        name -> Varchar,
        description -> Nullable<Text>,
        price -> Decimal,
        stock -> Integer,
        image -> Nullable<Varchar>,
        status -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    pets (id) {
        id -> Integer,
        user_id -> Integer,
        name -> Varchar,
        pet_type -> Varchar,
        breed -> Nullable<Varchar>,
        age -> Nullable<Int>,
        gender -> Nullable<Varchar>,
        avatar -> Nullable<Varchar>,
        description -> Nullable<Text>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    appointments (id) {
        id -> Integer,
        user_id -> Integer,
        merchant_id -> Integer,
        service_id -> Integer,
        pet_id -> Integer,
        appointment_time -> Datetime,
        status -> Varchar,
        total_price -> Decimal,
        notes -> Nullable<Text>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    product_orders (id) {
        id -> Integer,
        user_id -> Integer,
        merchant_id -> Integer,
        total_price -> Decimal,
        status -> Varchar,
        shipping_address -> Nullable<Varchar>,
        logistics_company -> Nullable<Varchar>,
        tracking_number -> Nullable<Varchar>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    product_order_items (id) {
        id -> Integer,
        order_id -> Integer,
        product_id -> Integer,
        quantity -> Integer,
        price -> Decimal,
    }
}

diesel::table! {
    reviews (id) {
        id -> Integer,
        user_id -> Integer,
        merchant_id -> Integer,
        service_id -> Nullable<Int>,
        appointment_id -> Nullable<Int>,
        product_id -> Nullable<Int>,
        rating -> Integer,
        comment -> Nullable<Text>,
        reply -> Nullable<Text>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    carts (id) {
        id -> Integer,
        user_id -> Integer,
        product_id -> Integer,
        quantity -> Integer,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    announcements (id) {
        id -> Integer,
        title -> Varchar,
        content -> Text,
        status -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    favorites (id) {
        id -> Integer,
        user_id -> Integer,
        merchant_id -> Nullable<Int>,
        service_id -> Nullable<Int>,
        product_id -> Nullable<Int>,
        created_at -> Timestamp,
    }
}

diesel::table! {
    notifications (id) {
        id -> Integer,
        user_id -> Integer,
        title -> Varchar,
        content -> Nullable<Text>,
        notification_type -> Nullable<Varchar>,
        is_read -> Bool,
        created_at -> Timestamp,
    }
}

diesel::table! {
    addresses (id) {
        id -> Integer,
        user_id -> Integer,
        receiver_name -> Varchar,
        phone -> Varchar,
        province -> Varchar,
        city -> Varchar,
        district -> Varchar,
        detail_address -> Varchar,
        is_default -> Bool,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::joinable!(appointments -> users (user_id));
diesel::joinable!(appointments -> merchants (merchant_id));
diesel::joinable!(appointments -> services (service_id));
diesel::joinable!(appointments -> pets (pet_id));
diesel::joinable!(carts -> users (user_id));
diesel::joinable!(favorites -> users (user_id));
diesel::joinable!(notifications -> users (user_id));
diesel::joinable!(appointments -> users (user_id));
diesel::joinable!(product_orders -> users (user_id));
diesel::joinable!(product_orders -> merchants (merchant_id));
diesel::joinable!(reviews -> users (user_id));
diesel::joinable!(reviews -> merchants (merchant_id));
diesel::joinable!(services -> merchants (merchant_id));
diesel::joinable!(products -> merchants (merchant_id));
diesel::joinable!(pets -> users (user_id));

diesel::allow_tables_to_appear_in_same_query!(
    users,
    admins,
    merchants,
    services,
    products,
    pets,
    appointments,
    product_orders,
    product_order_items,
    reviews,
    carts,
    announcements,
    favorites,
    notifications,
    addresses,
);
