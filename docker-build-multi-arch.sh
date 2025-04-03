#!/bin/bash

# 执行打包脚本
echo "开始执行打包脚本..."
./package.sh

# 等待打包完成
echo "等待打包完成..."
sleep 2

# 创建并启动支持多平台的构建器
echo "创建多平台构建器..."
docker buildx create --use --name multi-arch-builder --driver docker-container

# 构建多架构 Docker 镜像
echo "开始构建多架构 Docker 镜像..."
docker buildx build \
  --platform linux/arm64,linux/amd64 \
  -t wuhaocn/message-router-ai:1.0.1 \
  --push \
  .

echo "Docker 多架构镜像构建完成！"
echo "镜像名称: wuhaocn/message-router-ai:1.0.1 (Docker Hub)" 