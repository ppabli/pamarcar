FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src
COPY templates/ ./templates
COPY .env .

WORKDIR /app
CMD ["python", "-m", "src.main"]
