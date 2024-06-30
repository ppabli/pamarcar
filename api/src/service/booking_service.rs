use crate::config::db::Pool;
use crate::model::booking::{Booking, BookingData};
use crate::model::calendar::CalendarData;
use crate::service::calendar_service::{get_calendar, update_calendar};
use crate::model::user::User;
use crate::model::role::Role;
use crate::helper::api::get_detail_level;
use crate::config::route::{QueryParams, validate_query_params};

pub fn get_booking(pool: &Pool, params: QueryParams, user: User, role: Role) -> Result<Vec<BookingData>, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	if !role.bypass_booking_validation {

		params.field.clone().expect("Expectec field").push(String::from("user_id"));
		params.value.clone().expect("Expectec value").push(user.id.to_string());
		params.operator.clone().expect("Expectec operator").push(String::from("="));

	}

	let result = Booking::get_booking(&mut pool.get().unwrap(), params);

	match result {
		Ok(data) => {
			Ok(Booking::filter_booking_data_output(data, get_detail_level(role.detail_level_booking)))
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn create_booking(pool: &Pool, data: BookingData, user: User, role: Role) -> Result<BookingData, String> {

	let mut new_data = Booking::filter_booking_data_input(vec![data.clone()], get_detail_level(role.detail_level_booking)).pop().unwrap();

	if !role.bypass_booking_validation {
		new_data.user_id = Some(user.id);
	}

	let params = QueryParams {
		field: Some(vec![String::from("start_datetime"), String::from("end_datetime"), String::from("available")]),
		value: Some(vec![data.start_datetime.unwrap().to_string(), data.end_datetime.unwrap().to_string(), String::from("false")]),
		operator: Some(vec![String::from(">="), String::from("<"), String::from("=")]),
		..Default::default()
	};

	let check_availability = get_calendar(pool, params, role.clone());

	if check_availability.is_ok() && check_availability.unwrap().len() > 0 {
		return Err(String::from("Dates already booked"));
	}

	let result = Booking::create_booking(&mut pool.get().unwrap(), new_data);

	if !result.is_ok() {
		return Err(result.err().unwrap().to_string());
	}

	let new_booking = result.unwrap().clone();

	let updated_data = CalendarData {
		available: Some(false),
		booking_id: Some(new_booking.id),
		..Default::default()
	};

	let params = QueryParams {
		field: Some(vec![String::from("start_data"), String::from("end_date")]),
		value: Some(vec![data.start_datetime.unwrap().to_string(), data.end_datetime.unwrap().to_string()]),
		operator: Some(vec![String::from(">="), String::from("<")]),
		..Default::default()
	};

	let update_availability = update_calendar(pool, updated_data, params, role.clone());

	match update_availability {
		Ok(_) => {
			Ok(Booking::filter_booking_data_output(vec![new_booking], get_detail_level(role.detail_level_booking)).pop().unwrap())
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn update_booking(pool: &Pool, data: BookingData, params: QueryParams, user: User, role: Role) -> Result<BookingData, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	let mut new_data = Booking::filter_booking_data_input(vec![data.clone()], get_detail_level(role.detail_level_booking)).pop().unwrap();

	if !role.bypass_booking_validation {

		params.field.clone().expect("Expectec field").push(String::from("user_id"));
		params.value.clone().expect("Expectec value").push(user.id.to_string());
		params.operator.clone().expect("Expectec operator").push(String::from("="));

		new_data.user_id = Some(user.id);

	}

	let result = Booking::update_booking(&mut pool.get().unwrap(), new_data, params);

	if !result.is_ok() {
		return Err(result.err().unwrap().to_string());
	}

	let new_booking = result.unwrap().clone();

	let updated_data = CalendarData {
		booking_id: Some(new_booking.id),
		available: Some(false),
		..Default::default()
	};

	let params = QueryParams {
		field: Some(vec![String::from("start_data"), String::from("end_date")]),
		value: Some(vec![data.start_datetime.unwrap().to_string(), data.end_datetime.unwrap().to_string()]),
		operator: Some(vec![String::from(">="), String::from("<")]),
		..Default::default()
	};

	let update_availability = update_calendar(pool, updated_data, params, role.clone());

	match update_availability {
		Ok(_) => {
			Ok(Booking::filter_booking_data_output(vec![new_booking], get_detail_level(role.detail_level_booking)).pop().unwrap())
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn delete_booking(pool: &Pool, data: BookingData, params: QueryParams, user: User, role: Role) -> Result<usize, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	if !role.bypass_booking_validation {

		params.field.clone().expect("Expectec field").push(String::from("user_id"));
		params.value.clone().expect("Expectec value").push(user.id.to_string());
		params.operator.clone().expect("Expectec operator").push(String::from("="));

	}

	let result = Booking::delete_booking(&mut pool.get().unwrap(), data.id.unwrap(), params);

	if !result.is_ok() {
		return Err(result.err().unwrap().to_string());
	}

	let updated_data = CalendarData {
		available: Some(true),
		booking_id: None,
		..Default::default()
	};

	let params = QueryParams {
		field: Some(vec![String::from("booking_id")]),
		value: Some(vec![data.id.unwrap().to_string()]),
		operator: Some(vec![String::from("=")]),
		..Default::default()
	};

	let update_availability = update_calendar(pool, updated_data, params, role.clone());

	match update_availability {
		Ok(_) => {
			Ok(result.unwrap())
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}