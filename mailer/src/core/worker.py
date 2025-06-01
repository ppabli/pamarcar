import multiprocessing as mp
import pika
import json
import time
import signal
from concurrent.futures import ThreadPoolExecutor
from threading import Lock
from jinja2 import Environment, FileSystemLoader, TemplateNotFound
from pydantic import ValidationError
from src.config.settings import settings
from src.util import EmailExtractor
from src.util.logger import setup_logger
from src.util.exceptions import TemplateNotFoundError
from src.model.email_models import BulkEmailResult, EmailMessage, EmailResult
from src.core.mailer import Mailer
from src.core.api import APIClient, TokenManager
from typing import List, Union

class MessageProcessor:

	def __init__(self, mailer: Mailer, template_env: Environment, logger, api_client: APIClient):

		self.mailer = mailer
		self.template_env = template_env
		self.logger = logger
		self._lock = Lock()
		self.api_client = api_client
		self.email_extractor = EmailExtractor()

	def process_message(self, message_data: dict, queue_name: str) -> dict:

		thread_id = mp.current_process().pid

		try:

			message = EmailMessage(**message_data)
			template_name = message.template_name or queue_name

			recipients = self._resolve_recipients(message)

			if not recipients:

				return {
					'success': False,
					'message': 'No recipients found',
					'requeue': False
				}

			try:

				template = self.template_env.get_template(f"{template_name}.html")
				html_content = template.render(message.context)

			except TemplateNotFound:

				raise TemplateNotFoundError(f"Template {template_name}.html not found")

			result = self._send_emails(recipients, message, html_content)

			with self._lock:

				return self._process_result(result, thread_id)

		except ValidationError as e:

			with self._lock:

				self.logger.error(f"[PID:{thread_id}] Invalid message format: {e}")

			return {'success': False, 'message': str(e), 'requeue': False}

		except Exception as e:

			with self._lock:

				self.logger.error(f"[PID:{thread_id}] Error processing message: {e}")

			return {'success': False, 'message': str(e), 'requeue': True}

	def _resolve_recipients(self, message: EmailMessage) -> List[str]:

		recipients = []

		if message.to:

			recipients.extend(message.to)

		if message.ids:

			try:

				api_recipients = self._fetch_emails_from_api(message.ids, message.endpoint)
				recipients.extend(api_recipients)

			except Exception as e:

				self.logger.error(f"Error fetching emails from API: {e}")

		unique_recipients = []
		seen = set()

		for email in recipients:

			if email not in seen:

				unique_recipients.append(email)
				seen.add(email)

		return unique_recipients

	def _fetch_emails_from_api(self, ids: List[Union[str, int]], endpoint: str) -> List[str]:

		try:

			all_emails = set()

			for id in ids:

				response = self.api_client.get(
					endpoint=f'{endpoint}/{id}',
				)

				if response.get('error'):

					raise

				objects = response.get('data')

				if objects:

					all_emails.update(self.email_extractor.find_emails(objects))

			self.logger.info(f"Fetched {len(all_emails)} emails from API for {len(ids)} user IDs")
			return all_emails

		except Exception as e:

			self.logger.error(f"Failed to fetch emails from API endpoint {endpoint}: {e}")
			raise

	def _send_emails(self, recipients: List[str], message: EmailMessage, html_content: str) -> Union[EmailResult, BulkEmailResult]:

		if len(recipients) == 1:

			return self.mailer.send_email(
				to_email=recipients[0],
				subject=message.subject,
				html_content=html_content
			)

		elif message.use_bcc and len(recipients) > 10:

			return self.mailer.send_email_bcc(
				recipients=recipients,
				subject=message.subject,
				html_content=html_content,
				batch_size=message.bcc_batch_size
			)

		else:

			return self.mailer.send_email(
				to_email=recipients,
				subject=message.subject,
				html_content=html_content
			)

	def _process_result(self, result: Union[EmailResult, BulkEmailResult], thread_id: int) -> dict:

		if hasattr(result, 'total_recipients'):

			success_rate = result.success_rate

			if result.successful_sends > 0:

				self.logger.info(
					f"[PID:{thread_id}] Bulk message processed: {result.successful_sends}/{result.total_recipients} "
					f"sent ({success_rate:.1f}%) using {result.method_used}"
				)

			if result.failed_sends > 0:

				self.logger.error(
					f"[PID:{thread_id}] Bulk message failures: {result.failed_sends}/{result.total_recipients} failed"
				)

			overall_success = success_rate >= 80.0

			return {
				'success': overall_success,
				'message': f"{result.successful_sends}/{result.total_recipients} emails sent successfully ({success_rate:.1f}%)",
				'total_recipients': result.total_recipients,
				'successful_sends': result.successful_sends,
				'method_used': result.method_used,
				'requeue': not overall_success
			}

		else:

			if result.success:

				self.logger.info(f"[PID:{thread_id}] Message processed for {result.recipient}")

			else:

				self.logger.error(f"[PID:{thread_id}] Failed to send email: {result.message}")

			return {
				'success': result.success,
				'message': result.message,
				'recipient': result.recipient,
				'requeue': not result.success
			}

class QueueWorkerProcess:

	def __init__(self, queue_name: str, max_workers: int = 5):

		self.queue_name = queue_name
		self.max_workers = max_workers
		self.logger = setup_logger(f"worker.{queue_name}")
		self._should_stop = False

		self.mailer = Mailer(
			smtp_host=settings.SMTP_HOST,
			smtp_port=settings.SMTP_PORT,
			smtp_user=settings.SMTP_USER,
			smtp_password=settings.SMTP_PASSWORD,
			use_tls=settings.SMTP_USE_TLS
		)

		self.token_manager = TokenManager(
			api_base_url=settings.API_BASE_URL,
			client_id=settings.API_CLIENT_ID,
			client_secret=settings.API_CLIENT_SECRET
		)

		self.api_client = APIClient(self.token_manager)

		self.template_env = Environment(loader=FileSystemLoader(settings.TEMPLATE_DIR))
		self.message_processor = MessageProcessor(self.mailer, self.template_env, self.logger, self.api_client)

		signal.signal(signal.SIGINT, self._signal_handler)
		signal.signal(signal.SIGTERM, self._signal_handler)

	def run(self):

		self.logger.info(f"Starting worker process for queue: {self.queue_name} (PID: {mp.current_process().pid})")

		while not self._should_stop:

			try:

				self._connect_and_consume()

			except Exception as e:

				if not self._should_stop:

					self.logger.error(f"Worker {self.queue_name}: {e}")
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
		channel.basic_qos(prefetch_count=self.max_workers * 2)

		with ThreadPoolExecutor(max_workers=self.max_workers, thread_name_prefix=f"msg-{self.queue_name}") as executor:

			active_futures = {}

			def callback(ch, method, properties, body):

				try:

					raw_message = json.loads(body)

					future = executor.submit(
						self.message_processor.process_message,
						raw_message,
						self.queue_name
					)

					active_futures[future] = {
						'channel': ch,
						'delivery_tag': method.delivery_tag,
						'message_id': raw_message.get('id', 'unknown')
					}

				except Exception as e:

					self.logger.error(f"Error in callback: {e}")
					ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

			channel.basic_consume(
				queue=self.queue_name,
				on_message_callback=callback,
				auto_ack=False
			)

			self.logger.info(f"Worker listening on queue: {self.queue_name} with {self.max_workers} threads")

			try:

				while not self._should_stop:

					connection.process_data_events(time_limit=1)

					completed_futures = [f for f in active_futures.keys() if f.done()]

					for future in completed_futures:

						future_info = active_futures.pop(future)

						try:

							result = future.result()

							if result['success']:

								future_info['channel'].basic_ack(
									delivery_tag=future_info['delivery_tag']
								)

							else:

								future_info['channel'].basic_nack(
									delivery_tag=future_info['delivery_tag'],
									requeue=result.get('requeue', True)
								)

						except Exception as e:

							self.logger.error(f"Error processing future result: {e}")

							future_info['channel'].basic_nack(
								delivery_tag=future_info['delivery_tag'],
								requeue=True
							)

					time.sleep(0.01)

			except KeyboardInterrupt:

				self.logger.info(f"Stopping worker {self.queue_name}")

			finally:

				self.logger.info(f"Waiting for {len(active_futures)} active messages to complete...")

				for future in active_futures:

					try:

						future.result(timeout=30)

					except Exception as e:

						self.logger.error(f"Error waiting for future: {e}")

				channel.stop_consuming()
				connection.close()
				self.logger.info(f"Worker {self.queue_name} stopped cleanly")

	def _signal_handler(self, signum, frame):

		self.logger.info(f"Worker {self.queue_name} received signal {signum}")
		self._should_stop = True
		self.token_manager.stop()

def worker_process_entry(queue_name: str, max_workers: int = 5):

	worker = QueueWorkerProcess(queue_name, max_workers)
	worker.run()