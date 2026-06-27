@echo off
cd /d "%~dp0..\vue_app"
call npm run dev -- --host 127.0.0.1 --port 8088
