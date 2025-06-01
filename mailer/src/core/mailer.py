import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from typing import Optional, List, Union
from src.util.logger import setup_logger
from src.util.exceptions import SMTPConnectionError
from src.model.email_models import EmailResult, BulkEmailResult
from src.config.settings import settings

class Mailer:

	def __init__(self, smtp_host: str, smtp_port: int, smtp_user: str, smtp_password: str, use_tls: bool = True):

		self.smtp_host = smtp_host
		self.smtp_port = smtp_port
		self.smtp_user = smtp_user
		self.smtp_password = smtp_password
		self.use_tls = use_tls
		self.logger = setup_logger(__name__)

	def send_email(self, to_email: Union[str, List[str]], subject: str, html_content: str, from_email: Optional[str] = None) -> Union[EmailResult, BulkEmailResult]:

		if isinstance(to_email, str):

			recipients = [to_email.strip()]

		else:

			recipients = [email.strip() for email in to_email if email.strip()]

		if not recipients:

			return EmailResult(
				success=False,
				message="No valid recipients provided",
				recipient="",
				attempts=0
			)

		if len(recipients) == 1:

			return self._send_single_email(recipients[0], subject, html_content, from_email)

		else:

			return self._send_bulk_individual(recipients, subject, html_content, from_email)

	def send_email_bcc(self, recipients: List[str], subject: str, html_content: str, from_email: Optional[str] = None, batch_size: int = 50) -> BulkEmailResult:

		if not recipients:

			return BulkEmailResult(
				total_recipients=0,
				successful_sends=0,
				failed_sends=0,
				results=[],
				method_used="bcc"
			)

		results = []
		successful_batches = 0
		total_batches = 0

		self.logger.info(f"Sending BCC bulk email to {len(recipients)} recipients in batches of {batch_size}")

		for i in range(0, len(recipients), batch_size):

			batch = recipients[i:i + batch_size]
			total_batches += 1

			batch_result = self._send_bcc_batch(batch, subject, html_content, from_email, total_batches)
			results.extend(batch_result)

			if all(r.success for r in batch_result):

				successful_batches += 1

		successful_sends = sum(1 for r in results if r.success)

		bulk_result = BulkEmailResult(
			total_recipients=len(recipients),
			successful_sends=successful_sends,
			failed_sends=len(recipients) - successful_sends,
			results=results,
			method_used="bcc"
		)

		self.logger.info(f"BCC bulk email completed: {successful_batches}/{total_batches} batches successful, {successful_sends}/{len(recipients)} emails sent ({bulk_result.success_rate:.1f}%)")

		return bulk_result

	def _send_single_email(self, to_email: str, subject: str, html_content: str, from_email: Optional[str] = None) -> EmailResult:

		attempts = 0
		max_attempts = settings.MAX_RETRIES

		while attempts < max_attempts:

			attempts += 1

			try:

				msg = MIMEMultipart("alternative")
				msg["From"] = from_email or self.smtp_user
				msg["To"] = to_email
				msg["Subject"] = subject
				msg.attach(MIMEText(html_content, "html"))

				with smtplib.SMTP(self.smtp_host, self.smtp_port) as server:

					if self.use_tls:

						server.starttls()

					server.login(self.smtp_user, self.smtp_password)
					server.sendmail(self.smtp_user, to_email, msg.as_string())

				self.logger.info(f"Email sent successfully to {to_email}")

				return EmailResult(
					success=True,
					message="Email sent successfully",
					recipient=to_email,
					attempts=attempts
				)

			except Exception as e:

				self.logger.error(f"Error sending email to {to_email} (attempt {attempts}): {e}")

				if attempts >= max_attempts:

					return EmailResult(
						success=False,
						message=f"Error después de {attempts} intentos: {str(e)}",
						recipient=to_email,
						attempts=attempts
					)

	def _send_bulk_individual(self, recipients: List[str], subject: str, html_content: str, from_email: Optional[str] = None) -> BulkEmailResult:

		results = []
		successful_sends = 0

		self.logger.info(f"Sending individual bulk email to {len(recipients)} recipients")

		for recipient in recipients:

			result = self._send_single_email(recipient, subject, html_content, from_email)
			results.append(result)

			if result.success:

				successful_sends += 1

		bulk_result = BulkEmailResult(
			total_recipients=len(recipients),
			successful_sends=successful_sends,
			failed_sends=len(recipients) - successful_sends,
			results=results,
			method_used="individual"
		)

		self.logger.info(f"Individual bulk email completed: {successful_sends}/{len(recipients)} sent successfully ({bulk_result.success_rate:.1f}%)")

		return bulk_result

	def _send_bcc_batch(self, batch: List[str], subject: str, html_content: str, from_email: Optional[str] = None, batch_number: int = 1) -> List[EmailResult]:

		attempts = 0
		max_attempts = settings.MAX_RETRIES

		while attempts < max_attempts:

			attempts += 1

			try:

				msg = MIMEMultipart("alternative")
				msg["From"] = from_email or self.smtp_user
				msg["To"] = from_email or self.smtp_user  # El "To" visible
				msg["Subject"] = subject
				msg.attach(MIMEText(html_content, "html"))

				with smtplib.SMTP(self.smtp_host, self.smtp_port) as server:

					if self.use_tls:

						server.starttls()

					server.login(self.smtp_user, self.smtp_password)
					server.sendmail(self.smtp_user, batch, msg.as_string())

				results = []

				for recipient in batch:

					results.append(EmailResult(
						success=True,
						message=f"Email sent successfully via BCC (batch {batch_number})",
						recipient=recipient,
						attempts=attempts
					))

				self.logger.info(f"BCC batch {batch_number} sent successfully to {len(batch)} recipients")
				return results

			except Exception as e:

				self.logger.error(f"Error sending BCC batch {batch_number} (attempt {attempts}): {e}")

				if attempts >= max_attempts:

					results = []

					for recipient in batch:

						results.append(EmailResult(
							success=False,
							message=f"Error en batch BCC después de {attempts} intentos: {str(e)}",
							recipient=recipient,
							attempts=attempts
						))

					return results

	def test_connection(self) -> bool:

		try:

			with smtplib.SMTP(self.smtp_host, self.smtp_port) as server:

				if self.use_tls:

					server.starttls()

				server.login(self.smtp_user, self.smtp_password)

			self.logger.info("SMTP connection successful")
			return True

		except Exception as e:

			self.logger.error(f"SMTP connection error: {e}")
			raise SMTPConnectionError(f"Could not connect to SMTP server: {e}")