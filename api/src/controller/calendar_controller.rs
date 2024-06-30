use axum::body::Body;
use axum::Json;
use axum::http::Response;
use axum::extract::{Extension, State};
use axum_extra::extract::Query;
use std::sync::{Arc, Mutex};
use crate::service::calendar_service::{
	get_calendar,
	create_calendar,
	update_calendar,
	delete_calendar,
};
use crate::helper::api::ApiResponse;
use crate::model::calendar::CalendarData;
use crate::config::route::{QueryParams, SharedData, SesionData};

pub async fn get_calendar_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, filters: Query<QueryParams>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let role = extension_l.role.clone().unwrap();
	let result = get_calendar(&state.pool, filters.0, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<Vec<CalendarData>> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<Vec<CalendarData>> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn post_calendar_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, Json(body): Json<CalendarData>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let role = extension_l.role.clone().unwrap();
	let result = create_calendar(&state.pool, body, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<CalendarData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<CalendarData> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn put_calendar_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, Json(body): Json<CalendarData>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let role = extension_l.role.clone().unwrap();

	let query = QueryParams {
		field: vec![].into(),
		value: vec![].into(),
		operator: vec![].into(),
		limit: None,
		offset: None,
		order: None,
		sort: None,
	};

	let result = update_calendar(&state.pool, body, query, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<CalendarData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<CalendarData> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn delete_calendar_controller(State(state): State<SharedData>, Json(body): Json<CalendarData>) -> Result<Response<Body>, Response<Body>> {

	let query = QueryParams {
		field: vec![].into(),
		value: vec![].into(),
		operator: vec![].into(),
		limit: None,
		offset: None,
		order: None,
		sort: None,
	};

	let result = delete_calendar(&state.pool, body, query);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<usize> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<usize> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}