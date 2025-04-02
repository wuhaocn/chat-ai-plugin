import { useChatbotStore } from './chatbot';
import { API_ENDPOINTS } from '../config/api';

// 全局变量
let currentUserId = '';  // 当前用户ID
let currentConversationId = '';  // 当前会话ID
let chatbotId = '';  // 当前聊天机器人ID

// DOM 元素
const messageInput = document.getElementById('messageInput');
const sendButton = document.getElementById('sendButton');
const chatMessages = document.getElementById('chatMessages');

// 常量定义
const APP_KEY = 'test';

// API 配置
export const api = {
  baseURL: '/api',
  
  // 获取会话历史
  async getConversationHistory() {
    try {
      const response = await fetch(API_ENDPOINTS.messages.history);
      if (!response.ok) {
        console.warn('获取会话历史失败:', response.status);
        return [];
      }
      return await response.json();
    } catch (error) {
      console.warn('获取会话历史出错:', error);
      return [];
    }
  },

  // 发送消息
  async sendMessage(message) {
    try {
      const formData = new URLSearchParams();
      const timestamp = Date.now().toString();
      const msgUID = generateMessageId();
      
      // 设置基本消息参数
      formData.append('channelType', 'PERSON');
      formData.append('fromUserId', message.fromUserId);
      formData.append('toUserId', message.toUserId);
      formData.append('content', JSON.stringify({ content: message.content }));
      formData.append('msgTimestamp', timestamp);
      formData.append('timestamp', timestamp);
      formData.append('objectName', 'RC:TxtMsg');
      formData.append('sensitiveType', '0');
      formData.append('source', 'Android');
      formData.append('msgUID', msgUID);
      formData.append('clientIp', window.location.hostname);

      const response = await fetch(API_ENDPOINTS.messages.send, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: formData.toString()
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.warn('发送消息失败:', error);
      return { error: '发送消息失败，请稍后重试' };
    }
  },

  // 添加消息到界面
  appendMessage(message, isUser = false) {
    try {
      const messageContainer = document.getElementById('message-container');
      if (!messageContainer) {
        console.warn('消息容器不存在');
        return;
      }

      const messageElement = document.createElement('div');
      messageElement.className = `message ${isUser ? 'user' : 'ai'}`;
      messageElement.textContent = message;
      messageContainer.appendChild(messageElement);
      messageContainer.scrollTop = messageContainer.scrollHeight;
    } catch (error) {
      console.warn('添加消息到界面失败:', error);
    }
  },

  // 处理错误
  handleError(error) {
    console.warn('操作失败:', error);
    try {
      this.appendMessage('操作失败，请稍后重试', false);
    } catch (e) {
      console.warn('显示错误消息失败:', e);
    }
  },

  // 加载会话历史
  async loadConversationHistory() {
    try {
      const history = await this.getConversationHistory();
      if (Array.isArray(history)) {
        history.forEach(msg => {
          this.appendMessage(msg.content, msg.isUser);
        });
      }
    } catch (error) {
      console.warn('加载会话历史失败:', error);
    }
  },

  // 处理路由
  async handleRoute() {
    try {
      const path = window.location.pathname;
      const chatMatch = path.match(/^\/chat\/(\d+)$/);
      
      if (!chatMatch) {
        console.warn('无效的页面路径');
        return false;
      }

      const id = chatMatch[1];
      if (!id) {
        console.warn('无效的机器人ID');
        return false;
      }

      const isValid = await this.validateChatbotId(id);
      if (!isValid) {
        console.warn('无效的机器人ID');
        return false;
      }
      
      chatbotId = id;
      currentConversationId = generateConversationId();
      await this.loadConversationHistory();
      return true;
    } catch (error) {
      console.warn('路由处理失败:', error);
      return false;
    }
  },

  // 验证机器人ID
  async validateChatbotId(id) {
    try {
      const chatbotStore = useChatbotStore();
      const chatbot = await chatbotStore.fetchChatbots();
      return chatbot.some(c => c.chatbotId === id);
    } catch (error) {
      console.error('验证机器人ID失败:', error);
      return false;
    }
  }
};

// 消息处理
const messageHandler = {
  // 添加消息到聊天界面
  appendMessage(type, content) {
    try {
      if (!chatMessages) {
        console.warn('聊天消息容器不存在');
        return;
      }

      if (typeof content !== 'string') {
        content = String(content || '');
      }
      
      const messageDiv = document.createElement('div');
      messageDiv.className = `message ${type}-message`;
      
      const contentDiv = document.createElement('div');
      contentDiv.className = 'message-content';
      contentDiv.textContent = content;
      
      messageDiv.appendChild(contentDiv);
      chatMessages.appendChild(messageDiv);
      chatMessages.scrollTop = chatMessages.scrollHeight;
    } catch (error) {
      console.warn('添加消息失败:', error);
    }
  },

  // 处理服务器响应
  handleServerResponse(data) {
    try {
      if (!data) {
        console.warn('服务器响应数据为空');
        return;
      }

      if (data.status === 'success') {
        console.log('消息发送成功，等待机器人回复');
      } else {
        console.warn('消息发送失败:', data.errorMessage);
      }
    } catch (error) {
      console.warn('处理服务器响应失败:', error);
    }
  },

  // 处理错误
  handleError(error) {
    try {
      console.warn('操作失败:', error);
      if (chatMessages) {
        this.appendMessage('system', `错误: ${error?.message || '请重试'}`);
      }
    } catch (e) {
      console.warn('处理错误失败:', e);
    }
  }
};

// 路由处理
async function handleRoute() {
  const path = window.location.pathname;
  const chatMatch = path.match(/^\/chat\/(\d+)$/);
  
  if (!chatMatch) {
    messageHandler.appendMessage('system', '无效的页面路径');
    return false;
  }

  const id = chatMatch[1];
  if (!id) {
    messageHandler.appendMessage('system', '无效的机器人ID');
    return false;
  }

  try {
    const isValid = await api.validateChatbotId(id);
    if (!isValid) {
      throw new Error('无效的机器人ID');
    }
    
    chatbotId = id;
    currentConversationId = generateConversationId();
    await loadConversationHistory();
    return true;
  } catch (error) {
    messageHandler.handleError(error);
    return false;
  }
}

// 初始化
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const routeHandled = await handleRoute();
        if (!routeHandled) {
            return;
        }

        // 绑定事件
        if (sendButton) {
            sendButton.addEventListener('click', sendMessage);
        }
        if (messageInput) {
            messageInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendMessage();
                }
            });
        }
    } catch (error) {
        console.warn('初始化失败:', error);
    }
});

// 生成会话ID
function generateConversationId() {
    return 'conv_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

// 加载会话历史
async function loadConversationHistory() {
    try {
        if (!chatMessages) {
            console.warn('聊天消息容器不存在');
            return;
        }

        const messages = await api.getConversationHistory();
        if (Array.isArray(messages)) {
            messages.forEach(msg => {
                messageHandler.appendMessage(msg.fromUserId === currentUserId ? 'user' : 'system', msg.content);
            });
            try {
                chatMessages.scrollTop = chatMessages.scrollHeight;
            } catch (error) {
                console.warn('滚动到底部失败:', error);
            }
        }
    } catch (error) {
        messageHandler.handleError(error);
    }
}

// 生成UUID
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// 生成消息ID
function generateMessageId() {
  return 'CLU0-' + Math.random().toString(36).substr(2, 4).toUpperCase() + '-' +
         Math.random().toString(36).substr(2, 4).toUpperCase() + '-' +
         Math.random().toString(36).substr(2, 4).toUpperCase();
}

// 生成随机签名
function generateRandomSignature() {
  return Math.random().toString(36).substr(2, 32);
}

// 发送消息
async function sendMessage() {
    try {
        if (!messageInput || !chatbotId) {
            console.warn('消息输入框或聊天机器人ID不存在');
            return;
        }

        const content = messageInput.value.trim();
        if (!content) return;

        setInputState(false);

        messageHandler.appendMessage('user', content);
        messageInput.value = '';

        const response = await api.sendMessage(content);
        messageHandler.handleServerResponse(response);
    } catch (error) {
        console.warn('发送消息失败:', error);
    } finally {
        setInputState(true);
        if (messageInput) {
            messageInput.focus();
        }
    }
}

// 设置输入状态
function setInputState(enabled) {
    try {
        if (messageInput) {
            messageInput.disabled = !enabled;
        }
        if (sendButton) {
            sendButton.disabled = !enabled;
        }
    } catch (error) {
        console.warn('设置输入状态失败:', error);
    }
} 