mod controller;
mod model;
mod service;
mod schema;
mod config;
mod middleware;
mod helper;

use dotenv::dotenv;

#[tokio::main]
async fn main() {

	dotenv().ok();

	let database_url = std::env::var("DATABASE_URL").expect("DATABASE_URL must be set");
	let pool = config::db::create_db_pool(database_url);

	let mq_conn = config::mq::start_mq().await.unwrap();

	let app = config::route::get_router(pool, mq_conn).await;

	let listener = tokio::net::TcpListener::bind("0.0.0.0:8080").await.unwrap();
	axum::serve(listener, app).await.unwrap();

}