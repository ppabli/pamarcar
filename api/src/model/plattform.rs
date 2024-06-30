use serde::Serialize;
use diesel::{prelude::*, Queryable};
use crate::schema::plattforms::{
	self,
	dsl::plattforms as platforms_table,
};

#[derive(Queryable, Identifiable, Selectable, Serialize)]
#[diesel(table_name = plattforms)]
pub struct Platform {
	pub id: i32,
	pub plattform_name: String,
	pub created_at: chrono::NaiveDateTime,
	pub updated_at: chrono::NaiveDateTime,
}

impl Platform {


}