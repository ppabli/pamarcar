import logging
import sys
from pathlib import Path
from src.config.settings import settings

def setup_logger(name: str) -> logging.Logger:

	logger = logging.getLogger(name)
	logger.setLevel(getattr(logging, settings.LOG_LEVEL))

	if logger.handlers:

		return logger

	log_dir = Path("logs")
	log_dir.mkdir(exist_ok=True)

	formatter = logging.Formatter(
		'%(asctime)s - %(name)s - %(levelname)s - %(message)s'
	)

	console_handler = logging.StreamHandler(sys.stdout)
	console_handler.setFormatter(formatter)
	logger.addHandler(console_handler)

	file_handler = logging.FileHandler(log_dir / "mail_service.log")
	file_handler.setFormatter(formatter)
	logger.addHandler(file_handler)

	return logger