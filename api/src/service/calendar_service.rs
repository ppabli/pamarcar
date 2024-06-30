use crate::config::db::Pool;
use crate::model::calendar::{Calendar, CalendarData};
use crate::model::role::Role;
use crate::helper::api::get_detail_level;
use crate::config::route::{QueryParams, validate_query_params};

pub fn get_calendar(pool: &Pool, params: QueryParams, role: Role) -> Result<Vec<CalendarData>, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	let result = Calendar::get_calendar(&mut pool.get().unwrap(), params);

	match result {
		Ok(data) => {
			Ok(Calendar::filter_calendar_data_output(data, get_detail_level(role.detail_level_calendar)))
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn create_calendar(pool: &Pool, data: CalendarData, role: Role) -> Result<CalendarData, String> {

	let new_data = Calendar::filter_calendar_data_input(vec![data], get_detail_level(role.detail_level_calendar)).pop().unwrap();

	let result = Calendar::create_calendar(&mut pool.get().unwrap(), new_data);

	match result {
		Ok(data) => {
			let tmp = vec![data];
			Ok(Calendar::filter_calendar_data_output(tmp, get_detail_level(role.detail_level_calendar)).pop().unwrap())
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn update_calendar(pool: &Pool, data: CalendarData, params: QueryParams, role: Role) -> Result<CalendarData, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	let new_data = Calendar::filter_calendar_data_input(vec![data], get_detail_level(role.detail_level_calendar)).pop().unwrap();

	let result = Calendar::update_calendar(&mut pool.get().unwrap(), new_data, params);

	match result {
		Ok(data) => {
			let tmp = vec![data];
			Ok(Calendar::filter_calendar_data_output(tmp, get_detail_level(role.detail_level_calendar)).pop().unwrap())
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}

pub fn delete_calendar(pool: &Pool, data: CalendarData, params: QueryParams) -> Result<usize, String> {

	if !validate_query_params(params.clone()) {
		return Err(String::from("Invalid query params"));
	}

	let result = Calendar::delete_calendar(&mut pool.get().unwrap(), data.id.unwrap(), params);

	match result {
		Ok(data) => {
			Ok(data)
		},
		Err(err) => {
			Err(err.to_string())
		}
	}

}