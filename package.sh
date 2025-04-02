#!/bin/bash

# 设置服务名称
BACKEND_SERVICE="message-router-ai"
FRONTEND_SERVICE="message-router-ui"

# 清理之前的构建
echo "清理之前的构建..."
rm -rf release
rm -rf $BACKEND_SERVICE/build
rm -rf $FRONTEND_SERVICE/dist

# 构建后端项目
echo "开始构建后端项目..."
./gradlew clean build -x test
if [ $? -ne 0 ]; then
    echo "后端构建失败"
    exit 1
fi

# 创建发布目录
echo "创建发布目录..."
mkdir -p release/backend/conf
mkdir -p release/frontend

# 复制后端构建文件
echo "复制后端构建文件..."
cp -r message-router/message-router-ai/build/package/* release/backend/
cp message-router/message-router-ai/build/libs/message-router-ai-1.0.0.jar release/backend/lib/

# 复制配置文件
echo "复制配置文件..."
cp message-router/message-router-ai/src/main/resources/application.yml release/backend/conf/

# 创建后端启动脚本
echo "创建后端启动脚本..."
cat > release/backend/start.sh << 'EOF'
#!/bin/bash

# 检查参数
if [ "$#" -lt 3 ]; then
    echo "用法: $0 <app-key> <app-secret> <api-url>"
    exit 1
fi

# 设置参数
RONGCLOUD_APP_KEY=$1
RONGCLOUD_APP_SECRET=$2
RONGCLOUD_API_URL=$3

# 设置JVM参数
JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"

# 构建classpath
CLASSPATH="lib/*"

# 启动应用
java $JAVA_OPTS \
    -cp "$CLASSPATH" \
    -Dspring.profiles.active=dev \
    -Drongcloud.app-key=$RONGCLOUD_APP_KEY \
    -Drongcloud.app-secret=$RONGCLOUD_APP_SECRET \
    -Drongcloud.stream-message.api-url=$RONGCLOUD_API_URL \
    -Dspring.config.location=file:conf/application.yml \
    com.message.router.MessageRouterApplication
EOF

chmod +x release/backend/start.sh

# 构建前端项目
echo "开始构建前端项目..."
cd message-router/message-router-ui
npm install
VITE_API_BASE_URL='' npm run build
if [ $? -ne 0 ]; then
    echo "前端构建失败"
    exit 1
fi
cd ../..

# 复制前端构建文件
echo "复制前端构建文件..."
mkdir -p release/frontend/dist
cp -r message-router/message-router-ui/dist/* release/frontend/dist/

# 创建前端启动脚本
echo "创建前端启动脚本..."
cat > release/frontend/start.sh << 'EOF'
#!/bin/bash

# 检查参数
if [ $# -lt 1 ]; then
    echo "Usage: $0 <backend_url>"
    exit 1
fi

# 设置后端URL
BACKEND_URL=$1

# 创建 nginx 配置
cat > "$(dirname "$0")/nginx.conf" << EOL
worker_processes  1;

events {
    worker_connections  1024;
}

http {
    default_type  application/octet-stream;

    types {
        text/html                             html htm;
        text/css                              css;
        application/javascript                 js;
        image/jpeg                            jpg jpeg;
        image/png                             png;
        image/gif                             gif;
        image/svg+xml                         svg;
        application/font-woff                 woff;
        application/font-woff2                woff2;
        application/x-font-ttf                ttf;
    }

    sendfile        on;
    keepalive_timeout  65;

    error_log  "$(pwd)/error.log";
    access_log "$(pwd)/access.log";

    server {
        listen       8081;
        server_name  localhost;

        location / {
            root   "$(pwd)/dist";
            index  index.html;
            try_files \$uri \$uri/ /index.html;
        }

        location /api/ {
            proxy_pass ${BACKEND_URL}/api/;
            proxy_set_header Host \$host;
            proxy_set_header X-Real-IP \$remote_addr;
            proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto \$scheme;
        }
    }
}
EOL

# 启动服务
cd "$(dirname "$0")"
NGINX_CONF="$(pwd)/nginx.conf"
echo "Starting nginx with config: $NGINX_CONF"
nginx -c "$NGINX_CONF" -t
if [ $? -ne 0 ]; then
    echo "Nginx configuration test failed"
    exit 1
fi
nginx -c "$NGINX_CONF"
if [ $? -ne 0 ]; then
    echo "Failed to start nginx"
    exit 1
fi
echo "Nginx started successfully"
EOF

chmod +x release/frontend/start.sh

# 创建总启动脚本
echo "创建总启动脚本..."
cat > release/start.sh << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"

# 检查参数
if [ "$#" -lt 4 ]; then
    echo "用法: $0 <app-key> <app-secret> <api-url> <backend-url>"
    echo "例如: $0 test test http://localhost:8080 http://192.168.1.100:8080"
    exit 1
fi

# 设置参数
APP_KEY=$1
APP_SECRET=$2
API_URL=$3
BACKEND_URL=$4

# 杀掉之前的进程
echo "清理之前的进程..."
pkill -f "java.*MessageRouterApplication" || true
nginx -s stop || true

# 启动后端服务
echo "启动后端服务..."
cd backend
./start.sh "$APP_KEY" "$APP_SECRET" "$API_URL" &
BACKEND_PID=$!

# 等待后端服务启动
echo "等待后端服务启动..."
sleep 5

# 启动前端服务
echo "启动前端服务..."
cd ../frontend
./start.sh "$BACKEND_URL" &
FRONTEND_PID=$!

# 等待用户中断
echo "服务已启动，按 Ctrl+C 停止服务"
trap 'kill $BACKEND_PID; nginx -s stop' SIGINT SIGTERM
wait $BACKEND_PID $FRONTEND_PID
EOF

chmod +x release/start.sh

echo "打包完成！"
echo "发布文件位于 release 目录"
echo "使用方法："
echo "1. 进入 release 目录"
echo "2. 运行 ./start.sh <app-key> <app-secret> <api-url> <backend-url>" 