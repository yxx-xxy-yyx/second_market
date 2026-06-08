<template>
  <div class="user-bottom-nav">
    <div v-for="item in navMenu" :key="item.path"
      :class="['nav-item', { active: isActive(item.path), 'publish-center': item.isPublish }]"
      @click="handleNav(item.path)">
      <div class="nav-icon-container">
        <el-badge :is-dot="item.id === 'chat' && hasUnread" :offset="[0, 0]">
          <el-icon :size="item.isPublish ? 36 : 28">
            <component :is="item.icon" />
          </el-icon>
        </el-badge>
      </div>
      <span class="nav-label" v-if="!item.isPublish">{{ $t('nav.' + item.id) }}</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import IconHome from '@/components/icons/IconHome.vue'
import IconPlus from '@/components/icons/IconPlus.vue'
import IconUser from '@/components/icons/IconUser.vue'
import { ChatDotRound, School } from '@element-plus/icons-vue'
import { messageApi } from '@/api/message'
import { chatApi } from '@/api/chat'
import { useUserStore } from '@/stores/user'
import { onMounted, onUnmounted, ref, computed } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const unreadCount = ref(0)
const hasUnread = computed(() => unreadCount.value > 0)
let timer = null

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const [msgRes, chatRes] = await Promise.all([
      messageApi.getUnreadCount().catch(() => ({ code: '500' })),
      chatApi.getUnreadCount().catch(() => ({ code: '500' }))
    ])
    
    let total = 0
    if (msgRes.code === '200') total += (msgRes.data || 0)
    if (chatRes.code === '200') total += (chatRes.data || 0)
    
    unreadCount.value = total
  } catch (error) {
    // 静默处理错误，避免控制台过多报错
  }
}

onMounted(() => {
  fetchUnreadCount()
  // 每2分钟轮询一次未读消息，减少后端压力
  timer = setInterval(fetchUnreadCount, 120000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const navMenu = [
  { id: 'home', path: '/user/dashboard', icon: IconHome },
  { id: 'categories', path: '/user/categories', icon: School },
  { id: 'publish', path: '/user/publish', icon: IconPlus, isPublish: true },
  { id: 'chat', path: '/user/messageChat', icon: ChatDotRound },
  { id: 'myAccount', path: '/user/profile', icon: IconUser }
]

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const handleNav = (path) => {
  console.log('Navigating to:', path)
  if (route.path === path) return
  router.push(path)
}
</script>

<style scoped>
.user-bottom-nav {
  display: flex;
  position: fixed;
  bottom: max(10px, env(safe-area-inset-bottom));
  left: 12px;
  right: 12px;
  height: 72px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  justify-content: space-around;
  align-items: center;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
  flex: 1;
  height: 100%;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 18px;
}

.nav-item.active {
  color: #667eea;
}

.nav-item.active:not(.publish-center) {
  background: rgba(102, 126, 234, 0.2);
}

.nav-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
}

.nav-label {
  font-size: 11px;
  margin-top: -2px;
  line-height: 1;
}

.publish-center .nav-icon-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  margin-top: -34px;
  border: 4px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.4);
}
</style>
