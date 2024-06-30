// @generated automatically by Diesel CLI.

diesel::table! {
    bookings (id) {
        id -> Int4,
        external_id -> Varchar,
        property_id -> Int4,
        plattform_id -> Int4,
        payment_id -> Int4,
        user_id -> Int4,
        num_adults -> Int4,
        num_children -> Int4,
        booking_date -> Date,
        start_datetime -> Timestamp,
        end_datetime -> Timestamp,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    calendar (property_id, date) {
        id -> Int4,
        property_id -> Int4,
        booking_id -> Nullable<Int4>,
        date -> Date,
        price -> Numeric,
        available -> Bool,
        comment -> Nullable<Varchar>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    payments (id) {
        id -> Int4,
        user_id -> Int4,
        concept -> Varchar,
        amount -> Numeric,
        status -> Varchar,
        payment_date -> Date,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    plattforms (id) {
        id -> Int4,
        plattform_name -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    properties (id) {
        id -> Int4,
        name -> Varchar,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    roles (id) {
        id -> Int4,
        role_name -> Varchar,
        can_get_user -> Bool,
        can_create_user -> Bool,
        can_update_user -> Bool,
        can_delete_user -> Bool,
        detail_level_user -> Int4,
        bypass_user_validation -> Bool,
        can_get_plattform -> Bool,
        can_create_plattform -> Bool,
        can_update_plattform -> Bool,
        can_delete_plattform -> Bool,
        detail_level_platfform -> Int4,
        bypass_plattform_validation -> Bool,
        can_get_booking -> Bool,
        can_create_booking -> Bool,
        can_update_booking -> Bool,
        can_delete_booking -> Bool,
        detail_level_booking -> Int4,
        bypass_booking_validation -> Bool,
        can_get_role -> Bool,
        can_create_role -> Bool,
        can_update_role -> Bool,
        can_delete_role -> Bool,
        detail_level_role -> Int4,
        bypass_role_validation -> Bool,
        can_get_property -> Bool,
        can_create_property -> Bool,
        can_update_property -> Bool,
        can_delete_property -> Bool,
        detail_level_property -> Int4,
        bypass_property_validation -> Bool,
        can_get_calendar -> Bool,
        can_create_calendar -> Bool,
        can_update_calendar -> Bool,
        can_delete_calendar -> Bool,
        detail_level_calendar -> Int4,
        bypass_calendar_validation -> Bool,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::table! {
    users (id) {
        id -> Int4,
        user_name -> Varchar,
        email -> Nullable<Varchar>,
        phone -> Nullable<Varchar>,
        password -> Varchar,
        role_id -> Int4,
        tfa_enabled -> Bool,
        tfa_verified -> Bool,
        tfa_secret -> Nullable<Varchar>,
        tfa_auth_url -> Nullable<Varchar>,
        created_at -> Timestamp,
        updated_at -> Timestamp,
    }
}

diesel::joinable!(bookings -> payments (payment_id));
diesel::joinable!(bookings -> plattforms (plattform_id));
diesel::joinable!(bookings -> properties (property_id));
diesel::joinable!(bookings -> users (user_id));
diesel::joinable!(calendar -> bookings (booking_id));
diesel::joinable!(calendar -> properties (property_id));
diesel::joinable!(payments -> users (user_id));
diesel::joinable!(users -> roles (role_id));

diesel::allow_tables_to_appear_in_same_query!(
    bookings,
    calendar,
    payments,
    plattforms,
    properties,
    roles,
    users,
);
