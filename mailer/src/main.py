from src.core.service import MailService
from src.config.settings import settings
from src.util.logger import setup_logger

def main():

	logger = setup_logger(__name__)

	try:

		logger.info("Starting Mail Service...")
		service = MailService({
			"queue_names": settings.QUEUE_NAMES,
			"queue_threads": settings.QUEUE_THREADS,
		})
		service.start()

	except Exception as e:

		logger.error(f"Fatal error: {e}")
		raise

if __name__ == "__main__":

	main()