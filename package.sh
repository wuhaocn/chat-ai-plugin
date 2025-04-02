#!/bin/bash

# 设置服务名称
SERVICE_NAME="message-router-ai"

# 清理之前的构建
echo "清理之前的构建..."
rm -rf message-router/message-router-ai/build/*

# 执行构建
echo "开始构建..."
./gradlew :message-router:message-router-ai:buildTar :message-router:message-router-ai:publish -DserviceName=$SERVICE_NAME

# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "构建成功！"
    
    # 创建发布目录
    RELEASE_DIR="release"
    rm -rf $RELEASE_DIR
    mkdir -p $RELEASE_DIR
    
    # 复制package目录下的所有内容到release目录
    echo "复制构建产物..."
    cp -r message-router/message-router-ai/build/package/* $RELEASE_DIR/
    
    # 创建启动脚本
    echo "创建启动脚本..."
    cat > $RELEASE_DIR/start.sh << 'EOF'
#!/bin/bash

# 获取脚本所在目录的绝对路径
BASE_PATH=$(cd "$(dirname "$0")"; pwd)

# 检查参数
if [ $# -lt 3 ]; then
    echo "Usage: $0 <app-key> <app-secret> <api-url>"
    echo "Example: $0 c9kqb3rdkbb8j uTNrkYskbNC http://api-aliqa.rongcloud.net"
    exit 1
fi

# 获取参数
RONGCLOUD_APP_KEY=$1
RONGCLOUD_APP_SECRET=$2
RONGCLOUD_API_URL=$3

# JVM参数配置
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$BASE_PATH/logs"

# 主类
MAIN_CLASS="com.message.router.MessageRouterApplication"

# 启动命令
java $JAVA_OPTS \
    -cp "$BASE_PATH/message-router-ai-1.0.0.jar:$BASE_PATH/lib/*" \
    -Drongcloud.app-key=$RONGCLOUD_APP_KEY \
    -Drongcloud.app-secret=$RONGCLOUD_APP_SECRET \
    -Drongcloud.stream-message.api-url=$RONGCLOUD_API_URL \
    $MAIN_CLASS \
    --spring.config.location=file:$BASE_PATH/conf/application.yml
EOF
    
    chmod +x $RELEASE_DIR/start.sh
    
    echo "打包完成！"
    echo "发布文件在 $RELEASE_DIR 目录下"
    echo "使用方法："
    echo "1. cd $RELEASE_DIR"
    echo "2. ./start.sh <app-key> <app-secret> <api-url>"
    echo "   例如: ./start.sh c9kqb3rdkbb8j uTNrkYskbNC http://api-aliqa.rongcloud.net"
else
    echo "构建失败！"
    exit 1
fi 