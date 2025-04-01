#!/bin/bash

# 设置变量
IMAGE_NAME="message-router-ai"
IMAGE_TAG="1.0.0"
CONTAINER_NAME="message-router-ai"
PORT="8080"
H2_DATA_DIR="./data"

# 创建数据目录
mkdir -p $H2_DATA_DIR

# 构建镜像
echo "开始构建Docker镜像..."
docker build -t $IMAGE_NAME:$IMAGE_TAG -f message-router-ai/Dockerfile message-router-ai/

# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "Docker镜像构建成功: $IMAGE_NAME:$IMAGE_TAG"
else
    echo "Docker镜像构建失败"
    exit 1
fi 