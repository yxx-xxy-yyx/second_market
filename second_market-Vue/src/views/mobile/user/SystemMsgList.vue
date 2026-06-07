<template>
  <div class="page-wrapper bg-[#f8fafc] min-h-screen">
    <div class="top-nav bg-white/80 backdrop-blur-xl px-4 flex items-center justify-between h-14 border-b border-gray-100 sticky top-0 z-30">
      <div class="flex items-center">
        <el-icon @click="router.back()" :size="20" class="mr-4 cursor-pointer text-gray-600"><ArrowLeft /></el-icon>
        <span class="text-lg font-bold text-gray-900">{{ $t('messages.systemNotification') }}</span>
      </div>
      <LangSwitcher />
    </div>

    <div class="filter-sticky-bar bg-white px-4 py-3 border-b border-gray-50 flex justify-between items-center sticky top-14 z-20">
      <div class="filter-tabs flex bg-gray-100 p-1 rounded-xl">
        <div 
          v-for="filter in filterList" 
          :key="filter.value"
          :class="['filter-tab', { active: currentFilter === filter.value }]"
          @click="handleFilterChange(filter.value)"
        >
          {{ filter.label }}
        </div>
      </div>
      
      <el-button 
        type="primary" 
        text 
        size="small" 
        class="mark-all-btn"
        @click="handleMarkAllRead"
        :disabled="sysMessages.length === 0"
      >
        全部已读
      </el-button>
    </div>

    <div class="list-container p-4">
      <div v-if="loading" class="flex justify-center py-20">
        <van-loading type="spinner" color="#14b8a6" />
      </div>

      <el-empty v-else-if="sysMessages.length === 0" description="暂无相关消息" />

      <div v-else class="space-y-3">
        <div 
          v-for="item in sysMessages" 
          :key="item.id"
          @click="handleGoDetail(item)"
          class="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 active:scale-[0.98] transition-all duration-200 relative overflow-hidden"
        >
          <div v-if="item.isRead === 0" class="absolute top-4 left-2 w-1.5 h-1.5 bg-red-500 rounded-full"></div>
          
          <div class="flex justify-between items-start mb-2">
            <div class="flex items-center space-x-2">
              <el-icon :size="18" :class="item.isRead === 0 ? 'text-blue-500' : 'text-gray-400'">
                <Notification />
              </el-icon>
              <h3 :class="['font-bold text-[15px]', item.isRead === 0 ? 'text-gray-900' : 'text-gray-500']">
                {{ item.title }}
              </h3>
            </div>
            <span class="text-[11px] text-gray-400 font-medium">{{ formatTime(item.createTime) }}</span>
          </div>

          <p class="text-sm text-gray-500 line-clamp-2 leading-relaxed pl-6">
            {{ item.content }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Notification } from '@element-plus/icons-vue'
import { showToast, showConfirmDialog } from 'vant'
import request from '@/api/request'
import LangSwitcher from '@/components/LangSwitcher.vue'

const router = useRouter()
const sysMessages = ref([])
const loading = ref(true)
const currentFilter = ref(null) // null: 全部, 0: 未读, 1: 已读

const filterList = [
  { label: '全部', value: null },
  { label: '未读', value: 0 },
  { label: '已读', value: 1 }
]

// 获取系统消息列表
const fetchSysMessages = async () => {
  try {
    loading.value = true
    const postData = {
      // 如果后端需要分页参数，可以在此添加
      // current: 1,
      // size: 10
    }
    
    // 如果选择了已读/未读状态
    if (currentFilter.value !== null) {
      postData.isRead = currentFilter.value
    }

    const res = await request({
      url: '/message/my',
      method: 'post',
      data: postData
    })

    if (res.code === "200") {
      /**
       * 关键修正：
       * 根据你提供的 JSON，数组在 res.data.records 里面
       */
      sysMessages.value = res.data.records || []
      console.log('解析后的列表数据:', sysMessages.value)
    } else {
      showToast(res.message || '加载失败')
    }
  } catch (e) {
    console.error('fetch error:', e)
    showToast('网络开小差了')
  } finally {
    loading.value = false
  }
}

const handleFilterChange = (val) => {
  currentFilter.value = val
  fetchSysMessages()
}

const handleMarkAllRead = async () => {
  try {
    await showConfirmDialog({
      title: '确认',
      message: '是否将全部消息标记为已读？',
    })

    const res = await request({
      url: '/message/readAll',
      method: 'post'
    })

    if (res.code === "200") {
      showToast('已全部标记已读')
      fetchSysMessages()
    }
  } catch (e) { }
}

const handleGoDetail = (item) => {
  router.push(`/user/system-msg-detail/${item.id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  // 处理后端 ISO 格式时间 T
  const t = time.replace('T', ' ')
  const date = new Date(t)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  }
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

onMounted(fetchSysMessages)
</script>

<style scoped>
.filter-tab {
  padding: 4px 16px;
  font-size: 13px;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tab.active {
  background: white;
  color: #14b8a6;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.mark-all-btn {
  font-weight: bold;
  color: #3b82f6;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.top-nav {
  padding-top: env(safe-area-inset-top);
}
</style>