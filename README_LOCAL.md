# SkinAnalysis 本地分别启动说明

这个分支用于不依赖 Docker 的本地开发启动。整体仍然是同一套功能，只是把 Vue、Flask、Spring Boot、MySQL 分开运行。

## 端口

- 前端 Vue: `http://localhost:8088`
- Flask 模型服务: `http://localhost:5001`
- Spring Boot 数据服务: `http://localhost:8888`
- MySQL: `localhost:3308`

## 环境要求

- Node.js: 建议 18.x
- JDK: 17
- Maven: 可直接使用项目自带的 `springboot_app/mvnw.cmd`
- Python: 建议 3.8-3.10
- MySQL: 建议 8.x

## 日常启动顺序

每次重新开机或重新打开项目后，需要分别启动 4 个服务：

1. 项目专用 MySQL
2. Spring Boot 数据服务
3. Flask 模型服务
4. Vue 前端

建议打开 4 个 PowerShell 窗口，分别执行下面命令。

### 窗口 1: MySQL

```powershell
.\scripts\start-local-mysql.ps1
```

数据库数据保存在项目目录下的 `.local-run/mysql-data`。脚本只会在第一次没有表时导入 `mysql/init/init.sql`，后续注册和修改的数据会继续保留。

注意：`.local-run/` 已被 `.gitignore` 忽略，不会随代码上传。如果换电脑或重新 clone 项目，脚本会重新用 `mysql/init/init.sql` 初始化一份数据库，你本机后来注册的新账号不会自动带过去。

### 窗口 2: Spring Boot

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-22"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd springboot_app
.\mvnw.cmd spring-boot:run
```

### 窗口 3: Flask

```powershell
cd onnx_app
$env:FLASK_PORT="5001"
python app.py
```

第一次运行前如果缺 Python 依赖，先执行：

```powershell
pip install -r requirements.txt
```

### 窗口 4: Vue

```powershell
cd vue_app
npm run dev
```

第一次运行前如果没有 `node_modules`，先执行：

```powershell
npm install
```

启动完成后打开：

- 用户端：`http://localhost:8088`
- 管理员端：`http://localhost:8088/admin`

管理员默认账号：`admin`
管理员默认密码：`123456`

## 停止服务

前端、Flask、Spring Boot 可以在对应窗口按 `Ctrl + C` 停止。

项目专用 MySQL 可以执行：

```powershell
.\scripts\stop-local-mysql.ps1
```

## 1. 准备 MySQL

推荐使用项目专用 MySQL 实例，数据会放在 `.local-run/mysql-data`，不影响系统已有 MySQL：

```powershell
.\scripts\start-local-mysql.ps1
```

项目专用 MySQL 默认使用：

- 端口：`3308`
- 数据库：`SkinAnalysis`
- 用户名：`root`
- 密码：`root`

如果你想改用自己已经安装好的 MySQL，也可以手动创建并初始化数据库：

```powershell
mysql -uroot -p < mysql/init/init.sql
```

然后在启动 Spring Boot 前设置环境变量：

```powershell
$env:MYSQL_HOST="localhost"
$env:MYSQL_PORT="3306"
$env:MYSQL_DATABASE="SkinAnalysis"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="你的密码"
```

## 2. 启动 Spring Boot

```powershell
cd springboot_app
.\mvnw.cmd spring-boot:run
```

启动后数据接口在 `http://localhost:8888`。

## 3. 启动 Flask 模型服务

```powershell
cd onnx_app
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
$env:FLASK_PORT="5001"
python app.py
```

启动后模型接口在 `http://localhost:5001/detect`。

如果安装 `torchvision` 后仍提示缺少 `torch`，再按你当前 Python/CUDA 环境安装对应版本的 `torch`。

## 4. 启动前端

```powershell
cd vue_app
npm install
npm run dev
```

浏览器打开 `http://localhost:8088`。

前端开发代理已经指向本机：

- `/spring_api/*` -> `http://127.0.0.1:8888/*`
- `/flask_api/*` -> `http://127.0.0.1:5001/*`

## 5. 登录入口

- 用户端：`http://localhost:8088`
- 管理员端：`http://localhost:8088/admin`
- 管理员默认账号：`admin`
- 管理员默认密码：`123456`

## 6. AI 问答

当前前端问答组件使用的是智谱 AI，配置文件是 `vue_app/zhipu_api-key.js`。如果问答不可用，先检查这里的 API Key。
