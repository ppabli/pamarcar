FROM python:3.11-slim

LABEL maintainer="Pablo Liste Cancela <ppabli12@gmail.com>"

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src
COPY templates/ ./templates
COPY .env .

WORKDIR /app
CMD ["python", "-m", "src.main"]
