@echo off
set "FLASK_PORT=5001"
cd /d "%~dp0..\onnx_app"
python app.py
