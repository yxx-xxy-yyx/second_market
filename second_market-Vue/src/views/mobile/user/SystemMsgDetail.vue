<template>
  <div class="page-wrapper bg-[#f8fafc] min-h-screen flex flex-col">
    <div class="top-nav bg-white/80 backdrop-blur-xl px-4 flex items-center justify-between border-b border-gray-100 sticky top-0 z-50">
      <div class="flex items-center h-14">
        <el-icon @click="handleBack" :size="20" class="mr-4 cursor-pointer text-gray-600 active:text-teal-500">
          <ArrowLeft />
        </el-icon>
        <span class="text-lg font-bold text-gray-900">{{ $t('messages.messageDetail') }}</span>
      </div>
      
      <div class="flex items-center gap-2">
        <el-button 
          type="danger" 
          text 
          size="small" 
          class="delete-btn font-bold"
          @click="handleDelete"
        >
          {{ $t('common.delete') }}
        </el-button>
        <LangSwitcher />
      </div>
    </div>

    <div class="detail-container flex-1 overflow-y-auto">
      <div v-if="loading" class="flex justify-center py-20">
        <van-loading type="spinner" color="#14b8a6" />
      </div>

      <div v-else-if="detail" class="animate-content">
        <div class="bg-white min-h-[60vh] rounded-b-[32px] shadow-sm px-5 py-6">
          <h1 class="text-xl font-bold text-gray-900 leading-tight mb-4">
            {{ detail.title }}
          </h1>

          <div class="flex items-center justify-between mb-8">
            <div class="flex items-center space-x-2">
              <span class="px-2 py-0.5 bg-blue-50 text-blue-500 text-[10px] font-bold rounded">官方发布</span>
              <span v-if="detail.type === 0" class="px-2 py-0.5 bg-teal-50 text-teal-600 text-[10px] font-bold rounded">系统通知</span>
              <span v-else-if="detail.type === 1" class="px-2 py-0.5 bg-orange-50 text-orange-600 text-[10px] font-bold rounded">安全提醒</span>
              <span v-else class="px-2 py-0.5 bg-gray-100 text-gray-500 text-[10px] font-bold rounded">其他</span>
            </div>
            <span class="text-[11px] text-gray-400 font-medium">{{ formatFullTime(detail.createTime) }}</span>
          </div>

          <div class="message-content">
            <div v-if="detail.imgUrl" class="mb-6">
              <img :src="detail.imgUrl" class="w-full rounded-2xl border border-gray-50 shadow-sm" />
            </div>

            <div class="text-[15px] text-gray-700 leading-relaxed whitespace-pre-wrap">
              {{ detail.content }}
            </div>
          </div>

          <div class="mt-12 flex flex-col items-center">
            <div class="w-12 h-[1px] bg-gray-100 mb-4"></div>
            <p class="text-[10px] text-gray-300 tracking-widest uppercase">Intelligent Second-hand Market</p>
          </div>
        </div>
      </div>

      <el-empty v-else description="内容已被外星人带走了" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { showToast, showConfirmDialog } from 'vant'
import request from '@/api/request'
import LangSwitcher from '@/components/LangSwitcher.vue'

const router = useRouter()
const route = useRoute()
const detail = ref(null)
const loading = ref(true)

const fetchDetail = async () => {
  const msgId = route.params.id
  try {
    loading.value = true
    // 逻辑：通过 POST /message/my 并携带 ID 获取
    const res = await request({
      url: '/message/my',
      method: 'post',
      data: { id: msgId }
    })

    if (res.code === "200") {
      // 兼容 records 数组结构或直接对象结构
      detail.value = res.data.records ? res.data.records[0] : res.data
    } else {
      showToast(res.message)
    }
  } catch (e) {
    showToast('获取详情失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async () => {
  try {
    await showConfirmDialog({
      title: '删除确认',
      message: '确定要删除这条消息吗？',
      confirmButtonColor: '#ef4444'
    })
    const res = await request({
      url: '/message/delete',
      method: 'post',
      data: { id: route.params.id }
    })
    if (res.code === "200") {
      showToast('已删除')
      router.back()
    }
  } catch (e) {}
}

const handleBack = () => router.back()

const formatFullTime = (time) => {
  if (!time) return ''
  const t = time.replace('T', ' ')
  const date = new Date(t)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

onMounted(fetchDetail)
</script>

<style scoped>
/* 核心改动：不再使用 fixed，使用 flex 布局防止遮挡顶部 */
.page-wrapper {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.top-nav {
  padding-top: env(safe-area-inset-top);
  flex-shrink: 0;
}

.detail-container {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.delete-btn {
  color: #f43f5e !important;
}

.message-content {
  letter-spacing: 0.3px;
}

/* 简单的内容进场动画 */
.animate-content {
  animation: slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>