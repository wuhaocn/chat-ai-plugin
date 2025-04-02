#!/bin/bash

# 检查参数
if [ "$#" -lt 4 ]; then
    echo "用法: $0 <app-key> <app-secret> <api-url> <backend-url>"
    echo "例如: $0 1234567890 1234567890 http://10.3.13.81:18080 http://10.3.13.81:18080"
    exit 1
fi

# 设置参数
APP_KEY=$1
APP_SECRET=$2
API_URL=$3
BACKEND_URL=$4

# 删除已存在的容器
echo "清理已存在的容器..."
docker rm -f message-router-ai || true

# 启动新容器
echo "启动新容器..."
docker run -t \
  --name message-router-ai \
  -p 18080:8080 \
  -p 18081:8081 \
  -e APP_KEY=$APP_KEY \
  -e APP_SECRET=$APP_SECRET \
  -e API_URL=$API_URL \
  -e BACKEND_URL=$BACKEND_URL \
  message-router-ai:latest 