<template>
  <div class="chat-wrapper bg-[#f8fafc]">
    <div
      class="top-nav-placeholder bg-white/80 backdrop-blur-xl px-4 flex items-center justify-between border-b border-gray-100 z-20">
      <div class="w-10 h-10 flex items-center justify-center rounded-full active:bg-gray-100 cursor-pointer"
        @click="handleBack">
        <el-icon :size="20">
          <ArrowLeft />
        </el-icon>
      </div>
      <div class="flex flex-col items-center">
        <div class="font-bold text-lg text-gray-900">{{ targetUser.nickname || '聊天' }}</div>
        <div class="flex items-center space-x-1">
          <div class="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></div>
          <span class="text-[10px] text-gray-400">在线</span>
        </div>
      </div>
      <div class="flex items-center">
        <LangSwitcher />
        <div class="w-10"></div>
      </div>
    </div>

    <div class="chat-body-container flex flex-col overflow-hidden">
      <div v-if="goodsInfo" class="px-4 py-2 flex-shrink-0 z-10">
        <div class="bg-white/90 p-3 rounded-2xl shadow-sm border border-white/50 flex items-center space-x-3">
          <div class="w-12 h-12 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0">
            <img :src="goodsInfo.image" class="w-full h-full object-cover" />
          </div>
          <div class="flex-1 min-w-0 text-left">
            <h3 class="text-sm font-bold text-gray-900 truncate">{{ goodsInfo.title }}</h3>
            <span class="text-orange-500 font-bold text-sm">¥{{ goodsInfo.price }}</span>
          </div>
          <button class="px-3 py-1.5 bg-teal-50 text-teal-600 text-xs font-bold rounded-full"
            @click="router.push(`/user/product/${goodsInfo.id}`)">查看</button>
        </div>
      </div>

      <div class="message-scroll-area flex-1 overflow-y-auto p-4" ref="messageContainer">
        <div v-for="(msg, index) in messages" :key="msg.id || index">

          <div v-if="index === 0 || new Date(msg.createTime) - new Date(messages[index - 1].createTime) > 300000"
            class="flex justify-center mb-6 mt-2">
            <span class="text-[10px] text-gray-400 bg-gray-100/80 px-2.5 py-0.5 rounded-full">
              {{ formatTime(msg.createTime) }}
            </span>
          </div>

          <div class="flex w-full mb-6 items-end" :class="isMyMessage(msg) ? 'justify-end' : 'justify-start'">

            <div v-if="!isMyMessage(msg)"
              class="w-9 h-9 rounded-full flex-shrink-0 overflow-hidden border border-gray-100 mr-2">
              <img :src="formatAvatarUrl(targetUser.avatar)"
                class="w-full h-full object-cover" />
            </div>

            <div class="max-w-[75%] flex flex-col" :class="isMyMessage(msg) ? 'items-end' : 'items-start'">
              <div class="px-4 py-2.5 text-[15px] shadow-sm break-words relative"
                :class="[isMyMessage(msg) ? 'bg-teal-500 text-white rounded-2xl rounded-br-sm' : 'bg-white text-gray-800 rounded-2xl rounded-bl-sm border border-gray-100']">
                <img v-if="msg.type === 1" :src="getMessageImageUrl(msg.content)"
                  class="max-w-full rounded-lg cursor-pointer"
                  @click="showImagePreview([getMessageImageUrl(msg.content)])" />
                <span v-else>{{ msg.content }}</span>
              </div>
            </div>

            <div v-if="isMyMessage(msg)"
              class="w-9 h-9 rounded-full flex-shrink-0 overflow-hidden border border-gray-100 ml-2">
              <img :src="getMyAvatar()" class="w-full h-full object-cover"
                @error="(e) => e.target.src = DEFAULT_AVATAR" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="input-section-fixed bg-white/80 px-4 py-3 border-t border-gray-100 flex items-end space-x-3 z-20">
      <button type="button"
        class="relative w-11 h-11 flex items-center justify-center rounded-full bg-gray-100 text-gray-500 active:bg-teal-50 active:text-teal-600 transition-colors overflow-hidden"
        :disabled="uploadingImage" @click="triggerImagePicker">
        <el-icon :size="24">
          <Picture />
        </el-icon>
        <input ref="imageInputRef" type="file" accept="image/*" class="chat-image-input" @click.stop
          @change="handleImageInputChange" />
      </button>

      <div class="flex-1 bg-gray-100/80 rounded-[1.25rem] px-4 py-2.5">
        <textarea v-model="content" rows="1" placeholder="发消息..."
          class="w-full bg-transparent border-none outline-none text-[15px] resize-none max-h-24 leading-relaxed"
          @keydown.enter.prevent="sendMessage(0)"></textarea>
      </div>

      <button class="w-11 h-11 flex items-center justify-center rounded-full transition-all flex-shrink-0"
        :class="content.trim() ? 'bg-teal-500 text-white shadow-lg shadow-teal-100' : 'bg-gray-200 text-gray-400'"
        @click="sendMessage(0)">
        <el-icon :size="20">
          <Promotion />
        </el-icon>
      </button>
    </div>

    <div class="bottom-tab-placeholder"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showImagePreview } from 'vant'
import { ArrowLeft, Promotion, Picture } from '@element-plus/icons-vue'
import request from '@/api/request'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { formatAvatarUrl, formatImageUrl } from '@/utils/url'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const targetUserId = computed(() => route.params.id)
const goodsId = route.query.goodsId

const targetUser = ref({})
const messages = ref([])
const content = ref('')
const loading = ref(true)
const sending = ref(false)
const uploadingImage = ref(false)
const goodsInfo = ref(null)
const messageContainer = ref(null)
const imageInputRef = ref(null)
let pollingInterval = null

const DEFAULT_AVATAR = formatAvatarUrl('')

const isMyMessage = (msg) => {
  if (!userStore.user?.id) return false;
  const myId = String(userStore.user.id);
  const senderId = String(msg.senderId || msg.sender_id || '');
  return myId === senderId;
}

// 新增：获取当前登录者头像的逻辑
const getMyAvatar = () => {
  return formatAvatarUrl(userStore.user?.avatar || userStore.user?.headImg) || DEFAULT_AVATAR;
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    const date = new Date(timeStr)
    const now = new Date()
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    if (date.toDateString() === now.toDateString()) {
      return `${hours}:${minutes}`
    }
    return `${date.getMonth() + 1}月${date.getDate()}日 ${hours}:${minutes}`
  } catch (e) {
    return ''
  }
}

const getMessageImageUrl = (url) => {
  return formatImageUrl(url)
}

const fetchTargetUser = async () => {
  try {
    const res = await request.get('/user/info', { params: { userId: targetUserId.value } })
    targetUser.value = res.data || {}
  } catch (e) { }
}

const fetchGoodsInfo = async () => {
  if (!goodsId) return
  try {
    const res = await request.get(`/goods/detail/${goodsId}`)
    goodsInfo.value = res.data?.goods || res.data || null
  } catch (e) { }
}

const fetchHistory = async () => {
  try {
    const res = await request.get(`/chat/history/${targetUserId.value}`)
    if (res.code === '200' || res.success) {
      const list = res.data || res || []
      const newMessages = list.map(m => ({
        id: m.id,
        senderId: m.senderId || m.sender_id,
        content: m.content,
        type: m.msgType ?? m.type ?? 0,
        createTime: m.createTime || m.create_time
      }))

      // 只有当消息变化时才更新
      const hasNewMessages = newMessages.length > messages.value.length
      const lastMsgChanged = newMessages.length > 0 && messages.value.length > 0 && 
                            (newMessages[newMessages.length - 1].id !== messages.value[messages.value.length - 1].id)

      if (hasNewMessages || lastMsgChanged || (messages.value.length === 0 && newMessages.length > 0)) {
        messages.value = newMessages
        if (loading.value) {
          setTimeout(scrollToBottom, 100)
          loading.value = false
        } else {
          nextTick(scrollToBottom)
        }
        
        // 如果有新消息且当前在聊天界面，标记为已读
        if (newMessages.length > 0) {
          const lastMsg = newMessages[newMessages.length - 1]
          if (lastMsg.senderId == targetUserId.value) {
            markRead()
          }
        }
      }
    }
  } catch (e) {
    console.error('获取聊天历史失败:', e)
  }
}

const markRead = async () => {
  try {
    await request.post(`/chat/read/${targetUserId.value}`)
  } catch (e) {
    console.error('标记已读失败:', e)
  }
}

const sendMessage = async (type = 0, msgContent = '') => {
  const text = type === 0 ? content.value.trim() : msgContent
  if (!text || sending.value) return

  sending.value = true
  try {
    const res = await request.post('/chat/send', {
      receiverId: targetUserId.value,
      content: text,
      msgType: type
    })
    
    if (res.code === '200' || res.success) {
      if (type === 0) content.value = ''
      await fetchHistory()
      setTimeout(scrollToBottom, 100)
    } else {
      showToast(res.message || '发送失败')
    }
  } catch (e) {
    showToast('发送失败')
  } finally {
    sending.value = false
  }
}

const handleImageUpload = async (selectedFile) => {
  if (!selectedFile || uploadingImage.value) return

  if (!selectedFile.type?.startsWith('image/')) {
    showToast('请选择图片文件')
    return
  }

  if (selectedFile.size / 1024 / 1024 > 10) {
    showToast('图片不能超过10MB')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile)

  uploadingImage.value = true
  showToast('图片上传中...')
  try {
    const res = await request.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    const data = res?.data ?? res
    const imageUrl =
      (typeof data === 'string' ? data : '') ||
      data?.url ||
      data?.fileUrl ||
      data?.data?.fileUrl ||
      data?.filePath ||
      data?.path

    if (!imageUrl) {
      throw new Error('未获取到图片地址')
    }

    await sendMessage(1, imageUrl)
  } catch (e) {
    showToast(e.message || '图片上传失败')
  } finally {
    uploadingImage.value = false
  }
}

const handleImageInputChange = async (event) => {
  const selectedFile = event.target.files?.[0]
  await handleImageUpload(selectedFile)
  event.target.value = ''
}

const triggerImagePicker = () => {
  if (uploadingImage.value) return
  imageInputRef.value?.click()
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

onMounted(() => {
  fetchTargetUser()
  fetchGoodsInfo()
  fetchHistory()
  pollingInterval = setInterval(fetchHistory, 3000)
})

onUnmounted(() => clearInterval(pollingInterval))
const handleBack = () => router.back()
</script>

<style scoped>
.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
}

.top-nav-placeholder {
  height: calc(60px + env(safe-area-inset-top));
  padding-top: env(safe-area-inset-top);
  align-items: flex-end;
  padding-bottom: 10px;
  flex-shrink: 0;
}

.chat-body-container {
  flex: 1;
  overflow: hidden;
}

.message-scroll-area {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.input-section-fixed {
  flex-shrink: 0;
  padding-bottom: 12px;
}

.bottom-tab-placeholder {
  height: calc(82px + env(safe-area-inset-bottom));
  flex-shrink: 0;
}

.chat-image-input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

:deep(body),
:deep(html) {
  overflow: hidden !important;
  height: 100%;
}
</style>
