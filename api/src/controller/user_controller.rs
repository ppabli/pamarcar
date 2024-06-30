use axum::body::Body;
use axum::http::Response;
use axum::{
	extract::Json, extract::State
};
use crate::helper::api::{ApiResponse, DisableTFA, GenerateTFA, LoginResponse, VerifyTFA};
use crate::model::user::{RequiredLoginUser, RequiredRegisterUser, UserData};
use crate::service::user_service::{login, register, generate_tfa, verify_tfa, disable_tfa};
use crate::config::route::{SharedData, SesionData};

pub async fn login_controller(State(state): State<SharedData>, Json(user_data): Json<RequiredLoginUser>) -> Result<Response<Body>, Response<Body>> {

	let result = login(user_data, &state.pool);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<LoginResponse> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<LoginResponse> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn register_controller(State(state): State<SharedData>, Json(user_data): Json<RequiredRegisterUser>) -> Result<Response<Body>, Response<Body>> {

	let result = register(user_data, &state.pool, state.mq_conn);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<UserData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.header("Content-Type", "application/json")
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<UserData> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn generate_tfa_controller(State(state): State<SharedData>, Json(user_data): Json<GenerateTFA>) -> Result<Response<Body>, Response<Body>> {

	let result = generate_tfa(user_data.id, user_data.email, &state.pool);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<String> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<String> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn verify_tfa_controller(State(state): State<SharedData>, Json(user_data): Json<VerifyTFA>) -> Result<Response<Body>, Response<Body>> {

	let result = verify_tfa(user_data.id, user_data.token, &state.pool);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<UserData> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<String> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn disable_tfa_controller(State(state): State<SharedData>, Json(user_data): Json<DisableTFA>) -> Result<Response<Body>, Response<Body>> {

	let result = disable_tfa(user_data.id, &state.pool);

	match result {
		Ok(data) => {
			let api_response: ApiResponse<String> = ApiResponse::success(data);
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
		Err(err) => {
			let api_response: ApiResponse<String> = ApiResponse::error(&err.to_string());
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}