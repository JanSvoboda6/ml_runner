# syntax=docker/dockerfile:1.3

FROM python:3.8-slim-buster

WORKDIR /app

COPY requirements.txt requirements.txt

RUN pip3 install -r requirements.txt

COPY ./files ./files
COPY ./server.py ./server.py
COPY ./svm.py ./models/svm.py

CMD ["python3", "server.py"]
