<template>
  <div class="chatbot-list">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2>聊天机器人管理</h2>
            <el-tag type="info" effect="plain" class="count-tag">
              {{ store.chatbots.length }} 个机器人
            </el-tag>
          </div>
          <el-button type="primary" @click="showCreateDialog" class="create-btn">
            <el-icon><Plus /></el-icon>创建聊天机器人
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="store.loading"
        :data="store.chatbots"
        style="width: 100%"
        :header-cell-style="{ 
          background: '#ffffff',
          color: '#606266',
          fontWeight: 500,
          borderBottom: '1px solid #ebeef5'
        }"
        :cell-style="{ borderBottom: '1px solid #ebeef5' }"
        border>
        <el-table-column prop="chatbotId" label="ID" width="180" />
        <el-table-column prop="chatbotName" label="名称" width="180">
          <template #default="{ row }">
            <div class="chatbot-name">
              <el-icon><Monitor /></el-icon>
              <el-button link type="primary" @click="startChat(row)">
                {{ row.chatbotName }}
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="chatbotType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.chatbotType === 'DIFY' ? 'success' : 'warning'" effect="plain">
              {{ row.chatbotType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="chatbotModel" label="模型" width="120" />
        <el-table-column prop="chatbotStatus" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.chatbotStatus === 'ACTIVE' ? 'success' : 'danger'" effect="dark">
              {{ row.chatbotStatus === 'ACTIVE' ? '运行中' : '已停止' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button-group>
              <el-button size="small" type="primary" @click="showEditDialog(row)" class="action-btn">
                <el-icon><Edit /></el-icon>编辑
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="handleDelete(row)"
                class="action-btn">
                <el-icon><Delete /></el-icon>删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建聊天机器人' : '编辑聊天机器人'"
      width="50%"
      destroy-on-close
      class="chatbot-dialog">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="chatbot-form">
        <el-form-item label="机器人ID" prop="chatbotId">
          <el-input v-model="form.chatbotId" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="机器人名称" prop="chatbotName">
          <el-input v-model="form.chatbotName" />
        </el-form-item>
        <el-form-item label="机器人类型" prop="chatbotType">
          <el-select v-model="form.chatbotType">
            <el-option label="Dify" value="DIFY" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="Hook URL" prop="chatbotHookUrl">
          <el-input v-model="form.chatbotHookUrl" />
        </el-form-item>
        <el-form-item label="请求头">
          <div v-for="(header, index) in form.chatbotHookHeaders" :key="index" class="header-item">
            <el-input v-model="header.key" placeholder="Header Key" style="width: 200px; margin-right: 10px" />
            <el-input v-model="header.value" placeholder="Header Value" style="width: 300px" />
            <el-button type="danger" circle @click="removeHeader(index)" style="margin-left: 10px">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <el-button type="primary" @click="addHeader">添加请求头</el-button>
        </el-form-item>
        <el-form-item label="模型" prop="chatbotModel">
          <el-input v-model="form.chatbotModel" />
        </el-form-item>
        <el-form-item label="温度" prop="chatbotTemperature">
          <el-slider
            v-model="form.chatbotTemperature"
            :min="0"
            :max="2"
            :step="0.1"
            :marks="{
              0: '0',
              0.5: '0.5',
              1: '1',
              1.5: '1.5',
              2: '2'
            }"
            show-input
            class="temperature-slider" />
        </el-form-item>
        <el-form-item label="最大Token" prop="chatbotMaxTokens">
          <el-input-number
            v-model="form.chatbotMaxTokens"
            :min="1"
            :max="4000"
            :step="100"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="系统提示词" prop="chatbotSystemPrompt">
          <el-input
            v-model="form.chatbotSystemPrompt"
            type="textarea"
            :rows="4"
            placeholder="请输入系统提示词" />
        </el-form-item>
        <el-form-item label="状态" prop="chatbotStatus">
          <el-select v-model="form.chatbotStatus">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatbotStore } from '../api/chatbot'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Monitor } from '@element-plus/icons-vue'

const router = useRouter()
const store = useChatbotStore()
const dialogVisible = ref(false)
const dialogType = ref('create')
const formRef = ref(null)

const form = ref({
  chatbotId: '',
  chatbotName: '',
  chatbotType: 'DIFY',
  chatbotHookUrl: 'https://api.dify.ai/v1/chat-messages',
  chatbotHookHeaders: [
    { key: 'Content-Type', value: 'application/json' },
    { key: 'Authorization', value: 'Bearer your-api-key' }
  ],
  chatbotModel: 'gpt-3.5-turbo',
  chatbotTemperature: 0.7,
  chatbotMaxTokens: 2000,
  chatbotSystemPrompt: '你是一个专业的AI助手，请用简洁、专业的语言回答问题。',
  chatbotStatus: 'ACTIVE'
})

const rules = {
  chatbotId: [
    { required: true, message: '请输入机器人ID', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: 'ID只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  chatbotName: [
    { required: true, message: '请输入机器人名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在2到50个字符之间', trigger: 'blur' }
  ],
  chatbotType: [
    { required: true, message: '请选择机器人类型', trigger: 'change' }
  ],
  chatbotHookUrl: [
    { required: true, message: '请输入机器人Hook URL', trigger: 'blur' },
    { pattern: /^https?:\/\/.+$/, message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  chatbotModel: [
    { required: true, message: '请输入模型名称', trigger: 'blur' }
  ],
  chatbotTemperature: [
    { required: true, message: '请设置温度值', trigger: 'change' },
    { type: 'number', min: 0, max: 2, message: '温度值在0到2之间', trigger: 'change' }
  ],
  chatbotMaxTokens: [
    { required: true, message: '请设置最大Token数', trigger: 'change' },
    { type: 'number', min: 1, max: 4000, message: 'Token数在1到4000之间', trigger: 'change' }
  ],
  chatbotSystemPrompt: [
    { required: true, message: '请输入系统提示词', trigger: 'blur' },
    { min: 10, max: 500, message: '提示词长度在10到500个字符之间', trigger: 'blur' }
  ],
  chatbotStatus: [
    { required: true, message: '请选择机器人状态', trigger: 'change' }
  ]
}

onMounted(() => {
  store.fetchChatbots()
})

const showCreateDialog = () => {
  dialogType.value = 'create'
  form.value = {
    chatbotId: '',
    chatbotName: '',
    chatbotType: 'DIFY',
    chatbotHookUrl: 'https://api.dify.ai/v1/chat-messages',
    chatbotHookHeaders: [
      { key: 'Content-Type', value: 'application/json' },
      { key: 'Authorization', value: 'Bearer your-api-key' }
    ],
    chatbotModel: 'gpt-3.5-turbo',
    chatbotTemperature: 0.7,
    chatbotMaxTokens: 2000,
    chatbotSystemPrompt: '你是一个专业的AI助手，请用简洁、专业的语言回答问题。',
    chatbotStatus: 'ACTIVE'
  }
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  dialogType.value = 'edit'
  // 确保chatbotHookHeaders是一个对象
  const headers = row.chatbotHookHeaders && typeof row.chatbotHookHeaders === 'object'
    ? Object.entries(row.chatbotHookHeaders).map(([key, value]) => ({
        key,
        value
      }))
    : []
  form.value = {
    ...row,
    chatbotHookHeaders: headers
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 将chatbotHookHeaders从数组转换为对象
        const formData = { ...form.value }
        formData.chatbotHookHeaders = form.value.chatbotHookHeaders.reduce((acc, header) => {
          if (header.key && header.value) {
            acc[header.key] = header.value
          }
          return acc
        }, {})

        if (dialogType.value === 'create') {
          await store.createChatbot(formData)
          ElMessage.success('创建成功')
        } else {
          await store.updateChatbot(formData.chatbotId, formData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个聊天机器人吗？', '警告', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await store.deleteChatbot(row.chatbotId)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 开始聊天
const startChat = (chatbot) => {
  router.push(`/chat/${chatbot.chatbotId}`)
}

// 添加请求头
const addHeader = () => {
  form.value.chatbotHookHeaders.push({ key: '', value: '' })
}

// 删除请求头
const removeHeader = (index) => {
  form.value.chatbotHookHeaders.splice(index, 1)
}
</script>

<style scoped>
.chatbot-list {
  min-height: 100%;
}

.box-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.box-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: var(--text-primary);
}

.count-tag {
  font-size: 12px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
}

.create-btn {
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
}

.create-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.chatbot-name {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-regular);
}

.chatbot-name .el-icon {
  font-size: 16px;
  color: var(--primary-color);
}

.action-btn {
  transition: all 0.3s;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.chatbot-form {
  padding: 20px;
}

.temperature-slider {
  margin: 0 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-table) {
  margin-top: 10px;
}

:deep(.el-table th) {
  font-weight: 500;
}

:deep(.el-button-group) {
  display: flex;
  gap: 8px;
}

:deep(.el-button .el-icon) {
  margin-right: 4px;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border: 1px solid var(--border-color);
}

:deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary-color);
}

:deep(.el-select .el-input__wrapper) {
  box-shadow: none;
}

:deep(.el-textarea__inner) {
  box-shadow: none;
  border: 1px solid var(--border-color);
}

:deep(.el-textarea__inner:hover) {
  border-color: var(--primary-color);
}

:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px var(--primary-color);
}

:deep(.el-slider__runway) {
  height: 4px;
}

:deep(.el-slider__button-wrapper) {
  top: -15px;
}

:deep(.el-slider__button) {
  width: 16px;
  height: 16px;
  border: 2px solid var(--primary-color);
}

:deep(.el-slider__bar) {
  height: 4px;
  background-color: var(--primary-color);
}

:deep(.el-slider__marks-text) {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .box-card {
    margin: 0 -20px;
    border-radius: 0;
  }
  
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .create-btn {
    width: 100%;
  }
  
  :deep(.el-dialog) {
    width: 90% !important;
    margin: 0 auto;
  }
}

.header-item {
  margin-bottom: 10px;
}

.header-item:last-child {
  margin-bottom: 0;
}

:deep(.el-row) {
  margin-bottom: 0 !important;
}

:deep(.el-col) {
  display: flex;
  align-items: center;
}
</style> 