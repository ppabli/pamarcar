"""
Mail Service - A simple email service
"""

__version__ = "0.1"
__author__ = "Pablo Liste Cancela"
__email__ = "ppbli12@gmail.com"

from src.core.service import MailService
from src.config.settings import settings

__all__ = ["MailService", "settings"]
