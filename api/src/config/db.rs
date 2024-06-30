use diesel::pg::PgConnection;
use diesel::r2d2::{self, ConnectionManager};

pub type Connection = PgConnection;
pub type Pool = r2d2::Pool<ConnectionManager<Connection>>;

pub fn create_db_pool(database_url: String) -> Pool {

	let manager: ConnectionManager<PgConnection> = ConnectionManager::<Connection>::new(database_url);
	Pool::builder().build(manager).expect("Failed to create pool.")

}