use std::env;
use lapin::Connection;
use argon2::{
	password_hash::{
		rand_core::OsRng,
		PasswordHash,
		PasswordHasher,
		PasswordVerifier,
		SaltString,
	},
	Argon2
};
use std::sync::Arc;
use crate::config::db::Pool;
use crate::helper::api::LoginResponse;
use crate::model::user::{User, UserData, RequiredLoginUser, RequiredRegisterUser};
use crate::helper::auth::create_jwt;
use crate::helper::api::DetailLevel;
use crate::config::mq::send_message;
use crate::config::route::{QueryParams};

pub fn login(data: RequiredLoginUser, pool: &Pool) -> Result<LoginResponse, String> {

	if data.email.is_none() && data.phone.is_none() {
		return Err("Email or phone required".to_string());
	}

	let mut params = QueryParams {
		..Default::default()
	};

	if let Some(email) = &data.email {
		params.field = Some(vec!["email".to_string()]);
		params.value = Some(vec![email.to_string()]);
		params.operator = Some(vec!["=".to_string()]);
	}

	if let Some(phone) = &data.phone {
		params.field = Some(vec!["phone".to_string()]);
		params.value = Some(vec![phone.to_string()]);
		params.operator = Some(vec!["=".to_string()]);
	}

	let user = User::get_user(&mut pool.get().unwrap(), params);

	if user.is_err() {
		return Err("Invalid email or phone".to_string());
	}

	let user = user.unwrap().pop().unwrap();

	if data.tfa_code.is_none() && user.tfa_enabled && user.tfa_verified {
		return Err("TFA required".to_string());
	}

	// Hashing password using argon is a bit more complex and slower than bcrypt but it is more secure
	let parsed_hash = PasswordHash::new(&user.password).unwrap();

	if Argon2::default().verify_password(data.password.as_bytes(), &parsed_hash).is_err() {
		return Err("Invalid credentials".to_string());
	}

	let token = create_jwt(user.id);

	match token {

		Ok(token) => Ok(LoginResponse {
			token,
			user: User::filter_user_data_output(vec![user.clone()], DetailLevel::Minimal).pop().unwrap(),
		}),
		Err(e) => Err(e),

	}

}

pub fn register(data: RequiredRegisterUser, pool: &Pool, mq_conn: Arc<Connection>) -> Result<UserData, String> {

	let salt = SaltString::generate(&mut OsRng);
	let argon2 = Argon2::default();
	let new_password = argon2.hash_password(data.password.as_bytes(), &salt).unwrap().to_string();

	let role_id = env::var("DEFAULT_ROLE_ID").unwrap().parse::<i32>().unwrap();

	let new_user = UserData {
		user_name: Some(data.user_name),
		email: Some(data.email),
		phone: Some(data.phone),
		password: Some(new_password),
		role_id: Some(role_id),
		..Default::default()
	};

	let result = User::create_user(new_user, &mut pool.get().unwrap());

	match result {

		Ok(user) => {
			let mq_conn_clone = Arc::clone(&mq_conn);
			tokio::spawn(async move {
				send_message(&mq_conn_clone, "mail", "Test").await;
			});
			Ok(User::filter_user_data_output(vec![user], DetailLevel::Minimal).pop().unwrap())
		},
		Err(e) => Err(e.to_string()),

	}

}

pub fn generate_tfa(id: i32, email: String, pool: &Pool) -> Result<String, String> {

	let result = User::generate_tfa(id, email, &mut pool.get().unwrap());

	match result {

		Ok(user) => Ok(user.tfa_auth_url.unwrap()),
		Err(e) => Err(e),

	}

}

pub fn verify_tfa(id: i32, token: i32, pool: &Pool) -> Result<UserData, String> {

	let result = User::verify_tfa(id, token, &mut pool.get().unwrap());

	match result {

		Ok(user) => Ok(
			User::filter_user_data_output(vec![user], DetailLevel::Minimal).pop().unwrap(),
		),
		Err(e) => Err(e),

	}

}

pub fn disable_tfa(id: i32, pool: &Pool) -> Result<String, String> {

	let result = User::disable_tfa(id, &mut pool.get().unwrap());

	match result {

		Ok(_) => Ok("TFA disabled".to_string()),
		Err(e) => Err(e.to_string()),

	}

}