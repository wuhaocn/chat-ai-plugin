# Message Router AI

基于融云IM的消息路由AI服务，支持消息智能路由和AI对话功能。

> 本项目代码由AI编写，如需修改建议使用[Cursor](https://cursor.sh/)进行编辑。

## 项目文档

- [设计文档](./message-router/docs/design.md)
- [API文档](./message-router/docs/api.md)
- [代码规范](./message-router/docs/CODING_STANDARDS.md)
- [设计风格](./message-router/docs/design-style.md)

## 功能特点

- 支持融云IM消息接收和发送
- 集成Dify AI对话能力
- 支持消息智能路由
- 支持多环境配置
- Docker容器化部署

## 环境要求

- JDK 11+
- Gradle 7.0+
- Docker 20.10+
- 融云IM账号
- Dify AI账号

### Docker配置

如果访问Docker Hub较慢，可以配置镜像加速器。在`/etc/docker/daemon.json`中添加以下配置：

```json
{
  "registry-mirrors": [
    "https://docker.1ms.run",
    "https://docker.xuanyuan.me",
    "https://docker.rainbond.cc",
    "https://do.nark.eu.org"
  ]
}
```

配置完成后需要重启Docker服务：

```bash
# Linux系统
sudo systemctl restart docker

# macOS系统
# 在Docker Desktop中重启Docker服务
```

## 配置说明

### 环境变量

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| RONGCLOUD_APP_KEY | 融云应用Key | ${RONGCLOUD_APP_KEY} |
| RONGCLOUD_APP_SECRET | 融云应用Secret | ${RONGCLOUD_APP_SECRET} |
| RONGCLOUD_API_URL | 融云API地址 | ${RONGCLOUD_API_URL} |
| BACKEND_URL | Message Router AI后端服务地址，用于内部服务间通信 | http://10.3.13.81:18080 |

### 配置文件

- `application.yml`: 主配置文件
- `application-dev.yml`: 开发环境配置
- `application-prod.yml`: 生产环境配置
- `application-qa.yml`: 测试环境配置

## 构建和部署

### 本地构建

```bash
# 构建项目
./gradlew clean build

# 运行测试
./gradlew test
```

### Docker构建

项目提供了`docker-build.sh`脚本用于构建Docker镜像：

```bash
# 执行构建脚本
./docker-build.sh
```

该脚本会：
1. 执行打包脚本
2. 等待打包完成
3. 构建Docker镜像
4. 输出镜像信息

### Docker运行

```bash
# 单机部署示例
docker run -t --name message-router-ai \
  -p 18080:8080 \
  -p 18081:8081 \
  -e RONGCLOUD_APP_KEY=${RONGCLOUD_APP_KEY} \
  -e RONGCLOUD_APP_SECRET=${RONGCLOUD_APP_SECRET} \
  -e RONGCLOUD_API_URL=${RONGCLOUD_API_URL} \
  -e BACKEND_URL=http://localhost:18080 \
  message-router-ai:latest

```

### Docker Hub镜像

```bash
# 拉取镜像
docker pull wuhaocn/message-router-ai:1.0.0

# 运行容器
docker run -t --name message-router-ai \
  -p 18080:8080 \
  -p 18081:8081 \
  -e RONGCLOUD_APP_KEY=${RONGCLOUD_APP_KEY} \
  -e RONGCLOUD_APP_SECRET=${RONGCLOUD_APP_SECRET} \
  -e RONGCLOUD_API_URL=${RONGCLOUD_API_URL} \
  -e BACKEND_URL=${BACKEND_URL} \
  wuhaocn/message-router-ai:1.0.0
```

## 接口说明

### 消息接口

#### 发送消息
- 路径: `/api/messages/send`
- 方法: POST
- 说明: 发送消息到融云IM
- 请求体: JSON格式的消息内容
- 示例:
```json
{
    "fromUserId": "user1",
    "toUserId": "user2",
    "objectName": "RC:TxtMsg",
    "content": "{\"content\":\"Hello\"}"
}
```

#### 发送消息(表单)
- 路径: `/api/messages/send`
- 方法: POST
- Content-Type: application/x-www-form-urlencoded
- 参数:
  - message: 消息对象
  - signTimestamp: 签名时间戳(可选)
  - nonce: 随机数(可选)
  - signature: 签名(可选)
  - appKey: 应用Key(可选)

#### 获取会话历史
- 路径: `/api/messages/history`
- 方法: GET
- 说明: 获取消息历史记录

### 聊天机器人接口

#### 获取聊天机器人
- 路径: `/api/chatbots/{id}`
- 方法: GET
- 说明: 获取指定ID的聊天机器人信息
- 参数: id - 聊天机器人ID

#### 获取所有聊天机器人
- 路径: `/api/chatbots`
- 方法: GET
- 说明: 获取所有聊天机器人信息

#### 创建聊天机器人
- 路径: `/api/chatbots`
- 方法: POST
- 说明: 创建新的聊天机器人
- 请求体: JSON格式的聊天机器人信息

#### 更新聊天机器人
- 路径: `/api/chatbots/{id}`
- 方法: PUT
- 说明: 更新指定ID的聊天机器人信息
- 参数: id - 聊天机器人ID
- 请求体: JSON格式的聊天机器人信息

#### 删除聊天机器人
- 路径: `/api/chatbots/{id}`
- 方法: DELETE
- 说明: 删除指定ID的聊天机器人
- 参数: id - 聊天机器人ID

## 开发指南

### 分支管理

- `master`: 主分支，用于生产环境
- `develop`: 开发分支，用于开发环境
- `feature/*`: 功能分支，用于新功能开发
- `hotfix/*`: 修复分支，用于紧急bug修复

### 代码规范

- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 使用SpringDoc OpenAPI生成API文档
- 使用SLF4J进行日志记录
- 使用Checkstyle进行代码规范检查
- 使用Spotless进行代码格式化

## 维护说明

### 日志查看

```bash
# 查看容器日志
docker logs -f message-router-ai

# 查看特定时间段的日志
docker logs --since 1h message-router-ai
```

### 容器管理

```bash
# 启动容器
docker start message-router-ai

# 停止容器
docker stop message-router-ai

# 重启容器
docker restart message-router-ai

# 删除容器
docker rm -f message-router-ai
```

## 常见问题

1. 消息接收失败
   - 检查融云配置是否正确
   - 检查网络连接是否正常
   - 检查日志中的错误信息

2. AI对话无响应
   - 检查Dify配置是否正确
   - 检查网络连接是否正常
   - 检查日志中的错误信息

3. 构建失败
   - 检查Gradle版本是否兼容
   - 检查依赖是否完整
   - 检查网络连接是否正常

4. Docker拉取镜像失败
   - 检查网络连接是否正常
   - 检查Docker镜像加速器配置是否正确
   - 尝试使用其他镜像加速器

## 联系方式

如有问题，请联系项目维护人员。 