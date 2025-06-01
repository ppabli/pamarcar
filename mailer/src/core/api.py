import httpx
import threading
from typing import Optional, Dict, Any
from dataclasses import dataclass
from datetime import datetime, timedelta
from threading import RLock
from src.util.logger import setup_logger
from src.config.settings import settings

@dataclass
class TokenInfo:

	access_token: str
	expires_at: datetime
	refresh_token: Optional[str] = None

	@property
	def is_expired(self) -> bool:

		return datetime.now() >= (self.expires_at - timedelta(minutes=1))

	@property
	def expires_in_seconds(self) -> int:

		return max(0, int((self.expires_at - datetime.now()).total_seconds()))


class TokenManager:

	def __init__(self, api_base_url: str, client_id: str, client_secret: str):

		self.api_base_url = api_base_url
		self.client_id = client_id
		self.client_secret = client_secret
		self.logger = setup_logger(f"{__name__}.TokenManager")

		self._token: Optional[TokenInfo] = None
		self._lock = RLock()
		self._refresh_thread: Optional[threading.Thread] = None
		self._stop_refresh = threading.Event()

	def get_token(self) -> str:

		with self._lock:

			if self._token is None or self._token.is_expired:

				self._refresh_token_sync()

			return self._token.access_token

	def _refresh_token_sync(self):

		try:

			with httpx.Client(timeout=30.0) as client:

				response = client.post(
					f"{self.api_base_url}/login",
					json={
						"email": self.client_id,
						"password": self.client_secret
					},
					headers={
						"Content-Type": "application/json",
						"User-Agent": "EmailService/1.0"
					}
				)

				response.raise_for_status()

				access_token = response.headers.get("Authentication")
				if not access_token:

					raise ValueError("No se encontró el token en el header 'Authentication'")

				expires_in = settings.API_TOKEN_DURATION

				try:

					token_data = response.json() if response.content else {}
					expires_in = token_data.get("expires_in", settings.API_TOKEN_DURATION)

				except Exception:

					self.logger.warning("No se pudo parsear el body de la respuesta, usando expires_in por defecto")

				expires_at = datetime.now() + timedelta(seconds=expires_in)

				refresh_token = None

				try:

					if response.content:

						token_data = response.json()
						refresh_token = token_data.get("refresh_token")

				except Exception:

					pass

				self._token = TokenInfo(
					access_token=access_token,
					expires_at=expires_at,
					refresh_token=refresh_token
				)

				self.logger.info(f"Token refreshed successfully. Expires at: {expires_at}")

				if self._refresh_thread is None or not self._refresh_thread.is_alive():

					self._start_auto_refresh()

		except Exception as e:

			self.logger.error(f"Error refreshing token: {e}")
			raise

	def _start_auto_refresh(self):

		self._stop_refresh.clear()
		self._refresh_thread = threading.Thread(
			target=self._auto_refresh_worker,
			daemon=True,
			name="token-refresh"
		)
		self._refresh_thread.start()
		self.logger.info("Auto-refresh thread started")

	def _auto_refresh_worker(self):

		while not self._stop_refresh.is_set():

			try:

				if self._token:

					sleep_time = max(60, self._token.expires_in_seconds - 300)

					if self._stop_refresh.wait(sleep_time):

						break

					with self._lock:

						if self._token and self._token.expires_in_seconds <= 300:

							self.logger.info("Auto-refreshing token...")
							self._refresh_token_sync()

				else:

					if self._stop_refresh.wait(60):

						break

			except Exception as e:

				self.logger.error(f"Error in auto-refresh worker: {e}")

				if self._stop_refresh.wait(60):

					break

	def stop(self):

		self._stop_refresh.set()

		if self._refresh_thread and self._refresh_thread.is_alive():

			self._refresh_thread.join(timeout=5)

		self.logger.info("Token manager stopped")


class APIClient:

	def __init__(self, token_manager: TokenManager):

		self.token_manager = token_manager
		self.logger = setup_logger(f"{__name__}.APIClient")

	def _get_headers(self) -> Dict[str, str]:

		token = self.token_manager.get_token()

		return {
			"Authorization": f"Bearer {token}",
			"Content-Type": "application/json",
			"User-Agent": "EmailService/1.0"
		}

	def post(self, endpoint: str, data: Dict[str, Any], timeout: int = 30) -> Dict[str, Any]:

		return self._request("POST", endpoint, json_data=data, timeout=timeout)

	def get(self, endpoint: str, params: Optional[Dict[str, Any]] = None, timeout: int = 30) -> Dict[str, Any]:

		return self._request("GET", endpoint, params=params, timeout=timeout)

	def put(self, endpoint: str, data: Dict[str, Any], timeout: int = 30) -> Dict[str, Any]:

		return self._request("PUT", endpoint, json_data=data, timeout=timeout)

	def _request(self, method: str, endpoint: str, json_data: Optional[Dict] = None, params: Optional[Dict] = None, timeout: int = 30, retry_count: int = 0) -> Dict[str, Any]:

		max_retries = settings.MAX_RETRIES
		url = f"{self.token_manager.api_base_url.rstrip('/')}/{endpoint.lstrip('/')}"

		try:

			headers = self._get_headers()

			with httpx.Client(timeout=timeout) as client:

				response = client.request(
					method=method,
					url=url,
					headers=headers,
					json=json_data,
					params=params
				)

				if response.status_code == 401 and retry_count < max_retries:

					self.logger.warning(f"Received 401, refreshing token and retrying...")

					with self.token_manager._lock:

						self.token_manager._refresh_token_sync()

					return self._request(method, endpoint, json_data, params, timeout, retry_count + 1)

				response.raise_for_status()

				self.logger.info(f"{method} {endpoint} -> {response.status_code}")

				return response.json() if response.content else {}

		except httpx.HTTPStatusError as e:

			self.logger.error(f"HTTP error {e.response.status_code} for {method} {endpoint}: {e.response.text}")
			raise

		except httpx.RequestError as e:

			self.logger.error(f"Request error for {method} {endpoint}: {e}")
			raise

		except Exception as e:

			self.logger.error(f"Unexpected error for {method} {endpoint}: {e}")
			raise