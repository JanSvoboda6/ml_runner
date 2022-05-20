FROM python:3.8-slim-buster

WORKDIR /app

COPY requirements.txt requirements.txt

RUN pip3 install -r requirements.txt

RUN mkdir -p files
RUN mkdir -p runners_info

COPY ./server.py ./server.py
COPY ./models/ ./models/

CMD ["python3", "server.py"]
