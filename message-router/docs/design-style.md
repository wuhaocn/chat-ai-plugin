# 消息路由系统前端设计文档

## 1. 技术栈

### 1.1 核心框架
- Vue.js 3
- JavaScript ES6+
- HTML5
- CSS3

### 1.2 开发工具
- Vite
- ESLint
- Prettier
- Vue DevTools

## 2. 项目结构

```
message-router-ui/
├── src/
│   ├── api/              # API接口
│   ├── components/       # 组件
│   ├── views/           # 页面
│   ├── router/          # 路由
│   ├── store/           # 状态管理
│   ├── assets/          # 静态资源
│   └── utils/           # 工具函数
├── public/              # 公共资源
└── package.json         # 项目配置
```

## 3. 组件设计

### 3.1 聊天界面组件
```vue
<template>
  <div class="chat-container">
    <div class="message-list" ref="messageList">
      <div v-for="msg in messages" 
           :key="msg.id" 
           :class="['message', msg.type]">
        {{ msg.content }}
      </div>
    </div>
    <div class="input-area">
      <input v-model="inputMessage" 
             @keyup.enter="sendMessage"
             placeholder="输入消息...">
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>
```

### 3.2 样式设计
```css
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 20px;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.message {
  margin: 10px 0;
  padding: 10px;
  border-radius: 8px;
}

.message.user {
  background-color: #e3f2fd;
  margin-left: 20%;
}

.message.system {
  background-color: #f5f5f5;
  margin-right: 20%;
}

.input-area {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #eee;
}

input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 8px 16px;
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
}
```

## 4. 状态管理

### 4.1 消息状态
```javascript
const messageStore = {
  state: {
    messages: [],
    loading: false,
    error: null
  },
  mutations: {
    ADD_MESSAGE(state, message) {
      state.messages.push(message);
    },
    SET_LOADING(state, loading) {
      state.loading = loading;
    },
    SET_ERROR(state, error) {
      state.error = error;
    }
  }
};
```

### 4.2 API调用
```javascript
const api = {
  async sendMessage(message) {
    try {
      const response = await fetch('/api/messages/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams(message)
      });
      return await response.json();
    } catch (error) {
      console.warn('发送消息失败:', error);
      throw error;
    }
  }
};
```

## 5. 路由设计

### 5.1 路由配置
```javascript
const routes = [
  {
    path: '/chat/:id',
    name: 'Chat',
    component: () => import('../views/ChatView.vue')
  }
];
```

### 5.2 路由守卫
```javascript
router.beforeEach(async (to, from, next) => {
  if (to.name === 'Chat') {
    const id = to.params.id;
    if (!id) {
      next('/');
      return;
    }
    try {
      const isValid = await validateChatbotId(id);
      if (!isValid) {
        next('/');
        return;
      }
    } catch (error) {
      next('/');
      return;
    }
  }
  next();
});
```

## 6. 错误处理

### 6.1 全局错误处理
```javascript
app.config.errorHandler = (err, vm, info) => {
  console.warn('Vue错误:', err);
  console.warn('组件:', vm);
  console.warn('信息:', info);
};
```

### 6.2 API错误处理
```javascript
const handleApiError = (error) => {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        return '请求参数错误';
      case 401:
        return '未授权';
      case 403:
        return '禁止访问';
      case 404:
        return '资源不存在';
      default:
        return '服务器错误';
    }
  }
  return '网络错误';
};
```

## 7. 性能优化

### 7.1 代码分割
- 路由懒加载
- 组件异步加载
- 第三方库按需导入

### 7.2 缓存策略
- 消息缓存
- 路由缓存
- 组件缓存

## 8. 开发规范

### 8.1 代码风格
- 使用ESLint + Prettier
- 遵循Vue.js风格指南
- 使用TypeScript类型注解

### 8.2 命名规范
- 组件名：PascalCase
- 变量名：camelCase
- 常量名：UPPER_CASE
- 文件名：kebab-case 