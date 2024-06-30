use serde::{Deserialize, Serialize};
use diesel::{prelude::*, Insertable, Queryable};
use totp_rs::{Algorithm, Secret, TOTP};
use base32;
use diesel::sql_types::Bool;
use rand::Rng;
use crate::schema::users::{
	self,
	dsl::users as user_table,
};
use crate::helper::api::DetailLevel;
use crate::config::route::{QueryParams, parse_query_params};

#[derive(Queryable, Identifiable, Selectable, Serialize, Deserialize, Clone, Default, Debug)]
#[diesel(table_name = users)]
pub struct User {
	pub id: i32,
	pub user_name: String,
	pub email: Option<String>,
	pub phone: Option<String>,
	pub password: String,
	pub role_id: i32,
	pub tfa_enabled: bool,
	pub tfa_verified: bool,
	pub tfa_secret: Option<String>,
	pub tfa_auth_url: Option<String>,
	pub created_at: chrono::NaiveDateTime,
	pub updated_at: chrono::NaiveDateTime,
}

#[derive(Deserialize, Serialize)]
pub struct RequiredRegisterUser {
	pub user_name: String,
	pub email: String,
	pub phone: String,
	pub password: String,
}

#[derive(Deserialize, Serialize)]
pub struct RequiredLoginUser {
	pub email: Option<String>,
	pub phone: Option<String>,
	pub password: String,
	pub tfa_code: Option<i32>,
}

#[derive(Deserialize, Serialize, Debug, Insertable, AsChangeset, Clone, Default)]
#[diesel(table_name = users)]
pub struct UserData {
	#[serde(skip_serializing_if = "Option::is_none")]
	pub id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub user_name: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub email: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub phone: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub password: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub role_id: Option<i32>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub tfa_enabled: Option<bool>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub tfa_verified: Option<bool>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub tfa_secret: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub tfa_auth_url: Option<String>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub created_at: Option<chrono::NaiveDateTime>,
	#[serde(skip_serializing_if = "Option::is_none")]
	pub updated_at: Option<chrono::NaiveDateTime>,
}

impl User {

	pub fn filter_user_data_input(user_data: Vec<UserData>, detail_level: DetailLevel) -> Vec<UserData> {

		match detail_level {
			DetailLevel::Full => user_data.iter().map(|user| UserData {
				id: user.id,
				user_name: user.user_name.clone(),
				email: user.email.clone(),
				phone: user.phone.clone(),
				password: user.password.clone(),
				role_id: user.role_id,
				tfa_enabled: user.tfa_enabled,
				tfa_verified: user.tfa_verified,
				tfa_secret: user.tfa_secret.clone(),
				tfa_auth_url: user.tfa_auth_url.clone(),
				created_at: user.created_at,
				updated_at: user.updated_at,
			}).collect(),
			DetailLevel::SemiFull => user_data.iter().map(|user| UserData {
				id: user.id,
				user_name: user.user_name.clone(),
				email: user.email.clone(),
				phone: user.phone.clone(),
				role_id: user.role_id,
				tfa_enabled: user.tfa_enabled,
				tfa_verified: user.tfa_verified,
				tfa_secret: user.tfa_secret.clone(),
				tfa_auth_url: user.tfa_auth_url.clone(),
				..Default::default()
			}).collect(),
			DetailLevel::Partial => user_data.iter().map(|user| UserData {
				id: user.id,
				user_name: user.user_name.clone(),
				email: user.email.clone(),
				phone: user.phone.clone(),
				role_id: user.role_id,
				tfa_enabled: user.tfa_enabled,
				tfa_verified: user.tfa_verified,
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => user_data.iter().map(|user| UserData {
				id: user.id,
				user_name: user.user_name.clone(),
				email: user.email.clone(),
				phone: user.phone.clone(),
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],
		}

	}

	pub fn filter_user_data_output(user_data: Vec<User>, detail_level: DetailLevel) -> Vec<UserData> {

		match detail_level {
			DetailLevel::Full => user_data.iter().map(|user| UserData {
				id: Some(user.id),
				user_name: Some(user.user_name.clone()),
				email: user.email.clone(),
				phone: user.phone.clone(),
				password: Some(user.password.clone()),
				role_id: Some(user.role_id),
				tfa_enabled: Some(user.tfa_enabled),
				tfa_verified: Some(user.tfa_verified),
				tfa_secret: user.tfa_secret.clone(),
				tfa_auth_url: user.tfa_auth_url.clone(),
				created_at: Some(user.created_at),
				updated_at: Some(user.updated_at),
			}).collect(),
			DetailLevel::SemiFull => user_data.iter().map(|user| UserData {
				id: Some(user.id),
				user_name: Some(user.user_name.clone()),
				email: user.email.clone(),
				phone: user.phone.clone(),
				password: Some(user.password.clone()),
				role_id: Some(user.role_id),
				tfa_enabled: Some(user.tfa_enabled),
				tfa_verified: Some(user.tfa_verified),
				tfa_secret: user.tfa_secret.clone(),
				tfa_auth_url: user.tfa_auth_url.clone(),
				..Default::default()
			}).collect(),
			DetailLevel::Partial => user_data.iter().map(|user| UserData {
				id: Some(user.id),
				user_name: Some(user.user_name.clone()),
				email: user.email.clone(),
				phone: user.phone.clone(),
				password: Some(user.password.clone()),
				role_id: Some(user.role_id),
				tfa_enabled: Some(user.tfa_enabled),
				tfa_verified: Some(user.tfa_verified),
				..Default::default()
			}).collect(),
			DetailLevel::Minimal => user_data.iter().map(|user| UserData {
				id: Some(user.id),
				user_name: Some(user.user_name.clone()),
				email: user.email.clone(),
				phone: user.phone.clone(),
				..Default::default()
			}).collect(),
			DetailLevel::Cero => vec![],
		}

	}

	pub fn get_user(conn: &mut PgConnection, params: QueryParams) -> Result<Vec<User>, diesel::result::Error> {

		let limit = params.limit.unwrap_or(10);
		let offset = params.offset.unwrap_or(0);

		let result = user_table
			.filter(diesel::dsl::sql::<Bool>(&parse_query_params(params)))
			.limit(limit as i64)
			.offset(offset as i64)
			.load(conn);

		match result {
			Ok(users) => Ok(users),
			Err(e) => Err(e),
		}

	}

	pub fn create_user(data: UserData, conn: &mut PgConnection) -> Result<User, diesel::result::Error> {

		let result = diesel::insert_into(user_table)
			.values(data)
			.get_result(conn);

		match result {
			Ok(user) => Ok(user),
			Err(e) => Err(e),
		}

	}

	pub fn generate_tfa(id: i32, email: String, conn: &mut PgConnection) -> Result<User, String> {

		let params = QueryParams {
			field: Some(vec![String::from("id")]),
			value: Some(vec![id.to_string()]),
			operator: Some(vec![String::from("=")]),
			..Default::default()
		};

		let user_data = User::get_user(conn, params);

		if user_data.is_err() {
			return Err("Invalid user".to_string());
		}

		let user = user_data.unwrap().pop().unwrap();

		if user.id != id {
			return Err("Invalid user".to_string());
		}

		if user.tfa_enabled {
			return Err("TFA already enabled".to_string());
		}

		let mut rng = rand::thread_rng();
		let data_byte: [u8; 21] = rng.gen();
		let base32_string = base32::encode(base32::Alphabet::Rfc4648 { padding: false }, &data_byte);

		let totp = TOTP::new(
			Algorithm::SHA1,
			6,
			1,
			30,
			Secret::Encoded(base32_string.clone()).to_bytes().unwrap()
		).unwrap();

		let otp_base32 = totp.get_secret_base32();
		let issuer = std::env::var("API_ISSUER").expect("API_ISSUER must be set");

		let otp_auth_url = format!("otpauth://totp/{issuer}:{email}?secret={otp_base32}&issuer={issuer}",
			issuer = issuer,
			email = email.clone(),
			otp_base32 = otp_base32
		);

		let update_user = diesel::update(user_table.find(user.id))
			.set((
				users::tfa_enabled.eq(true),
				users::tfa_secret.eq(base32_string.clone()),
				users::tfa_auth_url.eq(otp_auth_url)
			))
			.get_result::<User>(conn);

		match update_user {
			Ok(user) => Ok(user),
			Err(e) => Err(format!("Error updating user: {}", e)),
		}

	}

	pub fn verify_tfa(id: i32, token: i32, conn: &mut PgConnection) -> Result<User, String> {

		let params = QueryParams {
			field: Some(vec![String::from("id")]),
			value: Some(vec![id.to_string()]),
			operator: Some(vec![String::from("=")]),
			..Default::default()
		};

		let user_data = User::get_user(conn, params);

		if user_data.is_err() {
			return Err("Invalid user".to_string());
		}

		let user = user_data.unwrap().pop().unwrap();

		if !user.tfa_enabled {
			return Err("TFA not enabled".to_string());
		}

		let tfa_secret = user.tfa_secret.clone().unwrap();
		let totp = TOTP::new(
			Algorithm::SHA1,
			6,
			1,
			30,
			Secret::Encoded(tfa_secret).to_bytes().unwrap()
		).unwrap();

		let is_valid = totp.check_current(token.to_string().as_str()).unwrap();

		if !is_valid {
			return Err("Invalid token".to_string());
		}

		let update_user = diesel::update(user_table.find(user.id))
			.set(users::tfa_verified.eq(true))
			.get_result::<User>(conn);

		match update_user {
			Ok(user) => Ok(user),
			Err(e) => Err(format!("Error updating user: {}", e)),
		}

	}

	pub fn validate_tfa(id: i32, token: i32, conn: &mut PgConnection) -> Result<User, String> {

		let params = QueryParams {
			field: Some(vec![String::from("id")]),
			value: Some(vec![id.to_string()]),
			operator: Some(vec![String::from("=")]),
			..Default::default()
		};

		let user_data = User::get_user(conn, params);

		if user_data.is_err() {
			return Err("Invalid user".to_string());
		}

		let user = user_data.unwrap().pop().unwrap();

		if !user.tfa_enabled {
			return Err("TFA not enabled".to_string());
		}

		let totp = TOTP::new(
			Algorithm::SHA1,
			6,
			1,
			30,
			Secret::Encoded(user.tfa_secret.clone().unwrap()).to_bytes().unwrap()
		).unwrap();

		let is_valid = totp.check_current(token.to_string().as_str()).unwrap();

		if !is_valid {
			return Err("Invalid token".to_string());
		}

		Ok(user)

	}

	pub fn disable_tfa(id: i32, conn: &mut PgConnection) -> Result<User, String> {

		let params = QueryParams {
			field: Some(vec![String::from("id")]),
			value: Some(vec![id.to_string()]),
			operator: Some(vec![String::from("=")]),
			..Default::default()
		};

		let user_data = User::get_user(conn, params);

		if user_data.is_err() {
			return Err("Invalid user".to_string());
		}

		let user = user_data.unwrap().pop().unwrap();

		if !user.tfa_enabled {
			return Err("TFA not enabled".to_string());
		}

		let update_user = diesel::update(user_table.find(user.id))
			.set((
				users::tfa_enabled.eq(false),
				users::tfa_verified.eq(false),
				users::tfa_secret.eq(None::<String>),
				users::tfa_auth_url.eq(None::<String>)
			))
			.get_result::<User>(conn);

		match update_user {
			Ok(user) => Ok(user),
			Err(e) => Err(format!("Error updating user: {}", e)),
		}

	}

}