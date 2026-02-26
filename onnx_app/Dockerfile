FROM python:3.8-slim

WORKDIR /app

COPY . /app

RUN pip install --no-cache-dir -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt

EXPOSE 5000

CMD ["python", "app.py"]