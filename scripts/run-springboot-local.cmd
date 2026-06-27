@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-22"
set "Path=%JAVA_HOME%\bin;%Path%"
cd /d "%~dp0..\springboot_app"
call mvnw.cmd spring-boot:run
