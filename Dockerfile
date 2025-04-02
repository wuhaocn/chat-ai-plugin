FROM openjdk:11-jdk-slim

# 使用阿里云镜像源安装 nginx
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y nginx && \
    rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制后端文件
COPY release/backend .

# 复制前端文件
COPY release/frontend/dist /usr/share/nginx/html

# 设置环境变量
ENV APP_KEY=""
ENV APP_SECRET=""
ENV API_URL=""
ENV BACKEND_URL=""

# 创建启动脚本
RUN echo '#!/bin/bash\n\
\n\
# 创建 nginx 配置\n\
cat > /etc/nginx/nginx.conf << EOL\n\
user www-data;\n\
worker_processes  1;\n\
\n\
events {\n\
    worker_connections  1024;\n\
}\n\
\n\
http {\n\
    include       /etc/nginx/mime.types;\n\
    default_type  application/octet-stream;\n\
\n\
    sendfile        on;\n\
    keepalive_timeout  65;\n\
\n\
    error_log  /var/log/nginx/error.log;\n\
    access_log /var/log/nginx/access.log;\n\
\n\
    server {\n\
        listen       8081;\n\
        server_name  localhost;\n\
\n\
        location / {\n\
            root   /usr/share/nginx/html;\n\
            index  index.html;\n\
            try_files \$uri \$uri/ /index.html;\n\
        }\n\
\n\
        location /api/ {\n\
            proxy_pass ${BACKEND_URL}/api/;\n\
            proxy_set_header Host \$host;\n\
            proxy_set_header X-Real-IP \$remote_addr;\n\
            proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;\n\
            proxy_set_header X-Forwarded-Proto \$scheme;\n\
        }\n\
    }\n\
}\n\
EOL\n\
\n\
# 启动 nginx\n\
nginx\n\
\n\
# 启动后端服务\n\
cd /app && java -Dspring.profiles.active=dev -Dspring.config.location=file:conf/application.yml -cp "lib/*" com.message.router.MessageRouterApplication --app-key=${APP_KEY} --app-secret=${APP_SECRET} --api-url=${API_URL} --backend-url=${BACKEND_URL}' > /start.sh && \
chmod +x /start.sh

# 暴露端口
EXPOSE 8080 8081

# 启动命令
CMD ["/start.sh"] 