#!/bin/bash

# 执行打包脚本
echo "开始执行打包脚本..."
./package.sh

# 等待打包完成
echo "等待打包完成..."
sleep 2

# 构建 Docker 镜像
echo "开始构建 Docker 镜像..."
docker build -t message-router-ai:latest .

echo "Docker 镜像构建完成！"
echo "镜像名称: message-router-ai:latest" 