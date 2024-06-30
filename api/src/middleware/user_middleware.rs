use axum::{
	body::Body,
	Extension,
	extract::{Request, State},
	http::Response,
	middleware::Next,
};
use std::sync::{Arc, Mutex};
use crate::helper::api::{ApiResponse, MiddlewareError};
use crate::config::route::{SharedData, SesionData, QueryParams};
use crate::model::role::{Role, validate_action};
use crate::model::user::User;

pub async fn validate_token(Extension(extension): Extension<Arc<Mutex<SesionData>>>, req: Request, next: Next) -> Result<Response<Body>, ApiResponse<MiddlewareError>> {

	let headers = req.headers();

	match headers.get("Authorization") {

		Some(token) => {

			let token = token.to_str().unwrap();
			let token = token.replace("Bearer ", "");

			match crate::helper::auth::decode_jwt(&token) {
				Ok(claims) => {
					extension.lock().unwrap().claims = Some(claims);
					let response = next.run(req).await;
					Ok(response)
				},
				Err(message) => {
					let api_response: ApiResponse<MiddlewareError> = ApiResponse::error(&message);
					let body_content = serde_json::to_string(&api_response).unwrap();
					let response = Response::builder()
						.status(200)
						.body(Body::from(body_content))
						.unwrap();
					Ok(response)
				}
			}
		},
		None => {
			let api_response: ApiResponse<MiddlewareError> = ApiResponse::error("Unauthorized");
			let body_content = serde_json::to_string(&api_response).unwrap();
			let response = Response::builder()
				.status(200)
				.body(Body::from(body_content))
				.unwrap();
			Ok(response)
		}
	}

}

pub async fn validate_rol(State(state): State<SharedData>, Extension(extension): Extension<Arc<Mutex<SesionData>>>, req: Request, next: Next) -> Result<Response<Body>, ApiResponse<MiddlewareError>> {

	let claims = extension.lock().unwrap().claims.clone().unwrap();
	let user_id = claims.user_id;

	let mut query = QueryParams {
		field: Some(vec![String::from("id")]),
		value: Some(vec![user_id.to_string()]),
		operator: Some(vec![String::from("=")]),
		..Default::default()
	};

	let user_data = User::get_user(&mut state.pool.get().unwrap(), query).unwrap().pop().unwrap();

	let role_id = user_data.role_id;

	query = QueryParams {
		field: Some(vec![String::from("id")]),
		value: Some(vec![role_id.to_string()]),
		operator: Some(vec![String::from("=")]),
		..Default::default()
	};

	let role_data = Role::get_role(&mut state.pool.get().unwrap(), query).unwrap().pop().unwrap();

	let path = req.uri().path();
	let method = req.method().to_string();

	if validate_action(path.to_string(), method, role_data.clone()) {
		extension.lock().unwrap().user = Some(user_data);
		extension.lock().unwrap().role = Some(role_data);
		let response = next.run(req).await;
		Ok(response)
	} else {
		let api_response: ApiResponse<MiddlewareError> = ApiResponse::error("Unauthorized");
		let body_content = serde_json::to_string(&api_response).unwrap();
		let response = Response::builder()
			.status(200)
			.body(Body::from(body_content))
			.unwrap();
		Ok(response)
	}

}