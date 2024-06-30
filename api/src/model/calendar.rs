use serde::{Deserialize, Serialize};
use diesel::{prelude::*, Queryable, Insertable, Selectable, QueryResult};
use diesel::pg::PgConnection;
use diesel::sql_types::Bool;
use crate::schema::calendar::{
	self,
	dsl::calendar as calendar_table,
};
use crate::helper::api::DetailLevel;
use crate::schema::calendar::dsl::id;
use crate::config::route::{QueryParams, parse_query_params};

#[derive(Queryable, Identifiable, Selectable, Serialize, Deserialize, Default, Debug)]
#[diesel(table_name = calendar)]
pub struct Calendar {
	pub id: i32,
	pub property_id: i32,
	pub booking_id: Option<i32>,
	pub date: chrono::NaiveDate,
	pub price: bigdecimal::BigDecimal,
	pub available: bool,
	pub comment: Option<String>,
	pub created_at: chrono::NaiveDateTime,
	pub updated_at: chrono::NaiveDateTime,
}

#[derive(Deserialize, Serialize, Debug, Insertable, AsChangeset, Default, Clone)]
#[diesel(table_name = calendar)]
pub struct CalendarData {
	#[serde(skip_serializing_if = "Option::is_none")]
	pub id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub property_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub booking_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub date: Option<chrono::NaiveDate>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub price: Option<bigdecimal::BigDecimal>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub available: Option<bool>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub comment: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub created_at: Option<chrono::NaiveDateTime>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub updated_at: Option<chrono::NaiveDateTime>,
}

impl Calendar {

	pub fn filter_calendar_data_input(calendar_data: Vec<CalendarData>, detail_level: DetailLevel) -> Vec<CalendarData> {

		match detail_level {

			DetailLevel::Full => calendar_data,
			DetailLevel::SemiFull => calendar_data.iter().map(|calendar| CalendarData {
				id: calendar.id,
				property_id: calendar.property_id,
				booking_id: calendar.booking_id,
				date: calendar.date,
				price: calendar.price.clone(),
				available: calendar.available,
				..Default::default()
			}).collect(),
			DetailLevel::Partial => calendar_data.iter().map(|calendar| CalendarData {
				id: calendar.id,
				property_id: calendar.property_id,
				date: calendar.date,
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => calendar_data.iter().map(|calendar| CalendarData {
				id: calendar.id,
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],

		}

	}

	pub fn filter_calendar_data_output(calendar_data: Vec<Calendar>, detail_level: DetailLevel) -> Vec<CalendarData> {

		match detail_level {

			DetailLevel::Full => calendar_data.iter().map(|calendar| CalendarData {
				id: Some(calendar.id),
				property_id: Some(calendar.property_id),
				booking_id: Some(calendar.booking_id.unwrap()),
				date: Some(calendar.date),
				price: Some(calendar.price.clone()),
				available: Some(calendar.available),
				comment: calendar.comment.clone(),
				created_at: Some(calendar.created_at),
				updated_at: Some(calendar.updated_at),
			}).collect(),
			DetailLevel::SemiFull => calendar_data.iter().map(|calendar| CalendarData {
				id: Some(calendar.id),
				property_id: Some(calendar.property_id),
				booking_id: Some(calendar.booking_id.unwrap()),
				date: Some(calendar.date),
				price: Some(calendar.price.clone()),
				available: Some(calendar.available),
				..Default::default()
			}).collect(),
			DetailLevel::Partial => calendar_data.iter().map(|calendar| CalendarData {
				id: Some(calendar.id),
				property_id: Some(calendar.property_id),
				date: Some(calendar.date),
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => calendar_data.iter().map(|calendar| CalendarData {
				id: Some(calendar.id),
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],
		}

	}

	pub fn get_calendar(conn: &mut PgConnection, params: QueryParams) -> Result<Vec<Calendar>, diesel::result::Error> {

		let limit = params.limit.unwrap_or(10);
		let offset = params.offset.unwrap_or(0);

		let result: QueryResult<Vec<Calendar>> = calendar_table
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.limit(limit as i64)
			.offset(offset as i64)
			.load(conn);

		match result {
			Ok(calendar) => Ok(calendar),
			Err(e) => Err(e),
		}

	}

	pub fn create_calendar(conn: &mut PgConnection, calendar_data: CalendarData) -> QueryResult<Calendar> {

		let result: QueryResult<Calendar> = diesel::insert_into(calendar_table)
			.values(&calendar_data)
			.get_result(conn);

		match result {
			Ok(calendar) => Ok(calendar),
			Err(e) => Err(e),
		}

	}

	pub fn update_calendar(conn: &mut PgConnection, calendar_data: CalendarData, params: QueryParams) -> QueryResult<Calendar> {

		let result: QueryResult<Calendar> = diesel::update(calendar_table)
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.set(&calendar_data)
			.get_result(conn);

		match result {
			Ok(calendar) => Ok(calendar),
			Err(e) => Err(e),
		}

	}

	pub fn delete_calendar(conn: &mut PgConnection, calendar_id: i32, params: QueryParams) -> QueryResult<usize> {

		let result: QueryResult<usize> = diesel::delete(calendar_table)
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.filter(id.eq(calendar_id))
			.execute(conn);

		match result {
			Ok(calendar) => Ok(calendar),
			Err(e) => Err(e),
		}

	}

}