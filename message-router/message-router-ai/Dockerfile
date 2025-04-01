# 使用多阶段构建
# 第一阶段：构建应用
FROM gradle:7.6.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# 第二阶段：运行应用
FROM openjdk:17-slim
WORKDIR /app

# 复制构建产物
COPY --from=build /app/build/libs/*.jar app.jar

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"] 