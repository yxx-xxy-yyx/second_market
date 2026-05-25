<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import {
  ChatBubbleOvalLeftEllipsisIcon
} from '@heroicons/vue/24/outline'
import { Notification } from '@element-plus/icons-vue' // 引入系统图标
import { useUserStore } from '@/stores/user'
import { chatApi } from '@/api/chat'

const router = useRouter()
const userStore = useUserStore()
const chatList = ref([])
const loading = ref(true)
let pollingInterval = null

// MessageView.vue 逻辑保持不变
const fetchChatList = async () => {
  try {
    const res = await chatApi.getChatList()
    if (res.code == '200' || res.success) {
      chatList.value = res.data || []
    } else {
      chatList.value = []
    }
  } catch (error) {
    chatList.value = []
  } finally {
    loading.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()

  if (date.toDateString() === now.toDateString()) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  }

  if (date.getFullYear() === now.getFullYear()) {
    return `${date.getMonth() + 1}/${date.getDate()}`
  }

  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
}

const handleChat = (targetUserId) => {
  if (!targetUserId) {
    showToast('用户 ID 丢失');
    return;
  }
  router.push({
    path: `/user/chat/${targetUserId}`
  });
};

onMounted(() => {
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  fetchChatList()
  pollingInterval = setInterval(fetchChatList, 5000) // 5秒轮询一次列表
})

onUnmounted(() => {
  if (pollingInterval) {
    clearInterval(pollingInterval)
  }
})
</script>

<template>
  <div class="message-page-wrapper bg-[#f8fafc]">
    <div class="message-list-container flex-1 overflow-y-auto">
      <div v-if="loading" class="flex justify-center py-20">
        <van-loading type="spinner" color="#14b8a6" />
      </div>

      <div v-else class="px-4 pt-4 pb-4 space-y-3">
        <div
          class="bg-white rounded-2xl p-2 flex items-center space-x-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 hover:shadow-md cursor-pointer group"
          @click="router.push('/user/notices')">
          <div class="relative">
            <div
              class="w-12 h-12 rounded-full bg-orange-500 flex items-center justify-center shadow-lg shadow-orange-100 flex-shrink-0 group-hover:bg-orange-600 transition-colors">
              <el-icon :size="26" color="#ffffff">
                <Notification />
              </el-icon>
            </div>
            <div
              class="absolute -top-0.5 -right-0.5 bg-red-500 w-3.5 h-3.5 rounded-full border-2 border-white shadow-sm">
            </div>
          </div>

          <div class="flex-1 min-w-0 py-1">
            <div class="flex justify-between items-baseline mb-1.5">
              <h3 class="font-bold text-gray-900 text-[14px] truncate group-hover:text-blue-600 transition-colors">公告通知
              </h3>
              <span
                class="text-[10px] text-orange-600 font-bold bg-orange-50 px-1.5 py-0.5 rounded flex-shrink-0">官方</span>
            </div>
            <p class="text-sm text-gray-500 truncate leading-relaxed">
              点击查看平台最新公告与重要通知
            </p>
          </div>
        </div>
        <div
          class="bg-white rounded-2xl p-2 flex items-center space-x-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 hover:shadow-md cursor-pointer group"
          @click="router.push('/user/system-messages')">
          <div class="relative">
            <div
              class="w-12 h-12 rounded-full bg-blue-500 flex items-center justify-center shadow-lg shadow-blue-100 flex-shrink-0 group-hover:bg-blue-600 transition-colors">
              <el-icon :size="26" color="#ffffff">
                <Notification />
              </el-icon>
            </div>
            <div
              class="absolute -top-0.5 -right-0.5 bg-red-500 w-3.5 h-3.5 rounded-full border-2 border-white shadow-sm">
            </div>
          </div>

          <div class="flex-1 min-w-0 py-1">
            <div class="flex justify-between items-baseline mb-1.5">
              <h3 class="font-bold text-gray-900 text-[14px] truncate group-hover:text-blue-600 transition-colors">系统消息
              </h3>
              <span class="text-[10px] text-blue-500 font-bold bg-blue-50 px-1.5 py-0.5 rounded flex-shrink-0">官方</span>
            </div>
            <p class="text-sm text-gray-500 truncate leading-relaxed">
              点击查看系统通知、账户安全及官方公告
            </p>
          </div>
        </div>

        <div
          class="bg-white rounded-2xl p-2 flex items-center space-x-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 hover:shadow-md cursor-pointer group"
          @click="router.push('/user/ai-chat')">
          <div class="relative">
            <div
              class="w-12 h-12 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-400 flex items-center justify-center shadow-lg shadow-purple-100 flex-shrink-0">
              <el-icon :size="28" color="#ffffff">
                <MagicStick />
              </el-icon>
            </div>
            <div
              class="absolute -bottom-0.5 -right-0.5 bg-green-500 w-3.5 h-3.5 rounded-full border-2 border-white shadow-sm">
            </div>
          </div>

          <div class="flex-1 min-w-0 py-1">
            <div class="flex justify-between items-baseline mb-1.5">
              <h3 class="font-bold text-gray-900 text-[14px] group-hover:text-purple-600 transition-colors">AI 智能助手</h3>
              <span
                class="text-[10px] text-purple-500 font-bold bg-purple-50 px-1.5 py-0.5 rounded flex-shrink-0">在线</span>
            </div>
            <p class="text-sm text-gray-500 truncate leading-relaxed">
              我是您的智能管家，有什么可以帮您？
            </p>
          </div>
        </div>

        <div v-if="chatList.length === 0" class="flex flex-col items-center justify-center py-32 text-gray-400">
          <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
            <ChatBubbleOvalLeftEllipsisIcon class="w-10 h-10 text-gray-300" />
          </div>
          <p class="text-sm font-medium text-gray-500">暂无个人消息</p>
          <p class="text-xs text-gray-400 mt-1">快去和卖家聊聊吧</p>
        </div>

        <div v-for="chat in chatList" :key="chat.targetUserId"
          class="bg-white rounded-2xl p-2 flex items-center space-x-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 hover:shadow-md cursor-pointer group"
          @click="handleChat(chat.targetUserId)">
          <div class="relative">
            <div
              class="w-12 h-12 rounded-full bg-gray-50 overflow-hidden border border-gray-100 group-hover:border-teal-100 transition-colors">
              <img v-if="chat.targetUserAvatar" :src="chat.targetUserAvatar" class="w-full h-full object-cover" />
              <div v-else
                class="w-full h-full flex items-center justify-center text-gray-300 text-xl font-bold bg-gray-50">
                {{ chat.targetUserNickname?.charAt(0).toUpperCase() }}
              </div>
            </div>
            <div v-if="chat.unreadCount > 0"
              class="absolute -top-0.5 -right-0.5 bg-gradient-to-r from-red-500 to-pink-500 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full min-w-[18px] text-center border-2 border-white shadow-sm">
              {{ chat.unreadCount > 99 ? '99+' : chat.unreadCount }}
            </div>
          </div>

          <div class="flex-1 min-w-0 py-1">
            <div class="flex justify-between items-baseline mb-1.5">
              <h3 class="font-bold text-gray-900 text-[14px] truncate group-hover:text-teal-600 transition-colors">{{
                chat.targetUserNickname }}</h3>
              <span class="text-xs text-gray-400 font-medium flex-shrink-0">{{ formatTime(chat.lastTime) }}</span>
            </div>
            <p class="text-sm text-gray-500 truncate leading-relaxed">
              <span v-if="chat.msgType === 1" class="text-teal-600 font-medium flex items-center">
                <span class="mr-1">📷</span> [图片]
              </span>
              <span v-else class="group-hover:text-gray-600 transition-colors">{{ chat.lastContent }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-tab-placeholder"></div>
  </div>
</template>

<style scoped>
/* 样式部分完全保留原版 */
.message-page-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  position: relative;
  padding-top: calc(65px + env(safe-area-inset-top));
  /* Ensure content starts below the header */
  padding-bottom: calc(65px + env(safe-area-inset-bottom));
}

.top-nav-placeholder {
  display: none;
}

.message-list-container {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.bottom-tab-placeholder {
  height: calc(65px + env(safe-area-inset-bottom));
  flex-shrink: 0;
}

:deep(body),
:deep(html) {
  overflow: hidden !important;
  height: 100%;
}
</style>
