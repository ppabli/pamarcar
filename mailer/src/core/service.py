import multiprocessing as mp
import signal
import sys
import time
from typing import List, Dict
from src.core.worker import worker_process_entry
from src.util.logger import setup_logger
from src.config.settings import settings

class MailService:

	def __init__(self, configs: Dict[str, object]):

		self.queue_configs = {
			queue_name: max_workers
			for queue_name, max_workers in zip(configs.get("queue_names", []), configs.get("queue_threads", []))
		}

		self.logger = setup_logger(__name__)
		self.processes: List[mp.Process] = []

		self._should_stop = False
		self._cleanup_done = False

		signal.signal(signal.SIGINT, self._signal_handler)
		signal.signal(signal.SIGTERM, self._signal_handler)

	def start(self):

		try:

			settings.validate()

			for queue_name, max_workers in self.queue_configs.items():

				process = mp.Process(
					target=worker_process_entry,
					args=(queue_name, max_workers),
					name=f"worker-{queue_name}"
				)

				process.start()
				self.processes.append(process)

				self.logger.info(f"Started process {process.pid} for queue: {queue_name} with {max_workers} threads")

			self.logger.info(f"Mail service started with {len(self.processes)} processes")

			try:

				while not self._should_stop:

					dead_processes = [p for p in self.processes if not p.is_alive()]

					if dead_processes:

						for process in dead_processes:

							self.logger.warning(f"Process {process.name} (PID: {process.pid}) died")
							self._restart_process(process)

					time.sleep(30)

			except KeyboardInterrupt:

				self.logger.info("Received interrupt signal")

		except Exception as e:

			self.logger.error(f"Error starting service: {e}")
			sys.exit(1)

		finally:

			if not self._cleanup_done:

				self.stop()

	def stop(self):

		if self._cleanup_done:

			return

		self.logger.info("Stopping mail service...")
		self._should_stop = True
		self._cleanup_done = True

		for process in self.processes:

			if process.is_alive():

				self.logger.info(f"Terminating process {process.name} (PID: {process.pid})")
				process.terminate()

		for process in self.processes:

			process.join(timeout=30)

			if process.is_alive():

				self.logger.warning(f"Force killing process {process.name}")
				process.kill()
				process.join()

		self.logger.info("Mail service stopped successfully")

	def _signal_handler(self, signum, frame):

		self.logger.info(f"Received signal {signum}")
		self._should_stop = True

	def _restart_process(self, dead_process):

		queue_name = None
		max_workers = None

		for qname, workers in self.queue_configs.items():

			if dead_process.name == f"worker-{qname}":

				queue_name = qname
				max_workers = workers

				break

		if queue_name is None:

			self.logger.error(f"Could not find config for process {dead_process.name}")

			return

		try:

			self.processes.remove(dead_process)

			new_process = mp.Process(
				target=worker_process_entry,
				args=(queue_name, max_workers),
				name=f"worker-{queue_name}"
			)

			new_process.start()
			self.processes.append(new_process)

			self.logger.info(f"Restarted process {new_process.pid} for queue: {queue_name}")

		except Exception as e:

			self.logger.error(f"Failed to restart process for queue {queue_name}: {e}")

	def health_check(self) -> dict:

		alive_processes = sum(1 for p in self.processes if p.is_alive())

		process_status = []

		for process in self.processes:

			process_status.append({
				'name': process.name,
				'pid': process.pid,
				'alive': process.is_alive(),
				'exitcode': process.exitcode
			})

		return {
			"status": "healthy" if alive_processes == len(self.processes) else "degraded",
			"total_processes": len(self.processes),
			"alive_processes": alive_processes,
			"processes": process_status
		}