use jsonwebtoken::{decode, DecodingKey, Validation, Algorithm, Header, EncodingKey, encode};
use serde::{Serialize, Deserialize};
use std::env;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Claims {
	pub user_id: i32,
	pub signature: String,
	pub exp: i64,
}

pub fn create_jwt(user_id: i32) -> Result<String, String> {

	let expiration = chrono::Utc::now() + chrono::Duration::hours(1);

	let claims = Claims {
		user_id,
		signature: "signature".to_string(),
		exp: expiration.timestamp(),
	};

	let secret = env::var("JWT_SECRET").expect("JWT_SECRET must be set");

	match encode(&Header::default(), &claims, &EncodingKey::from_secret(secret.as_ref())) {
		Ok(token) => {
			Ok(token)
		}
		Err(_) => Err("Error creating JWT".to_string())
	}

}

pub fn decode_jwt(token: &str) -> Result<Claims, String> {

	let secret = env::var("JWT_SECRET").expect("JWT_SECRET must be set");

	let token_data = decode::<Claims>(&token, &DecodingKey::from_secret(secret.as_ref()), &Validation::new(Algorithm::HS256));

	match token_data {
		Ok(data) => Ok(data.claims),
		Err(_) => {
			Err("Error on JWT".to_string())
		}
	}

}