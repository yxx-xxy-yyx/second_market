<template>
  <div class="ai-assistant-wrapper hidden lg:block">
    <!-- 悬浮按钮 -->
    <div 
      class="ai-float-btn" 
      :class="{ 'is-active': showChat }" 
      @mousedown="startDrag"
      @touchstart="startDrag"
      @click="toggleChat"
      :style="floatBtnStyle"
    >
      <div class="ai-icon-container">
        <el-icon v-if="!showChat" :size="30">
          <MagicStick />
        </el-icon>
        <el-icon v-else :size="30">
          <Close />
        </el-icon>
      </div>
      <div v-if="!showChat" class="ai-btn-text">{{ $t('ai.assistant') }}</div>
    </div>

    <!-- 聊天窗口 -->
    <transition name="el-zoom-in-bottom">
      <div v-show="showChat" class="ai-chat-window">
        <div class="chat-header">
          <div class="header-info">
            <el-icon :size="20" color="#409eff">
              <MagicStick />
            </el-icon>
            <span class="header-title">{{ $t('ai.assistant') }}</span>
          </div>
          <div class="header-actions">
            <el-button link @click="clearMessages" :title="$t('ai.clearChat')">
              <el-icon>
                <Delete />
              </el-icon>
            </el-button>
            <el-button link @click="showChat = false">
              <el-icon>
                <SemiSelect />
              </el-icon>
            </el-button>
          </div>
        </div>

        <div class="chat-messages" ref="messageContainer">
          <div v-for="(msg, index) in messages" :key="index" :class="['message-item', msg.role]">
            <div class="message-avatar">
              <el-avatar :size="32" :src="msg.role === 'ai' ? aiAvatar : userAvatar" />
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div v-if="msg.loading" class="loading-dots">
                  <span></span><span></span><span></span>
                </div>
                <div v-else class="markdown-body" v-html="renderMarkdown(msg.content)"></div>
              </div>
              <div class="message-time">{{ msg.time }}</div>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <div class="quick-tags">
            <el-tag v-for="tag in quickTags" :key="tag" size="small" class="quick-tag" @click="handleQuickTag(tag)">
              {{ tag }}
            </el-tag>
          </div>
          <div class="input-wrapper">
            <el-input v-model="inputMsg" :placeholder="$t('ai.chatPlaceholder')" type="textarea" :rows="2" resize="none"
              @keydown.enter.prevent="sendMessage" />
            <el-button type="primary" class="send-btn" :disabled="!inputMsg.trim() || loading" @click="sendMessage">
              <el-icon>
                <Promotion />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import { MagicStick, Close, Delete, SemiSelect, Promotion } from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { marked } from 'marked'
import { useI18n } from 'vue-i18n'
import { formatAvatarUrl } from '@/utils/url'

const userStore = useUserStore()
const { t, locale } = useI18n()
const showChat = ref(false)
const inputMsg = ref('')
const loading = ref(false)
const messageContainer = ref(null)

// 拖动相关状态
const position = ref({ x: 0, y: 0 })
const isDragging = ref(false)
const startPos = ref({ x: 0, y: 0 })
const dragStartTime = ref(0)

const floatBtnStyle = computed(() => {
  if (position.value.x === 0 && position.value.y === 0) return {}
  return {
    transform: `translate(${position.value.x}px, ${position.value.y}px)`,
    transition: isDragging.value ? 'none' : 'transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)'
  }
})

const startDrag = (e) => {
  isDragging.value = true
  dragStartTime.value = Date.now()
  const event = e.touches ? e.touches[0] : e
  startPos.value = {
    x: event.clientX - position.value.x,
    y: event.clientY - position.value.y
  }
  
  const onMouseMove = (moveEvent) => {
    if (!isDragging.value) return
    const currentEvent = moveEvent.touches ? moveEvent.touches[0] : moveEvent
    position.value = {
      x: currentEvent.clientX - startPos.value.x,
      y: currentEvent.clientY - startPos.value.y
    }
  }
  
  const onMouseUp = () => {
    isDragging.value = false
    document.removeEventListener('mousemove', onMouseMove)
    document.removeEventListener('mouseup', onMouseUp)
    document.removeEventListener('touchmove', onMouseMove)
    document.removeEventListener('touchend', onMouseUp)
  }
  
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', onMouseUp)
  document.addEventListener('touchmove', onMouseMove, { passive: false })
  document.addEventListener('touchend', onMouseUp)
}

const aiAvatar =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='128' height='128' viewBox='0 0 128 128'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%23409eff'/><stop offset='1' stop-color='%2396c8ff'/></linearGradient></defs><rect width='128' height='128' rx='28' fill='url(%23g)'/><path d='M40 54c0-13 10-23 24-23s24 10 24 23v12c0 13-10 23-24 23s-24-10-24-23V54z' fill='white' opacity='0.95'/><circle cx='56' cy='60' r='4' fill='%23409eff'/><circle cx='72' cy='60' r='4' fill='%23409eff'/><path d='M52 72c6 6 18 6 24 0' stroke='%23409eff' stroke-width='4' stroke-linecap='round' fill='none'/></svg>"
const userAvatar = computed(() => formatAvatarUrl(userStore.user?.avatar))

const messages = ref([
  {
    role: 'ai',
    content: t('ai.welcomeMsg'),
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }
])

const quickTags = computed(() => {
  return [
    t('ai.quickTag1'),
    t('ai.quickTag2'),
    t('ai.quickTag3'),
    t('ai.quickTag4')
  ]
})

const toggleChat = () => {
  // 如果拖动时间超过 200ms，认为是拖动而不是点击
  if (Date.now() - dragStartTime.value > 200) return
  
  showChat.value = !showChat.value
  if (showChat.value) {
    scrollToBottom()
  }
}

const renderMarkdown = (content) => {
  return marked(content)
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

const clearMessages = () => {
  messages.value = [messages.value[0]]
}

const handleQuickTag = (tag) => {
  inputMsg.value = tag
  sendMessage()
}

const sendMessage = async () => {
  if (!inputMsg.value.trim() || loading.value) return

  const content = inputMsg.value.trim()
  inputMsg.value = ''

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  })

  scrollToBottom()

  // 添加 AI 等待消息
  const aiMsgIndex = messages.value.length
  messages.value.push({
    role: 'ai',
    content: '',
    loading: true,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  })

  loading.value = true
  try {
    const res = await aiApi.generateContent({
      prompt: content,
      type: 'GENERAL_CONTENT'
    })

    if (res.success || res.code === '200' || res.code === 200) {
      messages.value[aiMsgIndex].content = res.data?.content || res.data || ''
      messages.value[aiMsgIndex].loading = false
    } else {
      throw new Error(res.message || 'AI 响应失败')
    }
  } catch (error) {
    messages.value[aiMsgIndex].content = t('ai.errorMsg')
    messages.value[aiMsgIndex].loading = false
    console.error('AI Chat Error:', error)
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const toggleChatWindow = () => {
  showChat.value = !showChat.value
  if (showChat.value) {
    scrollToBottom()
  }
}

// 暴露给外部调用
defineExpose({
  toggleChat: toggleChatWindow
})

onMounted(() => {
  window.addEventListener('toggle-ai-assistant', toggleChatWindow)
})
</script>

<style scoped>
.ai-assistant-wrapper {
  position: fixed;
  right: 20px;
  bottom: 80px;
  z-index: 2000;
}

@media screen and (max-width: 768px) {
  .ai-assistant-wrapper {
    bottom: 140px;
    right: 15px;
  }
}

/* 悬浮按钮样式 */
.ai-float-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #007aff 100%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: grab;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  user-select: none;
  touch-action: none;
}

.ai-float-btn:active {
  cursor: grabbing;
}

.ai-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6);
}

.ai-float-btn.is-active {
  background: #f56c6c;
  box-shadow: 0 4px 15px rgba(245, 108, 108, 0.4);
}

.ai-btn-text {
  font-size: 10px;
  margin-top: 2px;
  font-weight: bold;
}

/* 聊天窗口样式 */
.ai-chat-window {
  position: absolute;
  bottom: 75px;
  right: 0;
  width: 380px;
  height: 550px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

@media screen and (max-width: 480px) {
  .ai-chat-window {
    width: calc(100vw - 30px);
    height: 70vh;
    right: -15px;
  }
}

.chat-header {
  padding: 15px 20px;
  background: #f8f9fb;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-weight: bold;
  color: #303133;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #fcfcfd;
}

.message-item {
  display: flex;
  gap: 12px;
  max-width: 90%;
}

.message-item.user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  position: relative;
}

.ai .message-bubble {
  background: white;
  border: 1px solid #ebeef5;
  color: #303133;
  border-top-left-radius: 2px;
}

.user .message-bubble {
  background: #409eff;
  color: white;
  border-top-right-radius: 2px;
}

.message-time {
  font-size: 10px;
  color: #909399;
  margin-top: 4px;
}

.user .message-time {
  text-align: right;
}

.chat-input-area {
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background: white;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.input-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.send-btn {
  height: 52px;
}

/* Markdown 样式微调 */
:deep(.markdown-body) {
  font-size: 14px;
}

:deep(.markdown-body p) {
  margin: 0 0 8px 0;
}

:deep(.markdown-body p:last-child) {
  margin-bottom: 0;
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.loading-dots span {
  width: 6px;
  height: 6px;
  background-color: #909399;
  border-radius: 50%;
  display: inline-block;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {

  0%,
  80%,
  100% {
    transform: scale(0);
  }

  40% {
    transform: scale(1.0);
  }
}
</style>
