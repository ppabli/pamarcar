import re
from typing import List, Any, Set
from collections import deque

class EmailExtractor:

	def __init__(self):

		self.email_pattern = re.compile(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b')
		self.email_keys = {'email', 'e_mail', 'mail', 'correo', 'email_address'}

	def find_emails(self, data: Any) -> List[str]:

		emails = set()
		stack = deque([data])

		while stack:

			current = stack.popleft()

			if isinstance(current, dict):

				for key, value in current.items():

					if key.lower() in self.email_keys and isinstance(value, str):

						if self._is_valid_email(value):

							emails.add(value)

					else:

						if isinstance(value, (dict, list)):

							stack.append(value)

						elif isinstance(value, str):

							found_emails = self.email_pattern.findall(value)
							emails.update(found_emails)

			elif isinstance(current, list):

				for item in current:

					if isinstance(item, (dict, list)):

						stack.append(item)

					elif isinstance(item, str):

						found_emails = self.email_pattern.findall(item)
						emails.update(found_emails)

			elif isinstance(current, str):

				found_emails = self.email_pattern.findall(current)
				emails.update(found_emails)

		return list(emails)

	def _is_valid_email(self, email: str) -> bool:

		return '@' in email and '.' in email and len(email) > 5