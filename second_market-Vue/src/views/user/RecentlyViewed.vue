<template>
  <div class="recently-viewed-page min-h-screen bg-[#f8f8fa]">
    <!-- Header -->
    <div class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-100 px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl text-gray-700 cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold text-gray-900">{{ $t('dashboard.recentlyViewed') }}</h1>
      </div>
      <div class="flex items-center gap-3">
        <LangSwitcher />
        <el-button v-if="canClear" type="danger" link @click="handleClear">
          <el-icon class="mr-1"><Delete /></el-icon>
          {{ $t('common.clear') || '清空' }}
        </el-button>
      </div>
    </div>

    <div class="px-4 pt-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-1 flex">
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'product' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'product'">商品</button>
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'post' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'post'">帖子</button>
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'service' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'service'">跑腿</button>
      </div>
    </div>

    <!-- Content -->
    <div class="p-4">
      <div v-if="loading && tab === 'product'" class="grid grid-cols-2 gap-4">
        <div v-for="i in 6" :key="i" class="bg-white p-2 rounded-2xl animate-pulse">
          <div class="bg-gray-100 aspect-square rounded-xl"></div>
          <div class="h-4 bg-gray-100 rounded mt-3 w-3/4"></div>
          <div class="h-6 bg-gray-100 rounded mt-2 w-1/2"></div>
        </div>
      </div>

      <div v-else-if="tab === 'product' && historyList.length > 0" class="grid grid-cols-2 gap-4">
        <div v-for="item in historyList" :key="item.id" 
          class="bg-white rounded-2xl p-2 shadow-sm active:scale-[0.98] transition-all relative group"
          @click="goDetail(item.product.id)">
          <!-- Delete button -->
          <div class="absolute top-3 right-3 z-10 opacity-0 group-hover:opacity-100 transition-opacity" 
            @click.stop="handleDelete(item.id)">
            <div class="w-7 h-7 bg-black/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white">
              <el-icon><Close /></el-icon>
            </div>
          </div>

          <div class="aspect-square rounded-xl overflow-hidden bg-gray-50 mb-2">
            <el-image :src="getProductImage(item.product.images)" fit="cover" class="w-full h-full" />
          </div>
          <div class="px-1 pb-1">
            <h3 class="text-sm font-medium text-gray-800 line-clamp-1">{{ item.product.title }}</h3>
            <div class="mt-1 flex items-baseline text-primary">
              <span class="text-xs font-bold">¥</span>
              <span class="text-lg font-black ml-0.5">{{ item.product.price }}</span>
            </div>
            <div class="mt-1 text-[10px] text-gray-400 flex items-center justify-between">
              <span>{{ getSchoolName(item.product.schoolId) }}</span>
              <span>{{ formatTime(item.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="tab === 'post'" class="space-y-3">
        <div
          v-for="p in contentHistory.postHistory"
          :key="p.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
          @click="router.push(`/user/forum/detail/${p.id}`)"
        >
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-sm font-bold text-gray-900 line-clamp-2">{{ p.title }}</div>
              <div class="mt-2 text-[11px] text-gray-500 flex items-center justify-between gap-2">
                <span class="truncate">{{ p.userNickname || '校友' }}</span>
                <span class="shrink-0">{{ p.commentCount || 0 }} 评论</span>
              </div>
            </div>
            <button class="text-gray-400" @click.stop="contentHistory.removePost(p.id)">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>
        <el-empty v-if="contentHistory.postHistory.length === 0" description="暂无帖子浏览记录" />
      </div>

      <div v-else-if="tab === 'service'" class="space-y-3">
        <div
          v-for="s in contentHistory.serviceHistory"
          :key="s.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
        >
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-sm font-bold text-gray-900 truncate">{{ s.title }}</div>
              <div class="mt-1 text-[11px] text-gray-500 line-clamp-2">{{ s.content }}</div>
            </div>
            <button class="text-gray-400" @click.stop="contentHistory.removeService(s.id)">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          <div class="mt-2 text-[11px] text-gray-500">{{ serviceTypeText(s.type) }} · {{ s.location || '校内' }}</div>
        </div>
        <el-empty v-if="contentHistory.serviceHistory.length === 0" description="暂无跑腿浏览记录" />
      </div>

      <div v-else class="py-20 flex flex-col items-center text-gray-400">
        <el-empty description="暂无浏览记录" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Delete, Close } from '@element-plus/icons-vue'
import { browseApi } from '@/api/browse'
import { formatImageUrl } from '@/utils/url'
import { useSchoolStore } from '@/stores/school'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'
import { useContentHistoryStore } from '@/stores/contentHistory'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const schoolStore = useSchoolStore()
const loading = ref(false)
const historyList = ref([])
const tab = ref('product')
const contentHistory = useContentHistoryStore()

const canClear = computed(() => {
  if (tab.value === 'product') return historyList.value.length > 0
  if (tab.value === 'post') return contentHistory.postHistory.length > 0
  return contentHistory.serviceHistory.length > 0
})

const serviceTypeText = (t) => {
  if (t === 1) return '跑腿互助'
  if (t === 2) return '拼单拼车'
  if (t === 3) return '资源共享'
  return '校园服务'
}

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatImageUrl(images)
  }
}

const getSchoolName = (id) => schoolStore.getSchoolName(String(id)) || '校内'
const formatTime = (t) => dayjs(t).fromNow()

const goDetail = (id) => router.push(`/user/product/${id}`)

const fetchHistory = async () => {
  loading.value = true
  try {
    const params = {
      current: 1,
      size: 50,
      schoolId: schoolStore.selectedSchool ? Number(schoolStore.selectedSchool) : undefined
    }
    const res = await browseApi.getList(params)
    if (res.code === '200' || res.code === 200) {
      historyList.value = res.data.records
    }
  } catch (error) {
    console.error('获取浏览历史失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    const res = await browseApi.delete(id)
    if (res.code === '200' || res.code === 200) {
      historyList.value = historyList.value.filter(item => item.id !== id)
      ElMessage.success('已删除')
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定清空当前列表吗？', '提示', { type: 'warning' })
    if (tab.value === 'product') {
      const res = await browseApi.clear()
      if (res.code === '200' || res.code === 200) {
        historyList.value = []
        ElMessage.success('已清空')
      }
      return
    }
    if (tab.value === 'post') {
      contentHistory.clearPost()
      ElMessage.success('已清空')
      return
    }
    contentHistory.clearService()
    ElMessage.success('已清空')
  } catch (error) {
  }
}

// 监听学校切换
watch(() => schoolStore.selectedSchool, fetchHistory)

onMounted(fetchHistory)
</script>
