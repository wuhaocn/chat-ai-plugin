<template>
  <el-container class="layout-container">
    <el-aside width="240px" class="aside-container">
      <div class="logo">
        <el-icon class="logo-icon"><Connection /></el-icon>
        <span>消息路由系统</span>
      </div>
      <el-menu
        router
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#ffffff"
        text-color="#606266"
        active-text-color="#409eff">
        <el-menu-item index="/">
          <el-icon><Monitor /></el-icon>
          <span>聊天机器人管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header-container">
        <div class="header-left">
          <el-icon class="toggle-sidebar"><Fold /></el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-center">
          <el-input
            v-model="searchQuery"
            placeholder="搜索..."
            class="search-input"
            :prefix-icon="Search">
            <template #append>
              <el-button :icon="Search">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span>管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item>修改密码</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-container">
        <router-view></router-view>
      </el-main>
      <el-footer class="footer-container">
        <div class="footer-content">
          <span>© 2024 消息路由系统</span>
          <span>Version 1.0.0</span>
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { Monitor, Fold, Connection, Search } from '@element-plus/icons-vue'

const searchQuery = ref('')
</script>

<style>
:root {
  --primary-color: #409eff;
  --text-primary: #303133;
  --text-regular: #606266;
  --text-secondary: #909399;
  --border-color: #dcdfe6;
  --background-color: #f5f7fa;
  --hover-color: #ecf5ff;
  --active-color: #409eff;
  --header-height: 60px;
  --aside-width: 240px;
  --footer-height: 40px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
}

.layout-container {
  height: 100vh;
  background-color: var(--background-color);
}

.aside-container {
  background-color: #ffffff;
  border-right: 1px solid var(--border-color);
  transition: width 0.3s;
}

.logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
  background-color: #ffffff;
  border-bottom: 1px solid var(--border-color);
}

.logo-icon {
  font-size: 24px;
  margin-right: 12px;
  color: var(--primary-color);
}

.header-container {
  background-color: #ffffff;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: var(--header-height);
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-center {
  flex: 1;
  max-width: 500px;
  margin: 0 20px;
}

.search-input {
  width: 100%;
}

.header-right {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
  color: var(--text-regular);
  transition: color 0.3s;
}

.toggle-sidebar:hover {
  color: var(--primary-color);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: var(--text-regular);
  transition: color 0.3s;
}

.user-info:hover {
  color: var(--primary-color);
}

.el-menu {
  border-right: none;
}

.el-menu-vertical {
  height: calc(100vh - var(--header-height) - var(--footer-height));
  overflow-y: auto;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
  border-radius: 4px;
  transition: all 0.3s;
}

.el-menu-item:hover {
  background-color: var(--hover-color) !important;
}

.el-menu-item.is-active {
  background-color: var(--hover-color) !important;
  color: var(--primary-color) !important;
}

.main-container {
  background-color: var(--background-color);
  padding: 20px;
  min-height: calc(100vh - var(--header-height) - var(--footer-height));
}

.footer-container {
  height: var(--footer-height);
  background-color: #ffffff;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-content {
  color: var(--text-secondary);
  font-size: 12px;
  display: flex;
  gap: 20px;
}

/* 自定义滚动条样式 */
.el-menu-vertical::-webkit-scrollbar {
  width: 6px;
}

.el-menu-vertical::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.el-menu-vertical::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .aside-container {
    width: 64px !important;
  }
  
  .logo span {
    display: none;
  }
  
  .header-center {
    display: none;
  }
}
</style> 