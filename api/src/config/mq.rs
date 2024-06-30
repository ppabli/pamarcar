use lapin::{
	options::*, types::FieldTable, Connection, ConnectionProperties, Result, BasicProperties
};

pub async fn start_mq() -> Result<Connection> {

	let addr = std::env::var("AMQP_ADDR").unwrap_or_else(|_| "amqp://127.0.0.1:5672/%2f".into());

	let conn = Connection::connect(&addr, ConnectionProperties::default()).await.expect("connection error");

	let internal_channel = conn.create_channel().await;

	match internal_channel {
		Ok(channel) => {
			let _ = channel.queue_declare("mail", QueueDeclareOptions::default(), FieldTable::default()).await.expect("queue_declare");
		},
		Err(e) => {
			println!("Error creating channel: {:?}", e);
		}
	}

	Ok(conn)

}

pub async fn send_message(conn: &Connection, qeue_name: &str, message: &str) {

	let channel = conn.create_channel().await.expect("create_channel");

	channel.basic_publish("", qeue_name, BasicPublishOptions::default(), message.as_bytes(), BasicProperties::default()).await.expect("basic_publish");

}