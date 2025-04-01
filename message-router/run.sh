#!/bin/bash

# 设置变量
IMAGE_NAME="message-router-ai"
IMAGE_TAG="1.0.0"
CONTAINER_NAME="message-router-ai"
PORT="8080"
H2_DATA_DIR="./data"

# 检查容器是否已存在
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
    echo "容器已存在，正在停止并删除..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
fi

# 运行容器
echo "启动容器..."
docker run -d \
    --name $CONTAINER_NAME \
    -p $PORT:8080 \
    -v $(pwd)/$H2_DATA_DIR:/data \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e SPRING_DATASOURCE_URL=jdbc:h2:file:/data/message_router \
    -e SPRING_DATASOURCE_USERNAME=sa \
    -e SPRING_DATASOURCE_PASSWORD=password \
    -e SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect \
    -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    -e SPRING_JPA_SHOW_SQL=true \
    -e SPRING_H2_CONSOLE_ENABLED=true \
    -e SPRING_H2_CONSOLE_PATH=/h2-console \
    $IMAGE_NAME:$IMAGE_TAG

# 检查容器是否成功启动
if [ $? -eq 0 ]; then
    echo "容器启动成功"
    echo "容器ID: $(docker ps -q -f name=$CONTAINER_NAME)"
    echo "访问地址: http://localhost:$PORT"
    echo "H2控制台: http://localhost:$PORT/h2-console"
else
    echo "容器启动失败"
    exit 1
fi 