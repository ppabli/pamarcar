use serde::{Deserialize, Serialize};
use diesel::{prelude::*, Queryable, Insertable, Selectable, QueryResult};
use diesel::pg::PgConnection;
use diesel::sql_types::Bool;
use crate::schema::bookings::{
	self,
	dsl::bookings as booking_table,
};
use crate::helper::api::DetailLevel;
use crate::schema::bookings::dsl::id;
use crate::config::route::{QueryParams, parse_query_params};

#[derive(Queryable, Identifiable, Selectable, Serialize, Deserialize, Clone, Default, Debug)]
#[diesel(table_name = bookings)]
pub struct Booking {
	pub id: i32,
	pub external_id: String,
	pub property_id: i32,
	pub plattform_id: i32,
	pub payment_id: i32,
	pub user_id: i32,
	pub num_adults: i32,
	pub num_children: i32,
	pub booking_date: chrono::NaiveDate,
	pub start_datetime: chrono::NaiveDateTime,
	pub end_datetime: chrono::NaiveDateTime,
	pub created_at: chrono::NaiveDateTime,
	pub updated_at: chrono::NaiveDateTime,
}

#[derive(Deserialize, Serialize, Debug, Insertable, AsChangeset, Clone, Default)]
#[diesel(table_name = bookings)]
pub struct BookingData {
	#[serde(skip_serializing_if = "Option::is_none")]
	pub id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub external_id: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub property_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub plattform_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub payment_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub user_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub num_adults: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub num_children: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub booking_date: Option<chrono::NaiveDate>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub start_datetime: Option<chrono::NaiveDateTime>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub end_datetime: Option<chrono::NaiveDateTime>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub created_at: Option<chrono::NaiveDateTime>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub updated_at: Option<chrono::NaiveDateTime>,
}

impl Booking {

	pub fn filter_booking_data_input(booking_data: Vec<BookingData>, detail_level: DetailLevel) -> Vec<BookingData> {

		match detail_level {
			DetailLevel::Full => booking_data,
			DetailLevel::SemiFull => booking_data.iter().map(|booking| BookingData {
				id: booking.id,
				external_id: booking.external_id.clone(),
				property_id: booking.property_id,
				plattform_id: booking.plattform_id,
				payment_id: booking.payment_id,
				user_id: booking.user_id,
				num_adults: booking.num_adults,
				num_children: booking.num_children,
				booking_date: booking.booking_date,
				start_datetime: booking.start_datetime,
				end_datetime: booking.end_datetime,
				created_at: None,
				updated_at: None,
			}).collect(),
			DetailLevel::Partial => booking_data.iter().map(|booking| BookingData {
				id: booking.id,
				external_id: booking.external_id.clone(),
				property_id: booking.property_id,
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => booking_data.iter().map(|booking| BookingData {
				id: booking.id,
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],
		}

	}

	pub fn filter_booking_data_output(booking_data: Vec<Booking>, detail_level: DetailLevel) -> Vec<BookingData> {

		match detail_level {
			DetailLevel::Full => booking_data.iter().map(|booking| BookingData {
				id: Some(booking.id),
				external_id: Some(booking.external_id.clone()),
				property_id: Some(booking.property_id),
				plattform_id: Some(booking.plattform_id),
				payment_id: Some(booking.payment_id),
				user_id: Some(booking.user_id),
				num_adults: Some(booking.num_adults),
				num_children: Some(booking.num_children),
				booking_date: Some(booking.booking_date),
				start_datetime: Some(booking.start_datetime),
				end_datetime: Some(booking.end_datetime),
				created_at: Some(booking.created_at),
				updated_at: Some(booking.updated_at),
			}).collect(),
			DetailLevel::SemiFull => booking_data.iter().map(|booking| BookingData {
				id: Some(booking.id),
				external_id: Some(booking.external_id.clone()),
				property_id: Some(booking.property_id),
				plattform_id: Some(booking.plattform_id),
				payment_id: Some(booking.payment_id),
				user_id: Some(booking.user_id),
				num_adults: Some(booking.num_adults),
				num_children: Some(booking.num_children),
				booking_date: Some(booking.booking_date),
				start_datetime: Some(booking.start_datetime),
				end_datetime: Some(booking.end_datetime),
				..Default::default()
			}).collect(),
			DetailLevel::Partial => booking_data.iter().map(|booking| BookingData {
				id: Some(booking.id),
				external_id: Some(booking.external_id.clone()),
				property_id: Some(booking.property_id),
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => booking_data.iter().map(|booking| BookingData {
				id: Some(booking.id),
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],
		}

	}

	pub fn get_booking(conn: &mut PgConnection, params: QueryParams) -> Result<Vec<Booking>, diesel::result::Error> {

		let limit = params.limit.unwrap_or(10);
		let offset = params.offset.unwrap_or(0);

		let result = booking_table
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.limit(limit as i64)
			.offset(offset as i64)
			.load(conn);

		match result {
			Ok(bookings) => Ok(bookings),
			Err(e) => Err(e),
		}

	}

	pub fn create_booking(conn: &mut PgConnection, data: BookingData) -> Result<Booking, diesel::result::Error> {

		let result = diesel::insert_into(booking_table)
			.values(data)
			.get_result(conn);

		match result {
			Ok(booking) => Ok(booking),
			Err(e) => Err(e),
		}

	}

	pub fn update_booking(conn: &mut PgConnection, data: BookingData, params: QueryParams) -> Result<Booking, diesel::result::Error> {

		let result = diesel::update(booking_table)
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.set(data)
			.get_result(conn);

		match result {
			Ok(booking) => Ok(booking),
			Err(e) => Err(e),
		}

	}

	pub fn delete_booking(conn: &mut PgConnection, booking_id: i32, params: QueryParams) -> QueryResult<usize> {

		let result = diesel::delete(booking_table)
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.filter(id.eq(booking_id))
			.execute(conn);

		match result {
			Ok(booking) => Ok(booking),
			Err(e) => Err(e),
		}

	}

}