use serde::{Deserialize, Serialize};
use diesel::{prelude::*, Queryable};
use diesel::pg::PgConnection;
use diesel::sql_types::Bool;
use crate::schema::roles::{
	self,
	dsl::roles as role_table,
};
use crate::config::route::{QueryParams, parse_query_params};

#[derive(Queryable, Identifiable, Selectable, Serialize, Deserialize, Clone, Debug)]
#[diesel(table_name = roles)]
pub struct Role {
	pub id: i32,
	pub role_name: String,
	pub can_get_user: bool,
	pub can_create_user: bool,
	pub can_update_user: bool,
	pub can_delete_user: bool,
	pub detail_level_user: i32,
	pub bypass_user_validation: bool,
	pub can_get_plattform: bool,
	pub can_create_plattform: bool,
	pub can_update_plattform: bool,
	pub can_delete_plattform: bool,
	pub detail_level_platfform: i32,
	pub bypass_plattform_validation: bool,
	pub can_get_booking: bool,
	pub can_create_booking: bool,
	pub can_update_booking: bool,
	pub can_delete_booking: bool,
	pub detail_level_booking: i32,
	pub bypass_booking_validation: bool,
	pub can_get_role: bool,
	pub can_create_role: bool,
	pub can_update_role: bool,
	pub can_delete_role: bool,
	pub detail_level_role: i32,
	pub bypass_role_validation: bool,
	pub can_get_property: bool,
	pub can_create_property: bool,
	pub can_update_property: bool,
	pub can_delete_property: bool,
	pub detail_level_property: i32,
	pub bypass_property_validation: bool,
	pub can_get_calendar: bool,
	pub can_create_calendar: bool,
	pub can_update_calendar: bool,
	pub can_delete_calendar: bool,
	pub detail_level_calendar: i32,
	pub bypass_calendar_validation: bool,
	pub created_at: chrono::NaiveDateTime,
	pub updated_at: chrono::NaiveDateTime,
}

pub fn validate_action(path: String, method: String, role_data: Role) -> bool {

	match method.as_str() {
		"GET" => {
			match path.as_str() {
				"/users" => role_data.can_get_user && role_data.detail_level_user > 0,
				"/plattforms" => role_data.can_get_plattform && role_data.detail_level_platfform > 0,
				"/bookings" => role_data.can_get_booking && role_data.detail_level_booking > 0,
				"/roles" => role_data.can_get_role && role_data.detail_level_role > 0,
				"/property" => role_data.can_get_property && role_data.detail_level_property > 0,
				"/calendar" => role_data.can_get_calendar && role_data.detail_level_calendar > 0,
				_ => false,
			}
		},
		"POST" => {
			match path.as_str() {
				"/users" => role_data.can_create_user && role_data.detail_level_user > 0,
				"/plattforms" => role_data.can_create_plattform && role_data.detail_level_platfform > 0,
				"/bookings" => role_data.can_create_booking && role_data.detail_level_booking > 0,
				"/roles" => role_data.can_create_role && role_data.detail_level_role > 0,
				"/property" => role_data.can_create_property && role_data.detail_level_property > 0,
				"/calendar" => role_data.can_create_calendar && role_data.detail_level_calendar > 0,
				_ => false,
			}
		},
		"PUT" => {
			match path.as_str() {
				"/users" => role_data.can_update_user && role_data.detail_level_user > 0,
				"/plattforms" => role_data.can_update_plattform && role_data.detail_level_platfform > 0,
				"/bookings" => role_data.can_update_booking && role_data.detail_level_booking > 0,
				"/roles" => role_data.can_update_role && role_data.detail_level_role > 0,
				"/property" => role_data.can_update_property && role_data.detail_level_property > 0,
				"/calendar" => role_data.can_update_calendar && role_data.detail_level_calendar > 0,
				_ => false,
			}
		},
		"DELETE" => {
			match path.as_str() {
				"/users" => role_data.can_delete_user,
				"/plattforms" => role_data.can_delete_plattform,
				"/bookings" => role_data.can_delete_booking,
				"/roles" => role_data.can_delete_role,
				"/property" => role_data.can_delete_property,
				"/calendar" => role_data.can_delete_calendar,
				_ => false,
			}
		},
		_ => false,
	}

}

impl Role {

	pub fn get_role(conn: &mut PgConnection, params: QueryParams) -> Result<Vec<Role>, diesel::result::Error> {

		let limit = params.limit.unwrap_or(10);
		let offset = params.offset.unwrap_or(0);

		let result = role_table
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.limit(limit as i64)
			.offset(offset as i64)
			.load(conn);

		match result {
			Ok(role) => Ok(role),
			Err(e) => Err(e),
		}

	}

}