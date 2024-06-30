use axum::http::Method;
use axum::Router;
use lapin::Connection;
use axum::middleware;
use axum::Extension;
use axum::routing::{get, post, put, delete};
use tower::ServiceBuilder;
use tower_http::cors::{Any, CorsLayer};
use std::sync::{Arc, Mutex};
use serde::Deserialize;
use crate::controller::user_controller::{login_controller, register_controller, generate_tfa_controller, verify_tfa_controller, disable_tfa_controller};
use crate::config::db::Pool;
use crate::helper::auth::Claims;
use crate::middleware::user_middleware::{validate_rol, validate_token};
use crate::controller::calendar_controller::{get_calendar_controller, post_calendar_controller, put_calendar_controller, delete_calendar_controller};
use crate::controller::booking_controller::{get_booking_controller, post_booking_controller, put_booking_controller, delete_booking_controller};
use crate::model::user::User;
use crate::model::role::Role;

#[derive(Clone, Debug)]
pub struct SharedData {
	pub pool: Arc<Pool>,
	pub mq_conn: Arc<Connection>,
}

#[derive(Debug, Clone)]
pub struct SesionData {
	pub user: Option<User>,
	pub role: Option<Role>,
	pub claims: Option<Claims>,
}

#[derive(Deserialize, Debug, Clone, Default)]
pub struct QueryParams {
	#[serde(default)]
	pub limit: Option<i64>,
	#[serde(default)]
	pub offset: Option<i64>,
	#[serde(default)]
	pub order: Option<String>,
	#[serde(default)]
	pub sort: Option<String>,
	#[serde(default)]
	pub field: Option<Vec<String>>,
	#[serde(default)]
	pub operator: Option<Vec<String>>,
	#[serde(default)]
	pub value: Option<Vec<String>>,
}

pub fn validate_query_params(params: QueryParams) -> bool {

	let fields = params.field.unwrap_or(vec![]);
	let operators = params.operator.unwrap_or(vec![]);
	let values = params.value.unwrap_or(vec![]);

	if fields.len() != operators.len() || fields.len() != values.len() {
		return false;
	}

	true

}

pub fn parse_query_params(params: QueryParams) -> String {

	let fields = params.field.unwrap_or(vec![]);
	let operators = params.operator.unwrap_or(vec![]);
	let values = params.value.unwrap_or(vec![]);

	if fields.len() == 0 {
		return String::from("true");
	}

	let mut query = String::new();

	for i in 0..fields.len() {
		query.push_str(&format!("{} {} '{}'", fields[i], operators[i], values[i]));
		if i < fields.len() - 1 {
			query.push_str(" AND ");
		}
	}

	query

}

pub async fn get_router(pool: Pool, mq_conn: Connection) -> Router {

	let initial_state = SharedData {
		pool: Arc::new(pool),
		mq_conn: Arc::new(mq_conn),
	};

	let initial_extension = SesionData {
		user: None,
		role: None,
		claims: None,
	};

	let shared_initial_extension = Arc::new(Mutex::new(initial_extension));

	let cors = CorsLayer::new()
		.allow_origin(Any)
		.allow_methods([Method::GET, Method::POST])
		.allow_headers(Any);

	let open_routes: Router<SharedData> = Router::new()
		.route("/health", get(|| async { "OK" }))
		.route("/login", post(login_controller))
		.route("/register", post(register_controller));

	let protected_routes: Router<SharedData> = Router::new()
		.route("/calendar", get(get_calendar_controller))
		.route("/calendar", post(post_calendar_controller))
		.route("/calendar", put(put_calendar_controller))
		.route("/calendar", delete(delete_calendar_controller))
		.route("/booking", get(get_booking_controller))
		.route("/booking", post(post_booking_controller))
		.route("/booking", put(put_booking_controller))
		.route("/booking", delete(delete_booking_controller))
		.route("/generate", post(generate_tfa_controller))
		.route("/verify", post(verify_tfa_controller))
		.route("/disable", post(disable_tfa_controller))
		.route_layer(middleware::from_fn_with_state(initial_state.clone(), validate_rol))
		.route_layer(middleware::from_fn(validate_token));

	Router::new()
		.nest("/api", open_routes)
		.nest("/api", protected_routes)
		.with_state(initial_state)
		.layer(Extension(shared_initial_extension))
		.layer(ServiceBuilder::new().layer(cors))

}