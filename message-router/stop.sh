#!/bin/bash

# 设置变量
CONTAINER_NAME="message-router-ai"

# 检查容器是否运行
if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    echo "正在停止容器..."
    docker stop $CONTAINER_NAME
    echo "容器已停止"
else
    echo "容器未运行"
fi

# 检查容器是否存在
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "正在删除容器..."
    docker rm $CONTAINER_NAME
    echo "容器已删除"
else
    echo "容器不存在"
fi 