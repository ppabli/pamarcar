import os
from typing import List
from dotenv import load_dotenv

load_dotenv()

class Settings:

	RABBITMQ_HOST: str = os.getenv("RABBITMQ_HOST", "localhost")
	RABBITMQ_PORT: int = int(os.getenv("RABBITMQ_PORT", "5672"))
	RABBITMQ_USER: str = os.getenv("RABBITMQ_USER", "guest")
	RABBITMQ_PASSWORD: str = os.getenv("RABBITMQ_PASSWORD", "guest")
	RABBITMQ_VHOST: str = os.getenv("RABBITMQ_VHOST", "/")

	API_HOST: str = os.getenv("API_HOST", "localhost")
	API_PORT: int = int(os.getenv("API_PORT", "8080"))
	API_TIMEOUT: int = int(os.getenv("API_TIMEOUT", "10"))
	API_CLIENT_ID: str = os.getenv("API_CLIENT_ID")
	API_CLIENT_SECRET: str = os.getenv("API_CLIENT_SECRET")
	API_TOKEN_DURATION: int = int(os.getenv("API_TOKEN_DURATION", "1800"))

	API_BASE_URL: str = f"http://{API_HOST}:{API_PORT}"

	SMTP_HOST: str = os.getenv("SMTP_HOST")
	SMTP_PORT: int = int(os.getenv("SMTP_PORT", "587"))
	SMTP_USER: str = os.getenv("SMTP_USER")
	SMTP_PASSWORD: str = os.getenv("SMTP_PASSWORD")
	SMTP_USE_TLS: bool = os.getenv("SMTP_USE_TLS", "true").lower() == "true"

	QUEUE_NAMES: List[str] = [q.strip() for q in os.getenv("QUEUES", "").split(",") if q.strip()]
	QUEUE_THREADS: List[int] = [int(q.strip()) for q in os.getenv("QUEUES_THREADS", "").split(",") if q.strip()]

	LOG_LEVEL: str = os.getenv("LOG_LEVEL", "INFO")
	TEMPLATE_DIR: str = os.getenv("TEMPLATE_DIR", "templates")
	MAX_RETRIES: int = int(os.getenv("MAX_RETRIES", "3"))
	RETRY_DELAY: int = int(os.getenv("RETRY_DELAY", "5"))

	def validate(self) -> None:

		required_vars = [
			("SMTP_HOST", self.SMTP_HOST),
			("SMTP_USER", self.SMTP_USER),
			("SMTP_PASSWORD", self.SMTP_PASSWORD),
			("RABBITMQ_HOST", self.RABBITMQ_HOST),
		]

		missing = [var for var, value in required_vars if not value]

		if missing:

			raise ValueError(f"Missing environment variables: {', '.join(missing)}")

		if not self.QUEUE_NAMES:

			raise ValueError("RABBITMQ_QUEUES cannot be empty")

		if len(self.QUEUE_NAMES) != len(self.QUEUE_THREADS):

			raise ValueError("QUEUES and QUEUES_THREADS must have the same length")

settings = Settings()