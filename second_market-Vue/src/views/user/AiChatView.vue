<template>
  <div class="chat-wrapper bg-[#f8fafc]">

    <div
      class="top-nav-placeholder bg-white/80 backdrop-blur-xl px-4 flex items-center justify-between border-b border-gray-100 z-20">
      <div class="w-10 h-10 flex items-center justify-center rounded-full active:bg-gray-100 cursor-pointer"
        @click="router.back()">
        <el-icon :size="20">
          <ArrowLeft />
        </el-icon>
      </div>
      <div class="flex flex-col items-center">
        <div class="font-bold text-lg text-gray-900">AI 智能助手</div>
        <div class="flex items-center space-x-1">
          <div class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></div>
          <span class="text-[10px] text-gray-400">在线</span>
        </div>
      </div>
      <div class="flex items-center gap-2">
        <div class="w-10 h-10 flex items-center justify-center rounded-full active:bg-purple-50 text-gray-400"
          @click="showHistoryPanel = true">
          <el-icon :size="20">
            <Clock />
          </el-icon>
        </div>
        <div class="w-10 h-10 flex items-center justify-center rounded-full active:bg-red-50 text-gray-400"
          @click="handleClear">
          <el-icon :size="20">
            <Delete />
          </el-icon>
        </div>
        <LangSwitcher />
      </div>
    </div>

    <!-- 历史记录侧边栏 -->
    <transition name="slide">
      <div v-if="showHistoryPanel" class="history-panel">
        <div class="history-header">
          <h3>对话历史</h3>
          <button class="close-btn" @click="showHistoryPanel = false">
            <el-icon :size="18"><Close /></el-icon>
          </button>
        </div>
        <div class="history-content">
          <div v-if="historyLoading" class="history-loading">
            <van-loading size="20px" />
            <span>加载中...</span>
          </div>
          <div v-else-if="chatHistory.length === 0" class="history-empty">
            <el-icon :size="32"><Document /></el-icon>
            <p>暂无历史记录</p>
          </div>
          <div v-else class="history-list">
            <div 
              class="history-item" 
              v-for="(record, index) in chatHistory" 
              :key="record.id || index"
            >
              <div class="history-time">{{ formatHistoryTime(record.createTime) }}</div>
              <div class="history-preview">{{ record.userMessage || '对话记录' }}</div>
            </div>
          </div>
        </div>
        <div class="history-footer">
          <button class="clear-history-btn" @click="handleClearHistory">
            <el-icon :size="16"><Delete /></el-icon>
            清空所有历史
          </button>
        </div>
      </div>
    </transition>
    <div v-if="showHistoryPanel" class="history-overlay" @click="showHistoryPanel = false"></div>

    <div class="chat-body-container">
      <div class="message-list-scroll px-4 py-4 space-y-4" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="index" class="flex flex-col">
          <div class="flex justify-center my-4">
            <div class="chat-tip-time">{{ msg.time || formatTime() }}</div>
          </div>

          <div :class="['flex w-full', msg.role === 'user' ? 'justify-end' : 'justify-start']">
            <div
              :class="['flex max-w-[85%] space-x-2', msg.role === 'user' ? 'flex-row-reverse space-x-reverse' : 'flex-row']">
              <div class="w-10 h-10 rounded-full flex-shrink-0 flex items-center justify-center shadow-sm"
                :class="msg.role === 'user' ? 'bg-teal-500' : 'bg-gradient-to-tr from-indigo-500 to-purple-400'">
                <el-icon v-if="msg.role === 'ai'" color="white" :size="22">
                  <MagicStick />
                </el-icon>
                <span v-else class="text-white text-xs font-bold">我</span>
              </div>
              <div
                :class="['px-4 py-3 rounded-2xl text-[15px] shadow-sm',
                  msg.role === 'user' ? 'bg-teal-500 text-white rounded-tr-none' : 'bg-white text-gray-800 rounded-tl-none border border-gray-100']">
                <div v-if="msg.loading" class="loading-dots flex space-x-1 py-1">
                  <span class="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce"></span>
                  <span class="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></span>
                  <span class="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.4s"></span>
                </div>
                <template v-else>{{ msg.content }}</template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="input-area-wrapper bg-white/80 backdrop-blur-lg border-t border-gray-100 shadow-[0_-2px_10px_rgba(0,0,0,0.02)] z-20">
      <div class="flex overflow-x-auto space-x-2 px-4 py-3 no-scrollbar">
        <div v-for="tag in quickTags" :key="tag" @click="handleTagClick(tag)"
          class="whitespace-nowrap px-3 py-1.5 bg-gray-50 text-gray-600 text-[11px] rounded-full border border-gray-100 active:bg-purple-50">
          {{ tag }}
        </div>
      </div>

      <div class="px-4 py-3 flex items-end space-x-2">
        <div
          class="flex-1 bg-gray-100/80 rounded-[1.25rem] px-4 py-2 flex items-center border border-gray-100 focus-within:border-teal-500 focus-within:bg-white transition-all">
          <textarea v-model="userInput" rows="1" placeholder="问问 AI 助手..."
            class="w-full bg-transparent border-none outline-none text-[15px] resize-none leading-relaxed py-1"
            @keydown.enter.prevent="handleSend"></textarea>
        </div>
        <button @click="handleSend" :disabled="!userInput.trim() || aiLoading"
          class="w-11 h-11 rounded-full flex items-center justify-center transition-all flex-shrink-0"
          :class="userInput.trim() ? 'bg-teal-500 text-white shadow-lg shadow-teal-100' : 'bg-gray-200 text-gray-400'">
          <el-icon v-if="!aiLoading" :size="20">
            <Promotion />
          </el-icon>
          <van-loading v-else size="18px" color="#fff" />
        </button>
      </div>
    </div>

    <div class="bottom-tab-placeholder"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, MagicStick, Delete, Promotion, Clock, Close, Document } from '@element-plus/icons-vue'
import { showToast, showConfirmDialog } from 'vant'
import request from '@/api/request'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { aiChatRecordApi } from '@/api/ai'

const router = useRouter()
const messageContainer = ref(null)
const userInput = ref('')
const aiLoading = ref(false)
const messages = ref([{ role: 'ai', content: '您好！我是您的智能管家，有什么可以帮您？', time: '12:00' }])
const quickTags = ['帮我写商品描述', '估算手机价格', '如何辨别真伪', '交易安全提醒']

// 历史记录相关
const showHistoryPanel = ref(false)
const historyLoading = ref(false)
const chatHistory = ref([])

// 加载历史记录
const loadChatHistory = async () => {
  historyLoading.value = true
  try {
    const res = await aiChatRecordApi.getHistory()
    if (res.code === '200' && res.data) {
      chatHistory.value = res.data
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  } finally {
    historyLoading.value = false
  }
}

// 清空历史记录
const handleClearHistory = async () => {
  try {
    await showConfirmDialog({ message: '确定清空所有历史记录吗？' })
    const res = await aiChatRecordApi.clearHistory()
    if (res.code === '200') {
      chatHistory.value = []
      showToast('已清空历史记录')
    } else {
      showToast('清空失败')
    }
  } catch (e) {
    // 用户取消
  }
}

// 格式化历史时间
const formatHistoryTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 监听历史面板打开
watch(showHistoryPanel, (val) => {
  if (val) loadChatHistory()
})

const handleSend = async () => {
  if (!userInput.value.trim() || aiLoading.value) return
  
  const content = userInput.value.trim()
  messages.value.push({ role: 'user', content, time: formatTime() })
  userInput.value = ''
  scrollToBottom()

  // 添加 AI 等待消息
  const aiMsgIndex = messages.value.length
  messages.value.push({
    role: 'ai',
    content: '正在思考...',
    loading: true,
    time: formatTime()
  })

  aiLoading.value = true
  try {
    const res = await request.post('/ai/generate', {
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
    messages.value[aiMsgIndex].content = '抱歉，服务出现了一点问题，请稍后再试。'
    messages.value[aiMsgIndex].loading = false
    showToast('发送失败')
    console.error('AI Chat Error:', error)
  } finally {
    aiLoading.value = false
    scrollToBottom()
  }
}

const handleTagClick = (tag) => { userInput.value = tag; handleSend() }
const handleClear = async () => {
  try { await showConfirmDialog({ message: '确定清空所有对话吗？' }); messages.value = [messages.value[0]]; } catch (e) { }
}

const scrollToBottom = () => {
  nextTick(() => { if (messageContainer.value) messageContainer.value.scrollTop = messageContainer.value.scrollHeight })
}
const formatTime = () => {
  const d = new Date(); return `${d.getHours()}:${d.getMinutes().toString().padStart(2, '0')}`
}
onMounted(scrollToBottom)
</script>

<style scoped>
/* 1. 基础容器：固定死，不随内容滚动 */
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  /* 针对 iOS 新版浏览器的动态视口高度 */
  width: 100vw;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
}

/* 2. 顶部导航：对齐 ChatView */
.top-nav-placeholder {
  height: calc(60px + env(safe-area-inset-top));
  padding-top: env(safe-area-inset-top);
  align-items: flex-end;
  padding-bottom: 10px;
  flex-shrink: 0;
  /* 绝对不能被压缩 */
}

/* 3. 中间滚动区：占据剩余所有空间 */
.chat-body-container {
  flex: 1;
  overflow: hidden;
  /* 这一层禁止滚动 */
  display: flex;
  flex-direction: column;
}

.message-list-scroll {
  flex: 1;
  overflow-y: auto;
  /* 只有这一层负责滚动 */
  -webkit-overflow-scrolling: touch;
}

/* 4. 底部输入区：固定在底部，自动处理安全距离 */
.input-area-wrapper {
  flex-shrink: 0;
}

.bottom-tab-placeholder {
  height: calc(82px + env(safe-area-inset-bottom));
  flex-shrink: 0;
}

/* 处理 iOS 底部黑条的填充 */
.pb-safe {
  padding-bottom: max(1rem, env(safe-area-inset-bottom));
}

.chat-tip-time {
  background-color: #f1f5f9;
  color: #94a3b8;
  font-size: 10px;
  padding: 2px 10px;
  border-radius: 100px;
}

.no-scrollbar::-webkit-scrollbar {
  display: none;
}

/* 历史记录面板样式 */
.history-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 280px;
  height: 100vh;
  background: white;
  z-index: 30;
  display: flex;
  flex-direction: column;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1);
}

.history-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  z-index: 25;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.history-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.history-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.history-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #6b7280;
}

.history-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #9ca3af;
}

.history-empty p {
  font-size: 14px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.history-item:hover {
  background: #f3f4f6;
}

.history-time {
  font-size: 12px;
  color: #9ca3af;
}

.history-preview {
  font-size: 14px;
  color: #374151;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-footer {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

.clear-history-btn {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #fee2e2;
  background: #fee2e2;
  color: #ef4444;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s;
}

.clear-history-btn:hover {
  background: #ef4444;
  color: white;
}

.slide-enter-active, .slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from, .slide-leave-to {
  transform: translateX(100%);
}
</style>