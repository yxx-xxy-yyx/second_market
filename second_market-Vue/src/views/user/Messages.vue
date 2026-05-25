<template>
  <div class="messages-page">
    <div class="page-container">
      <div class="page-header">
        <h2 class="page-title">{{ $t('messages.pageTitle') }}</h2>
      </div>

      <div class="messages-content">
        <el-row :gutter="20">
          <!-- 左侧消息列表 40% -->
          <el-col :xs="24" :sm="24" :md="10" :lg="10">
            <el-card class="message-list-card" shadow="hover">
              <template #header>
                <div class="list-header">
                  <div class="main-tabs">
                    <div
                      :class="['main-tab', { active: activeTab === 'notifications' }]"
                      @click="handleTabChange('notifications')"
                    >
                      {{ $t('messages.notifications') }}
                    </div>
                    <div
                      :class="['main-tab', { active: activeTab === 'chats' }]"
                      @click="handleTabChange('chats')"
                    >
                      {{ $t('messages.chats') }}
                    </div>
                  </div>
                  <el-button
                    v-if="activeTab === 'notifications'"
                    type="primary"
                    text
                    size="small"
                    @click="handleMarkAllRead"
                    :disabled="unreadCount === 0"
                  >
                    {{ $t('messages.markAllRead') }}
                  </el-button>
                </div>
                <div class="filter-tabs" v-if="activeTab === 'notifications'">
                  <div
                    v-for="filter in filterList"
                    :key="filter.value"
                    :class="['filter-tab', { active: currentFilter === filter.value }]"
                    @click="handleFilterChange(filter.value)"
                  >
                    {{ filter.label }}
                  </div>
                </div>
              </template>

              <div class="message-list" v-loading="loading">
                <el-empty
                  v-if="!loading && ((activeTab === 'notifications' && messages.length === 0) || (activeTab === 'chats' && chatList.length === 0))"
                  :description="$t('common.noData')"
                />

                <!-- 通知列表 -->
                <template v-if="activeTab === 'notifications'">
                  <div
                    v-for="message in messages"
                    :key="message.id"
                    :class="['message-item', { active: selectedMessage?.id === message.id, unread: !message.isRead }]"
                    @click="handleMessageClick(message)"
                  >
                    <div class="message-icon">
                      <el-icon :size="24" :color="getMessageColor(message.type)">
                        <component :is="getMessageIcon(message.type)" />
                      </el-icon>
                    </div>
                    <div class="message-content">
                      <div class="message-title">{{ message.displayTitle }}</div>
                      <div class="message-time">{{ formatTime(message.createTime) }}</div>
                    </div>
                    <div class="unread-badge" v-if="!message.isRead"></div>
                  </div>
                </template>

                <!-- 私聊列表 -->
                <template v-else>
                  <div
                    v-for="chat in chatList"
                    :key="chat.targetUserId"
                    :class="['message-item', 'chat-item', { active: chatTarget.userId === chat.targetUserId }]"
                    @click="handleChatClick(chat)"
                  >
                    <div class="chat-avatar">
                      <el-avatar :size="40" :src="formatAvatar(chat.targetUserAvatar)">
                        <el-icon><User /></el-icon>
                      </el-avatar>
                      <div class="unread-dot" v-if="chat.unreadCount > 0">{{ chat.unreadCount }}</div>
                    </div>
                    <div class="message-content">
                      <div class="message-title-row">
                        <span class="message-title">{{ chat.targetUserNickname }}</span>
                        <span class="message-time">{{ formatTime(chat.lastTime) }}</span>
                      </div>
                      <div class="message-preview">{{ chat.lastContent }}</div>
                    </div>
                  </div>
                </template>

                <div class="pagination-wrapper" v-if="activeTab === 'notifications' && total > pageSize">
                  <el-pagination
                    v-model:current-page="pageNum"
                    :page-size="pageSize"
                    :total="total"
                    layout="prev, pager, next"
                    small
                    @current-change="handlePageChange"
                  />
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 右侧消息详情 60% -->
          <el-col :xs="24" :sm="24" :md="14" :lg="14">
            <el-card class="message-detail-card" shadow="hover">
              <template #header>
                <div class="detail-header">
                  <div class="header-left">
                    <el-icon v-if="isChatMode"><ChatDotRound /></el-icon>
                    <el-icon v-else><Bell /></el-icon>
                    <span>{{ isChatMode ? $t('messages.chattingWith', { username: chatTarget.username }) : $t('messages.messageDetail') }}</span>
                  </div>
                  <div class="header-actions" v-if="selectedMessage && !isChatMode">
                    <el-tag :type="getMessageTypeTag(selectedMessage.type)" size="small">
                      {{ getMessageTypeLabel(selectedMessage.type) }}
                    </el-tag>
                  </div>
                </div>
              </template>

              <!-- 私聊模式 -->
              <div class="chat-container" v-if="isChatMode">
                <div class="chat-messages-box" ref="chatBox">
                  <div class="chat-tip">{{ $t('messages.chatPrivacyTip') }}</div>
                  <div v-if="chatMessages.length === 0" class="empty-chat">
                    {{ $t('messages.startChat') }}
                  </div>
                  <div v-for="(msg, index) in chatMessages" :key="index" :class="['chat-msg-item', String(msg.fromId) === String(userStore.user?.id) ? 'sent' : 'received']">
                    <el-avatar :size="32" :src="String(msg.fromId) === String(userStore.user?.id) ? formatAvatar(userStore.user?.avatar) : formatAvatar(chatTarget.avatar)" />
                    <div class="chat-msg-content">
                      <div class="chat-msg-bubble">{{ msg.content }}</div>
                      <div class="chat-msg-time">{{ formatTime(msg.createTime) }}</div>
                    </div>
                  </div>
                </div>
                <div class="chat-input-box">
                  <el-input
                    v-model="chatInput"
                    type="textarea"
                    :rows="3"
                    :placeholder="$t('messages.inputMessage')"
                    resize="none"
                    @keydown.enter.prevent="handleSendChat"
                  />
                  <div class="chat-actions">
                    <el-button type="primary" :loading="sending" @click="handleSendChat">
                      {{ $t('messages.send') }}
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 详情模式 -->
              <div class="message-detail" v-else-if="selectedMessage">
                <el-card class="detail-card" shadow="never">
                  <div class="detail-title-section">
                    <h3 class="detail-title">{{ selectedMessage.displayTitle }}</h3>
                    <div class="detail-meta">
                      <el-icon><Clock /></el-icon>
                      <span>{{ formatTime(selectedMessage.createTime) }}</span>
                    </div>
                  </div>
                  
                  <el-divider />
                  
                  <div class="detail-content-wrapper">
                    <div class="content-label">{{ $t('messages.messageContent') }}</div>
                    <div class="detail-content" v-html="selectedMessage.displayContent"></div>
                  </div>

                  <el-divider />
                  
                  <div class="detail-actions">
                    <el-button v-if="selectedMessage.link" type="primary" size="small" @click="openLink(selectedMessage.link)">{{ $t('common.viewDetail') }}</el-button>
                    <el-button type="primary" plain size="small" @click="handleMarkRead(selectedMessage.id)" v-if="!selectedMessage.isRead">
                      <el-icon><CircleCheck /></el-icon>
                      {{ $t('messages.markAsRead') }}
                    </el-button>
                    <el-button type="danger" text size="small" @click="handleDeleteMessage(selectedMessage.id)">
                      <el-icon><Delete /></el-icon>
                      {{ $t('messages.deleteMessage') }}
                    </el-button>
                  </div>
                </el-card>
              </div>

              <el-empty
                v-else
                :description="$t('messages.selectMessageToView')"
                :image-size="120"
              >
                <template #image>
                  <el-icon :size="80" color="#909399"><ChatDotRound /></el-icon>
                </template>
              </el-empty>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  ShoppingCart,
  Star,
  Clock,
  CircleCheck,
  Delete,
  ChatDotRound
} from '@element-plus/icons-vue'
import { messageApi } from '@/api/message'
import { chatApi } from '@/api/chat'
import { formatAvatarUrl } from '@/utils/url'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t, locale } = useI18n()

const loading = ref(false)
const sending = ref(false)
const messages = ref([])
const selectedMessage = ref(null)
const currentFilter = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const unreadCount = ref(0)
const activeTab = ref('notifications') // 'notifications' | 'chats'

// 私聊相关
const isChatMode = ref(false)
const chatList = ref([])
const chatTarget = ref({
  userId: null,
  username: '',
  avatar: ''
})
const chatMessages = ref([])
const chatInput = ref('')
const chatBox = ref(null)
let pollingInterval = null

const filterList = computed(() => {
  if (activeTab.value === 'notifications') {
    return [
      { label: t('common.all'), value: null },
      { label: t('messages.unread'), value: 0 },
      { label: t('messages.read'), value: 1 }
    ]
  } else {
    return []
  }
})

const handleTabChange = (tab) => {
  activeTab.value = tab
  selectedMessage.value = null
  isChatMode.value = false
  if (tab === 'notifications') {
    pageNum.value = 1
    loadMessages()
  } else {
    loadChatList()
  }
}

const escapeHtml = (value) => {
  if (value === null || value === undefined) return ''
  return String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

const extractProductName = (text) => {
  if (!text) return ''
  const str = String(text)
  const m1 = str.match(/"([^"]+)"/)
  if (m1?.[1]) return m1[1]
  const m2 = str.match(/“([^”]+)”/)
  if (m2?.[1]) return m2[1]
  return ''
}

const extractRating = (text) => {
  if (!text) return ''
  const str = String(text)
  const m = str.match(/(\d)\s*星/)
  return m?.[1] || ''
}

const toSafeHtml = (text) => {
  return escapeHtml(text).replace(/\n/g, '<br/>')
}

const extractRouteLink = (text) => {
  if (!text) return ''
  const str = String(text)
  const m = str.match(/(\/user\/[A-Za-z0-9/_-]+)/)
  return m?.[1] || ''
}

const openLink = (path) => {
  if (!path) return
  router.push(path)
}

const localizeMessage = (msg) => {
  const rawTitle = msg?.title || ''
  const rawContent = msg?.content || ''

  let displayTitle = rawTitle
  let displayContent = rawContent
  const link = extractRouteLink(rawContent)

  if (rawTitle === '订单支付成功') {
    displayTitle = t('messages.templates.orderPaidTitle')
    const product = extractProductName(rawContent)
    displayContent = toSafeHtml(
      product
        ? t('messages.templates.orderPaidContent', { product })
        : t('messages.templates.orderPaidContentNoProduct')
    )
  } else if (rawTitle === '欢迎注册智能二手商城') {
    displayTitle = t('messages.templates.welcomeTitle')
    displayContent = toSafeHtml(t('messages.templates.welcomeContent'))
  } else if (rawTitle === '您的商品已售出') {
    displayTitle = t('messages.templates.productSoldTitle')
    const product = extractProductName(rawContent)
    displayContent = toSafeHtml(
      product
        ? t('messages.templates.productSoldContent', { product })
        : t('messages.templates.productSoldContentNoProduct')
    )
  } else if (rawTitle === '收到新评价') {
    displayTitle = t('messages.templates.newReviewTitle')
    const product = extractProductName(rawContent)
    const rating = extractRating(rawContent)
    displayContent = toSafeHtml(
      product && rating
        ? t('messages.templates.newReviewContent', { product, rating })
        : t('messages.templates.newReviewContentGeneric')
    )
  } else {
    displayContent = toSafeHtml(rawContent)
  }

  return {
    ...msg,
    displayTitle,
    displayContent,
    link
  }
}

// 初始化
onMounted(async () => {
  // 检查是否有跳转参数
  const { userId, username, avatar, tab } = route.query
  if (tab === 'chats' || userId) {
    activeTab.value = 'chats'
    await loadChatList()
    if (userId) {
      isChatMode.value = true
      chatTarget.value = { userId: Number(userId), username, avatar }
      await fetchChatHistory(userId)
      startPolling()
    }
  } else {
    await loadMessages()
  }
})

onUnmounted(() => {
  stopPolling()
})

const startPolling = () => {
  stopPolling()
  pollingInterval = setInterval(() => {
    if (isChatMode.value && chatTarget.value.userId) {
      fetchChatHistory(chatTarget.value.userId, true)
    }
  }, 3000)
}

const stopPolling = () => {
  if (pollingInterval) {
    clearInterval(pollingInterval)
    pollingInterval = null
  }
}

// 获取私聊消息列表
const loadChatList = async () => {
  loading.value = true
  try {
    const res = await chatApi.getChatList()
    if (res.code === '200') {
      chatList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error(t('messages.getMessagesFailed'))
  } finally {
    loading.value = false
  }
}

// 获取与特定用户的聊天记录
const fetchChatHistory = async (userId, isPolling = false) => {
  try {
    const res = await chatApi.getChatHistory(userId)
    if (res.code === '200' || res.success) {
      const newMessages = (res.data || []).map(m => ({
        ...m,
        fromId: m.senderId
      }))
      
      // 只有当消息内容发生变化（如新消息增加）时才更新
      // 检查最后一条消息的内容和时间是否一致，或者长度增加
      const hasNewMessages = newMessages.length > chatMessages.value.length
      const lastMsgChanged = newMessages.length > 0 && chatMessages.value.length > 0 && 
                            (newMessages[newMessages.length - 1].id !== chatMessages.value[chatMessages.value.length - 1].id)

      if (hasNewMessages || lastMsgChanged || (chatMessages.value.length === 0 && newMessages.length > 0)) {
        chatMessages.value = newMessages
        await nextTick()
        scrollToBottom()
      }
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error)
  }
}

const handleChatClick = async (chat) => {
  isChatMode.value = true
  chatTarget.value = {
    userId: chat.targetUserId,
    username: chat.targetUserNickname,
    avatar: chat.targetUserAvatar
  }
  chatMessages.value = [] // 切换时先清空，避免看到旧消息
  await fetchChatHistory(chat.targetUserId)
  if (chat.unreadCount > 0) {
    await markChatRead(chat.targetUserId)
    chat.unreadCount = 0
  }
  startPolling()
}

const markChatRead = async (senderId) => {
  try {
    await chatApi.markAsRead(senderId)
  } catch (error) {
    console.error('标记聊天已读失败:', error)
  }
}

const scrollToBottom = () => {
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

const handleSendChat = async () => {
  if (!chatInput.value.trim() || sending.value) return
  
  const content = chatInput.value
  sending.value = true
  try {
    const res = await chatApi.sendMessage({
      receiverId: chatTarget.value.userId,
      content: content,
      msgType: 0 // 0: 文本
    })
    
    if (res.code === '200' || res.success) {
      chatInput.value = ''
      // 发送成功后立即获取最新记录，而不是手动推送到数组，避免 id 缺失
      await fetchChatHistory(chatTarget.value.userId)
    }
  } catch (error) {
    ElMessage.error(t('messages.sendFailed'))
  } finally {
    sending.value = false
  }
}

const formatAvatar = (avatar) => {
  return formatAvatarUrl(avatar)
}

const getMessageIcon = (type) => {
  const iconMap = {
    1: Bell,
    2: ShoppingCart,
    3: Star,
    101: ChatDotRound,
    102: Star,
    103: Bell
  }
  return iconMap[type] || Bell
}

const getMessageColor = (type) => {
  const colorMap = {
    1: '#409eff',
    2: '#67c23a',
    3: '#e6a23c',
    101: '#36B3C2',
    102: '#36B3C2',
    103: '#36B3C2'
  }
  return colorMap[type] || '#909399'
}

const getMessageTypeTag = (type) => {
  const tags = {
    1: 'primary',
    2: 'success',
    3: 'warning',
    101: 'info',
    102: 'info',
    103: 'info'
  }
  return tags[type] || 'info'
}

const getMessageTypeLabel = (type) => {
  const labels = {
    1: t('messages.systemNotification'),
    2: t('messages.orderMessage'),
    3: t('messages.platformAnnouncement'),
    101: '论坛评论',
    102: '论坛点赞',
    103: '@提及'
  }
  return labels[type] || t('messages.other')
}

const handleMarkRead = async (id) => {
  try {
    const res = await messageApi.markAsRead(id)
    if (res.code === '200' || res.success) {
      ElMessage.success(t('messages.markedAsRead'))
      if (selectedMessage.value?.id === id) {
        selectedMessage.value.isRead = true
      }
      await loadMessages()
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error(t('messages.markAsReadFailed'))
  }
}

const handleDeleteMessage = async (id) => {
  try {
    await ElMessageBox.confirm(
      t('messages.confirmDeleteMessage'), 
      t('messages.confirmDelete'), 
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    
    await messageApi.deleteMessage(id)
    ElMessage.success(t('messages.deleteSuccess'))
    selectedMessage.value = null
    await loadMessages()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('messages.deleteFailed'))
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) {
    return t('messages.justNow')
  } else if (diff < 3600000) {
    return t('messages.minutesAgo', { count: Math.floor(diff / 60000) })
  } else if (diff < 86400000) {
    return t('messages.hoursAgo', { count: Math.floor(diff / 3600000) })
  } else if (diff < 604800000) {
    return t('messages.daysAgo', { count: Math.floor(diff / 86400000) })
  } else {
    return date.toLocaleDateString(locale.value === 'zh' ? 'zh-CN' : locale.value === 'en' ? 'en-US' : 'ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }
}

const loadMessages = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    if (currentFilter.value !== null) {
      params.isRead = currentFilter.value
    }

    const res = await messageApi.getMyMessages(params)

    if (res.code === '200' || res.success) {
      messages.value = (res.data.records || []).map(localizeMessage)
      total.value = res.data.total || 0

      if (messages.value.length > 0 && !selectedMessage.value) {
        selectedMessage.value = messages.value[0]
        if (!messages.value[0].isRead) {
          markAsRead(messages.value[0].id)
        }
      }

      loadUnreadCount()
    } else {
      throw new Error(res.message || t('messages.getMessagesFailed'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('messages.getMessagesFailed'))
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    const [msgRes, chatRes] = await Promise.all([
      messageApi.getUnreadCount(),
      chatApi.getUnreadCount()
    ])
    
    let total = 0
    if (msgRes.code === '200' || msgRes.success) total += (msgRes.data || 0)
    if (chatRes.code === '200' || chatRes.success) total += (chatRes.data || 0)
    
    unreadCount.value = total
  } catch (error) {
    console.error('获取未读数量失败:', error)
  }
}

const handleFilterChange = (filter) => {
  currentFilter.value = filter
  pageNum.value = 1
  selectedMessage.value = null
  loadMessages()
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadMessages()
}

const handleMessageClick = async (message) => {
  isChatMode.value = false // 切换回普通消息详情模式
  selectedMessage.value = message

  if (!message.isRead) {
    await markAsRead(message.id)
    message.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const markAsRead = async (id) => {
  try {
    await messageApi.markAsRead(id)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm(
      t('messages.confirmMarkAllRead'),
      t('messages.markAllReadTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'info'
      }
    )

    const res = await messageApi.markAllAsRead()

    if (res.code === '200' || res.success) {
      ElMessage.success(t('messages.markAllReadSuccess'))
      messages.value.forEach(msg => {
        msg.isRead = true
      })
      unreadCount.value = 0
    } else {
      throw new Error(res.message || t('messages.markFailed'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || t('messages.markFailed'))
    }
  }
}

onMounted(() => {
  loadMessages()
})

watch(locale, () => {
  messages.value = messages.value.map(localizeMessage)
  if (selectedMessage.value) {
    selectedMessage.value = localizeMessage(selectedMessage.value)
  }
})
</script>

<style scoped>
.messages-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 20px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #fafbfc 100%);
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.messages-content {
  min-height: 600px;
}

.message-list-card,
.message-detail-card {
  min-height: 600px;
}

.list-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.main-tabs {
  display: flex;
  background: #f4f4f5;
  padding: 4px;
  border-radius: 8px;
}

.main-tab {
  flex: 1;
  text-align: center;
  padding: 6px 0;
  cursor: pointer;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}

.main-tab.active {
  background: #fff;
  color: #409eff;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.filter-tabs {
  display: flex;
  gap: 15px;
}

.filter-tab {
  padding: 6px 16px;
  border-radius: 16px;
  background: #f5f7fa;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.filter-tab:hover {
  background: #e6f0ff;
  color: #409eff;
}

.filter-tab.active {
  background: #409eff;
  color: #fff;
}

.message-list {
  min-height: 500px;
}

.message-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  border: 2px solid transparent;
  margin-bottom: 8px;
}

.chat-item {
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  position: relative;
}

.unread-dot {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  border: 2px solid #fff;
  min-width: 18px;
  text-align: center;
}

.message-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.message-preview {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.message-item:hover {
  background: #f0f7ff;
}

.message-item.active {
  border-color: #409eff;
  background: #f0f7ff;
}

.message-item.unread .message-title {
  font-weight: 700;
}

.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 12px;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.unread-badge {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #f56c6c;
  flex-shrink: 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.detail-header {
  font-weight: 600;
  font-size: 16px;
}

.message-detail {
  padding: 20px 0;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
}

.detail-time {
  font-size: 14px;
  color: #909399;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-content {
  font-size: 15px;
  line-height: 1.6;
  color: #606266;
}

.detail-content :deep(p) {
  margin-bottom: 12px;
}

.detail-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
  margin: 12px 0;
}

@media (max-width: 768px) {
  .messages-page {
    padding: 16px;
  }

  .messages-content .el-col {
    margin-bottom: 20px;
  }

  .message-list-card,
  .message-detail-card {
    min-height: 400px;
  }
}

/* 新增样式 */
.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-card {
  border: none;
  background: #f8f9fa;
}

.detail-title-section {
  margin-bottom: 16px;
}

.detail-title-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.detail-content-wrapper {
  margin: 16px 0;
}

.content-label {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 12px;
}

.detail-actions {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.message-item {
  transition: all 0.3s ease;
}

.message-item:hover {
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.chat-messages-box {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: #fcfcfd;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.chat-tip {
  text-align: center;
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 4px 12px;
  border-radius: 4px;
  align-self: center;
}

.empty-chat {
  text-align: center;
  margin-top: 50px;
  color: #c0c4cc;
  font-size: 14px;
}

.chat-msg-item {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.chat-msg-item.sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.chat-msg-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.chat-msg-bubble {
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-all;
}

.sent .chat-msg-bubble {
  background: #409eff;
  color: white;
  border-top-right-radius: 2px;
}

.received .chat-msg-bubble {
  background: white;
  border: 1px solid #ebeef5;
  color: #303133;
  border-top-left-radius: 2px;
}

.chat-msg-time {
  font-size: 10px;
  color: #909399;
}

.sent .chat-msg-time {
  text-align: right;
}

.chat-input-box {
  padding: 15px;
  border-top: 1px solid #ebeef5;
  background: white;
}

.chat-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.message-detail-card :deep(.el-card__body) {
  padding: 0;
}

.message-detail {
  padding: 20px;
}
</style>
