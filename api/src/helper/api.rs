use axum::response::{IntoResponse, Response};
use axum::http::StatusCode;
use serde::{Deserialize, Serialize};

use crate::model::user::UserData;

#[derive(PartialEq)]
pub enum DetailLevel {
	Full,
	SemiFull,
	Partial,
	Minimal,
	Cero
}

pub fn get_detail_level(level: i32) -> DetailLevel {
	match level {
		0 => DetailLevel::Cero,
		1 => DetailLevel::Minimal,
		2 => DetailLevel::Partial,
		3 => DetailLevel::SemiFull,
		4 => DetailLevel::Full,
		_ => DetailLevel::Cero,
	}
}

#[derive(Serialize, Deserialize)]
pub struct MiddlewareError {
	pub code: i32,
	pub message: String,
}

#[derive(Serialize)]
pub struct LoginResponse {
	pub token: String,
	pub user: UserData,
}

#[derive(Serialize, Deserialize)]
pub struct ApiResponse<T> {
	pub code: i32,
	pub status: String,
	pub message: String,
	pub data: Option<T>,
}

#[derive(Debug, Deserialize)]
pub struct GenerateTFA {
	pub email: String,
	pub id: i32,
}

#[derive(Debug, Deserialize)]
pub struct VerifyTFA {
	pub id: i32,
	pub token: i32,
}

#[derive(Debug, Deserialize)]
pub struct DisableTFA {
	pub id: i32,
}

impl<T> ApiResponse<T> {

	pub fn success(data: T) -> Self {
		ApiResponse {
			code: 200,
			status: "success".to_string(),
			message: "".to_string(),
			data: Some(data),
		}
	}

	pub fn error(message: &str) -> Self {
		ApiResponse {
			code: 400,
			status: "error".to_string(),
			message: message.to_string(),
			data: None,
		}
	}

}

impl<T: Serialize> IntoResponse for ApiResponse<T> {
	fn into_response(self) -> Response {
		let status_code = match self.status.as_str() {
			"success" => StatusCode::OK,
			"error" => StatusCode::INTERNAL_SERVER_ERROR,
			_ => StatusCode::BAD_REQUEST,
		};

		let body = serde_json::to_string(&self).unwrap();

		(status_code, body).into_response()
	}
}