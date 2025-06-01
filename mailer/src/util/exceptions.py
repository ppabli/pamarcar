class MailServiceError(Exception):

	pass

class SMTPConnectionError(MailServiceError):

	pass

class TemplateNotFoundError(MailServiceError):

	pass

class QueueConnectionError(MailServiceError):

	pass

class InvalidEmailFormatError(MailServiceError):

	pass