<template>
  <div class="chat-view">
    <el-card class="chat-card">
      <template #header>
        <div class="chat-header">
          <h2>聊天对话</h2>
          <div class="header-info">
            <el-input
              v-model="userId"
              placeholder="请输入用户ID"
              class="user-id-input"
              style="width: 200px; margin-right: 10px;"
              clearable>
            </el-input>
            <el-tag type="info" effect="plain" v-if="currentConversationId">
              会话ID: {{ currentConversationId }}
            </el-tag>
          </div>
        </div>
      </template>

      <div class="chat-content">
        <div class="chat-messages" ref="messagesContainer">
          <div v-for="message in messages" :key="message.id" 
               class="message-item" 
               :class="{ 'message-from-user': message.fromUserId === userId }">
            <div class="message-info">
              <span class="message-sender">{{ message.fromUserId === userId ? userId : (message.fromUserId === 'system' ? '系统' : route.params.id) }}</span>
            </div>
            <div class="message-content">{{ message.content || '' }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <el-button
          v-show="showScrollButton"
          class="scroll-button"
          circle
          @click="scrollToBottom">
          <el-icon><ArrowDown /></el-icon>
        </el-button>
      </div>

      <div class="chat-input">
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入消息..."
            @keyup.enter.ctrl="sendMessage"
            :disabled="loading">
          </el-input>
          <el-button
            type="primary"
            @click="sendMessage"
            :loading="loading"
            :disabled="!inputMessage.trim()"
            class="send-button">
            发送
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { api } from '../api/chat'

const route = useRoute()
const inputMessage = ref('')
const userId = ref('')
const messagesContainer = ref(null)
const messages = ref([])
const loading = ref(false)
const currentConversationId = ref('')
const showScrollButton = ref(false)
let eventSource = null

// 生成会话ID
const generateConversationId = () => {
  if (!userId.value || !route.params.id) {
    return ''
  }
  return `${userId.value}_${route.params.id}`
}

// 监听用户ID变化，更新会话ID
watch(userId, (newValue) => {
  if (newValue && route.params.id) {
    currentConversationId.value = generateConversationId()
  }
})

// 监听滚动事件
const handleScroll = () => {
  if (messagesContainer.value) {
    const { scrollTop, scrollHeight, clientHeight } = messagesContainer.value
    showScrollButton.value = scrollHeight - scrollTop - clientHeight > 100
  }
}

// 从路由参数获取聊天机器人ID
onMounted(() => {
  const chatbotId = route.params.id
  if (chatbotId && userId.value) {
    currentConversationId.value = generateConversationId()
  } else if (!chatbotId) {
    ElMessage.error('未找到聊天机器人ID')
  }
  // 初始化时滚动到底部
  scrollToBottom()
  // 添加滚动监听
  if (messagesContainer.value) {
    messagesContainer.value.addEventListener('scroll', handleScroll)
  }
})

// 组件卸载时清理
onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
  if (messagesContainer.value) {
    messagesContainer.value.removeEventListener('scroll', handleScroll)
  }
})

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  if (!userId.value.trim()) {
    ElMessage.warning('请输入用户ID')
    return
  }

  const messageContent = inputMessage.value.trim()
  loading.value = true

  try {
    // 添加用户消息到聊天界面（右侧）
    messages.value.push({
      id: Date.now(),
      content: messageContent,
      senderUserId: userId.value,
      targetId: route.params.id,
      timestamp: Math.floor(Date.now() / 1000),
      messageType: 'text',
      conversationId: currentConversationId.value,
      extra: {
        source: 'web',
        platform: 'web'
      }
    })

    // 清空输入框
    inputMessage.value = ''

    // 发送消息到服务器
    const response = await api.sendMessage({
      fromUserId: userId.value,
      toUserId: route.params.id,
      content: messageContent
    })

    if (response.status === 'success') {
      // 添加系统提示消息到聊天界面（左侧）
      messages.value.push({
        id: Date.now() + 1,
        content: '发送成功',
        senderUserId: 'system',
        targetId: userId.value,
        timestamp: Math.floor(Date.now() / 1000),
        messageType: 'system',
        conversationId: currentConversationId.value
      })
    } else {
      throw new Error(response.errorMessage || '消息发送失败')
    }

    // 滚动到底部
    await scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
    // 添加错误提示消息到聊天界面（左侧）
    messages.value.push({
      id: Date.now() + 1,
      content: `发送失败: ${error.message || '请重试'}`,
      senderUserId: 'system',
      targetId: userId.value,
      timestamp: Math.floor(Date.now() / 1000),
      messageType: 'system',
      conversationId: currentConversationId.value
    })
  } finally {
    loading.value = false
  }
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    const container = messagesContainer.value
    container.scrollTop = container.scrollHeight
    // 确保滚动到底部
    setTimeout(() => {
      container.scrollTop = container.scrollHeight
    }, 100)
  }
}

// 格式化时间
const formatTime = (timestamp) => {
  return new Date(timestamp * 1000).toLocaleString()
}

// 监听消息列表变化，自动滚动到底部
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

// 监听输入框变化，自动调整高度
watch(inputMessage, () => {
  nextTick(() => {
    scrollToBottom()
  })
})
</script>

<style scoped>
.chat-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  height: calc(100vh - 180px);
  position: relative;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  background-color: #fff;
  z-index: 3;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-id-input {
  margin-right: 10px;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: calc(100% - 5px);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  margin-bottom: 20px;
  z-index: 1;
  min-height: 500px;
}

/* 自定义滚动条样式 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.message-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 80%;
  margin-right: 20%;
}

.message-from-user {
  margin-right: 0;
  margin-left: 20%;
}

.message-info {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.message-sender {
  font-weight: 500;
}

.message-content {
  padding: 12px 16px;
  background-color: #95ec69;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-from-user .message-content {
  background-color: #e8f5fe;
}

.message-time {
  font-size: 12px;
  color: #999;
  align-self: flex-end;
}

.chat-input {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 2;
  height: 80px;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

:deep(.el-textarea__inner) {
  resize: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid #dcdfe6;
  min-height: 50px;
  max-height: 50px;
}

.send-button {
  height: 60px;
  padding: 0 24px;
  border-radius: 8px;
  background-color: #409eff;
  color: white;
  border: none;
  transition: all 0.3s;
}

.send-button:hover {
  opacity: 0.9;
}

.send-button:disabled {
  background-color: #a0cfff;
  color: white;
}

:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #409eff;
}

:deep(.el-textarea__inner:hover) {
  border-color: #409eff;
}

.scroll-button {
  position: absolute;
  right: 20px;
  bottom: 100px;
  z-index: 3;
  background-color: #409eff;
  color: white;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.scroll-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.2);
}
</style> 