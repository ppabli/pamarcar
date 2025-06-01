import pika
import json
import threading
import time
from jinja2 import Environment, FileSystemLoader, TemplateNotFound
from pydantic import ValidationError
from src.config.settings import settings
from src.util.logger import setup_logger
from src.util.exceptions import QueueConnectionError, TemplateNotFoundError
from src.model.email_models import EmailMessage

class QueueConsumer(threading.Thread):

	def __init__(self, queue_name: str, mailer):

		super().__init__(daemon=True)

		self.queue_name = queue_name
		self.mailer = mailer
		self.logger = setup_logger(f"{__name__}.{queue_name}")
		self.env = Environment(loader=FileSystemLoader(settings.TEMPLATE_DIR))
		self._should_stop = False

	def run(self):

		while not self._should_stop:

			try:

				self._connect_and_consume()

			except Exception as e:

				self.logger.error(f"Consumer {self.queue_name}: {e}")

				if not self._should_stop:

					self.logger.info(f"Retrying connection in {settings.RETRY_DELAY} seconds...")
					time.sleep(settings.RETRY_DELAY)

	def _connect_and_consume(self):

		credentials = pika.PlainCredentials(settings.RABBITMQ_USER, settings.RABBITMQ_PASSWORD)

		parameters = pika.ConnectionParameters(
			host=settings.RABBITMQ_HOST,
			port=settings.RABBITMQ_PORT,
			virtual_host=settings.RABBITMQ_VHOST,
			credentials=credentials,
			heartbeat=600,
			blocked_connection_timeout=300
		)

		connection = pika.BlockingConnection(parameters)
		channel = connection.channel()

		channel.queue_declare(queue=self.queue_name, durable=True)
		channel.basic_qos(prefetch_count=1)
		channel.basic_consume(
			queue=self.queue_name,
			on_message_callback=self.callback,
			auto_ack=False
		)

		self.logger.info(f"Listening on queue: {self.queue_name}")

		try:

			channel.start_consuming()

		except KeyboardInterrupt:

			self.logger.info(f"Stopping consumer {self.queue_name}")
			channel.stop_consuming()
			connection.close()

	def callback(self, ch, method, properties, body):

		try:

			raw_message = json.loads(body)
			message = EmailMessage(**raw_message)

			template_name = message.template_name or self.queue_name

			try:

				template = self.env.get_template(f"{template_name}.html")
				html_content = template.render(message.context)

			except TemplateNotFound:

				raise TemplateNotFoundError(f"Template {template_name}.html not found")

			result = self.mailer.send_email(
				to_email=message.to,
				subject=message.subject,
				html_content=html_content
			)

			if result.success:

				ch.basic_ack(delivery_tag=method.delivery_tag)
				self.logger.info(f"Message processed for {message.to}")

			else:

				self.logger.error(f"Failed to send email: {result.message}")
				ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

		except ValidationError as e:

			self.logger.error(f"Invalid message format: {e}")
			ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

		except Exception as e:

			self.logger.error(f"Error processing message: {e}")
			ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

	def stop(self):

		self._should_stop = True
