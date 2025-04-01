# 项目编码规范

## 0. 默认规则

### 0.1 基本原则
- 禁止使用Lombok，所有实体类必须手动编写getter/setter方法
- 前后端代码风格保持一致，包括命名规范、注释风格等
- 所有代码改动必须编写CHANGELOG.md，记录改动内容、原因和影响
- 遵循"最小知识原则"，每个类只做一件事
- 遵循"开闭原则"，对扩展开放，对修改关闭
- 遵循"单一职责原则"，每个方法只做一件事
- 遵循"依赖倒置原则"，面向接口编程

### 0.2 代码审查
- 所有代码必须经过审查才能合并
- 审查重点：代码规范、安全性、性能、可维护性
- 审查清单：
  - 是否遵循编码规范
  - 是否有安全隐患
  - 是否有性能问题
  - 是否易于维护
  - 是否有完整的测试
  - 是否更新了CHANGELOG.md

### 0.3 版本控制
- 使用语义化版本号（Semantic Versioning）
- 主版本号：不兼容的API修改
- 次版本号：向下兼容的功能性新增
- 修订号：向下兼容的问题修正

### 0.4 文档维护
- 所有API必须有文档
- 所有配置项必须有说明
- 所有重要决策必须有记录
- 所有已知问题必须有记录

## 1. 项目结构

### 1.1 目录结构
```
message-router/
├── message-router-ai/          # 后端服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/message/router/
│   │   │   │       ├── config/     # 配置类
│   │   │   │       ├── controller/ # 控制器
│   │   │   │       ├── entity/     # 实体类
│   │   │   │       ├── model/      # 数据模型
│   │   │   │       ├── service/    # 服务层
│   │   │   │       └── util/       # 工具类
│   │   │   └── resources/
│   │   └── test/
│   └── build.gradle
└── message-router-ui/          # 前端服务
    ├── src/
    │   ├── api/               # API接口
    │   ├── assets/           # 静态资源
    │   ├── components/       # 组件
    │   ├── router/          # 路由配置
    │   ├── utils/           # 工具函数
    │   └── views/           # 页面视图
    └── package.json
```

### 1.2 命名规范

#### 1.2.1 后端命名规范
- 类名：使用大驼峰命名法（PascalCase），如`ChatbotController`
- 方法名：使用小驼峰命名法（camelCase），如`getChatbotById`
- 变量名：使用小驼峰命名法（camelCase），如`chatbotId`
- 常量名：使用全大写下划线分隔（UPPER_SNAKE_CASE），如`MAX_RETRY_COUNT`
- 包名：全小写，如`com.message.router`
- 文件名：与类名一致，如`ChatbotController.java`

#### 1.2.2 前端命名规范
- 组件名：使用大驼峰命名法（PascalCase），如`ChatView.vue`
- 文件名：使用小驼峰命名法（camelCase），如`chatApi.js`
- 变量名：使用小驼峰命名法（camelCase），如`chatbotList`
- 常量名：使用全大写下划线分隔（UPPER_SNAKE_CASE），如`API_BASE_URL`
- CSS类名：使用kebab-case，如`chat-message`

## 2. 代码风格

### 2.1 Java代码规范
- 使用4个空格进行缩进
- 每行代码不超过120个字符
- 类和方法之间空一行
- 相关的代码块之间空一行
- 使用`@Override`注解标记重写方法
- 使用`@Deprecated`注解标记废弃方法
- 使用`@SuppressWarnings`注解时说明原因
- 禁止使用Lombok，必须手动编写getter/setter
- 使用构造器注入而不是字段注入
- 使用final修饰不可变字段
- 使用Optional处理可能为空的值
- 使用枚举替代魔法数字
- 使用Builder模式创建复杂对象
- 使用策略模式处理条件分支
- 使用观察者模式处理事件

```java
public class ChatbotEntity {
    private final String chatbotId;
    private final String name;
    private final String description;
    private final Map<String, Object> config;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;
    
    public ChatbotEntity(String chatbotId, String name, String description, 
                        Map<String, Object> config) {
        this.chatbotId = chatbotId;
        this.name = name;
        this.description = description;
        this.config = new HashMap<>(config);
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    // Getters
    public String getChatbotId() {
        return chatbotId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, Object> getConfig() {
        return Collections.unmodifiableMap(config);
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    // Builder
    public static class Builder {
        private String chatbotId;
        private String name;
        private String description;
        private Map<String, Object> config = new HashMap<>();
        
        public Builder chatbotId(String chatbotId) {
            this.chatbotId = chatbotId;
            return this;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder config(Map<String, Object> config) {
            this.config.putAll(config);
            return this;
        }
        
        public ChatbotEntity build() {
            return new ChatbotEntity(chatbotId, name, description, config);
        }
    }
}
```

### 2.2 JavaScript/Vue代码规范
- 使用2个空格进行缩进
- 每行代码不超过100个字符
- 使用单引号而不是双引号
- 使用分号结束语句
- 使用`const`和`let`而不是`var`
- 使用箭头函数而不是普通函数
- 使用解构赋值
- 使用模板字符串而不是字符串拼接

```javascript
const API_BASE_URL = '/api';

const getChatbotList = async () => {
  try {
    const { data } = await axios.get(`${API_BASE_URL}/chatbots`);
    return data;
  } catch (error) {
    console.error('获取机器人列表失败:', error);
    throw error;
  }
};
```

### 2.3 Vue组件规范
- 组件名使用大驼峰命名法
- 使用组合式API（Composition API）
- 使用`<script setup>`语法
- 使用`defineProps`和`defineEmits`声明属性和事件
- 使用`ref`和`reactive`管理状态
- 使用`computed`和`watch`处理响应式数据

```vue
<script setup>
import { ref, onMounted } from 'vue';
import { useChatbotStore } from '../api/chatbot';

const chatbotStore = useChatbotStore();
const chatbotList = ref([]);

const props = defineProps({
  chatbotId: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['update']);

onMounted(async () => {
  await loadChatbots();
});
</script>
```

## 3. 注释规范

### 3.1 类注释
```java
/**
 * 聊天机器人实体类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
```

### 3.2 方法注释
```java
/**
 * 根据ID获取聊天机器人
 *
 * @param chatbotId 聊天机器人ID
 * @return 聊天机器人实体
 * @throws ChatbotException 当机器人不存在时抛出异常
 */
```

### 3.3 代码注释
- 使用`//`进行单行注释
- 使用`/* */`进行多行注释
- 注释应该解释"为什么"而不是"是什么"
- 避免无意义的注释

## 4. Git提交规范

### 4.1 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### 4.2 类型说明
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建过程或辅助工具的变动

### 4.3 示例
```
feat(chat): 添加消息发送功能

- 实现消息发送API
- 添加消息发送UI组件
- 添加消息发送状态管理

Closes #123
```

## 5. 测试规范

### 5.1 单元测试
- 使用JUnit 5进行单元测试
- 测试类名以`Test`结尾
- 测试方法名以`test`开头
- 使用`@Test`注解标记测试方法
- 使用断言验证测试结果

```java
@Test
void testGetChatbotById() {
    // 准备测试数据
    String chatbotId = "test-id";
    ChatbotEntity expected = new ChatbotEntity();
    expected.setChatbotId(chatbotId);
    
    // 执行测试
    ChatbotEntity actual = chatbotService.getChatbotById(chatbotId);
    
    // 验证结果
    assertEquals(expected.getChatbotId(), actual.getChatbotId());
}
```

### 5.2 集成测试
- 使用`@SpringBootTest`进行集成测试
- 使用`@TestConfiguration`配置测试环境
- 使用`@MockBean`模拟依赖
- 使用`@Transactional`管理测试数据

## 6. 安全规范

### 6.1 数据安全
- 敏感数据加密存储
- 使用HTTPS传输
- 实现请求签名验证
- 实现防重放攻击机制

### 6.2 代码安全
- 避免SQL注入
- 避免XSS攻击
- 避免CSRF攻击
- 实现请求频率限制

## 7. 性能规范

### 7.1 后端性能
- 使用缓存优化查询
- 实现分页查询
- 优化数据库索引
- 使用异步处理耗时操作

### 7.2 前端性能
- 实现组件懒加载
- 使用虚拟滚动
- 优化图片加载
- 实现数据缓存 