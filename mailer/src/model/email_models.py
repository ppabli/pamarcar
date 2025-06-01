from pydantic import BaseModel, Field, field_validator, model_validator
from typing import Dict, Any, List, Union, Optional

class EmailMessage(BaseModel):

	to: Optional[Union[str, List[str]]] = Field(None, description="Email recipient(s) - direct emails")
	ids: Optional[List[Union[str, int]]] = Field(None, description="IDs to fetch emails from API")
	endpoint: Optional[str] = Field(None, description="API endpoint to fetch emails (when using ids)")

	subject: str = Field(..., description="Email subject")
	template_name: Optional[str] = Field(None, description="Template name to use")
	context: Dict[str, Any] = Field(default_factory=dict, description="Template context data")

	use_bcc: bool = Field(False, description="Use BCC for bulk sending (more efficient)")
	bcc_batch_size: int = Field(50, description="Batch size when using BCC")

	@model_validator(mode='after')
	def validate_recipients(self):

		if not self.to and not self.ids:

			raise ValueError("Must provide either 'to' (direct emails) or 'ids' (to fetch from API)")

		if self.ids and not self.endpoint:

			raise ValueError("'endpoint' is required when using 'ids'")

		return self

	@field_validator('to')
	@classmethod
	def validate_to(cls, v):

		if v is None:

			return None

		if isinstance(v, str):

			if ',' in v:

				return [email.strip() for email in v.split(',') if email.strip()]

			return [v.strip()]

		elif isinstance(v, list):

			return [email.strip() for email in v if isinstance(email, str) and email.strip()]

		else:

			raise ValueError("'to' must be a string or list of strings")

class EmailResult(BaseModel):

	success: bool
	message: str
	recipient: str
	attempts: int = 1

class BulkEmailResult(BaseModel):

	total_recipients: int
	successful_sends: int
	failed_sends: int
	results: List[EmailResult]
	method_used: str = "individual"

	@property
	def success_rate(self) -> float:

		return (self.successful_sends / self.total_recipients) * 100 if self.total_recipients > 0 else 0