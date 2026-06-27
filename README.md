# 皮肤智诊 SkinAnalysis

皮肤智诊（SkinAnalysis）是一套基于深度学习的皮肤病辅助分析系统。当前分支采用本地分离启动方式，不再依赖 Docker 一键编排，前端、模型服务、业务后端和数据库分别运行，便于开发、调试和演示。

模型训练数据集来源：ISIC2019。受数据集范围限制，当前仅支持部分皮肤疾病的辅助识别，结果仅供健康管理参考，不能替代医生面诊、皮肤镜检查或病理诊断。

## 核心功能

- 用户端图像诊断：上传皮肤图片，查看 Top 3 识别结果、概率、疾病简介和建议。
- 诊断报告：诊断历史支持详情查看、筛选和报告下载。
- 在线问答：结合最近一次诊断结果和疾病知识，提供皮肤健康问答。
- 疾病百科：展示模型覆盖的疾病知识，管理端可维护内容。
- 个人资料：支持用户信息维护、密码修改和安全问题。
- 管理后台：用户管理、诊断记录、数据统计、反馈审核、模型表现看板。
- 反馈闭环：用户可反馈识别结果是否准确，管理端可审核并统计模型表现。

## 系统架构

- 前端：Vue 3 + Vite + Element Plus + ECharts
- 模型服务：Flask + ONNX
- 数据服务：Spring Boot + Spring Data JPA
- 数据库：MySQL 8

服务端口：

| 服务 | 地址 |
| --- | --- |
| Vue 前端 | `http://localhost:8088` |
| Flask 模型服务 | `http://localhost:5001` |
| Spring Boot 数据服务 | `http://localhost:8888` |
| MySQL | `localhost:3308` |

前端开发代理：

- `/spring_api/*` -> `http://127.0.0.1:8888/*`
- `/flask_api/*` -> `http://127.0.0.1:5001/*`

## 目录说明

```text
SkinAnalysis
├── vue_app/          # Vue 前端
├── springboot_app/   # Spring Boot 数据服务
├── onnx_app/         # Flask 模型服务
├── mysql/init/       # 数据库初始化脚本
├── scripts/          # 本地启动/停止脚本
├── assets/           # README 功能截图
└── 测试图片/          # 可用于测试上传的皮肤图片
```

## 环境要求

- Node.js：建议 18.x
- JDK：17 或更高版本
- Python：建议 3.8 到 3.10
- MySQL：建议 8.x
- Maven：可直接使用项目自带的 `springboot_app/mvnw.cmd`

## 本地启动

建议打开 4 个 PowerShell 窗口，按顺序分别启动 MySQL、Spring Boot、Flask 和 Vue。

### 1. 启动项目专用 MySQL

```powershell
.\scripts\start-local-mysql.ps1
```

默认配置：

- 数据库：`SkinAnalysis`
- 地址：`127.0.0.1:3308`
- 用户名：`root`
- 密码：`root`

数据库数据保存在 `.local-run/mysql-data`，该目录已被 `.gitignore` 忽略。首次启动时会导入 `mysql/init/init.sql`，后续本机数据会继续保留。

停止 MySQL：

```powershell
.\scripts\stop-local-mysql.ps1
```

### 2. 启动 Spring Boot

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-22"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd springboot_app
.\mvnw.cmd spring-boot:run
```

如果你使用 JDK 17，把 `JAVA_HOME` 改成自己的 JDK 17 安装路径即可。

### 3. 启动 Flask 模型服务

```powershell
cd onnx_app
pip install -r requirements.txt
$env:FLASK_PORT="5001"
python app.py
```

如果希望使用虚拟环境：

```powershell
cd onnx_app
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
$env:FLASK_PORT="5001"
python app.py
```

### 4. 启动 Vue 前端

```powershell
cd vue_app
npm install
npm run dev
```

启动完成后访问：

- 用户端：`http://localhost:8088`
- 管理端：`http://localhost:8088/admin`

管理员默认账号：

- 用户名：`admin`
- 密码：`123456`

## AI 问答配置

当前在线问答使用智谱 AI，配置文件为：

```text
vue_app/zhipu_api-key.js
```

如果问答不可用，优先检查 API Key 是否填写正确。

## 常用验证命令

前端构建：

```powershell
cd vue_app
npm run build
```

后端测试：

```powershell
cd springboot_app
$env:JAVA_HOME="C:\Program Files\Java\jdk-22"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd test
```

## 界面预览

### 用户端

登录与注册入口：

![用户登录](assets/user-login.png)

图像诊断与模型分析：

![图像诊断](assets/user-diagnosis-upload.png)

诊断历史筛选：

![诊断历史](assets/user-history-empty.png)

个人资料维护：

![个人资料](assets/user-profile.png)

疾病百科：

![疾病百科](assets/user-disease-wiki.png)

移动端 AI 问答：

![AI 问答](assets/user-ai-chat-mobile.png)

### 管理端

用户管理：

![用户管理](assets/admin-user-management.png)

数据统计：

![数据统计](assets/admin-statistics-dashboard.png)

诊断记录管理：

![诊断记录](assets/admin-diagnosis-records.png)

反馈审核与模型表现：

![反馈审核](assets/admin-feedback-review.png)

## 更多本地说明

更详细的本地启动和故障排查说明见：

```text
README_LOCAL.md
```
