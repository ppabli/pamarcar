use axum::body::Body;
use axum::Json;
use axum::http::Response;
use axum::Extension;
use axum::extract::State;
use axum_extra::extract::Query;
use std::sync::{Arc, Mutex};
use crate::helper::api::ApiResponse;
use crate::config::route::{QueryParams, SharedData, SesionData};
use crate::model::booking::BookingData;
use crate::service::booking_service::{
	get_booking,
	create_booking,
	update_booking,
	delete_booking,
};

pub async fn get_booking_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, filters: Query<QueryParams>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let user = extension_l.user.clone().unwrap();
	let role = extension_l.role.clone().unwrap();

	let result = get_booking(&state.pool, filters.0, user, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<Vec<BookingData>> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<Vec<BookingData>> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn post_booking_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, Json(data): Json<BookingData>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let user = extension_l.user.clone().unwrap();
	let role = extension_l.role.clone().unwrap();

	let result = create_booking(&state.pool, data, user, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<BookingData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<BookingData> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn put_booking_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, Json(data): Json<BookingData>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let user = extension_l.user.clone().unwrap();
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

	let result = update_booking(&state.pool, data, query, user, role);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<BookingData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<BookingData> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn delete_booking_controller(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, Json(data): Json<BookingData>) -> Result<Response<Body>, Response<Body>> {

	let extension_l = extension.lock().unwrap();
	let user = extension_l.user.clone().unwrap();
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

	let result = delete_booking(&state.pool, data, query, user, role);

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