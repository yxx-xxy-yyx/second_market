<template>
  <div class="recently-viewed-page min-h-screen bg-[#f8f8fa]">
    <!-- Header -->
    <div
      class="sticky top-0 z-50 bg-gradient-to-br from-[#14b8a6] to-[#06b6d4] text-white backdrop-blur-md px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl text-white cursor-pointer" @click="router.back()">
          <ArrowLeft />
        </el-icon>
        <h1 class="text-lg font-bold text-white">{{ $t('dashboard.recentlyViewed') }}</h1>
      </div>
      <div class="flex items-center gap-3">
        <LangSwitcher />
        <button v-if="canClear"
          class="text-white text-xs font-medium px-3 py-1.5 rounded-full bg-white/15 hover:bg-white/25 transition"
          @click="handleClear">
          <el-icon class="mr-1 text-white">
            <Delete />
          </el-icon>
          {{ $t('common.clear') || '清空' }}
        </button>
      </div>
    </div>

    <div class="px-4 pt-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-1 flex">
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition"
          :class="tab === 'product' ? 'bg-primary/10 text-primary' : 'text-gray-500'"
          @click="tab = 'product'">商品</button>
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition"
          :class="tab === 'post' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'post'">帖子</button>
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition"
          :class="tab === 'service' ? 'bg-primary/10 text-primary' : 'text-gray-500'"
          @click="tab = 'service'">跑腿</button>
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

      <div v-else-if="tab === 'product' && historyList.length > 0" class="space-y-3">
        <div v-for="item in historyList" :key="item.id"
          class="bg-white rounded-2xl p-3 shadow-sm border border-gray-100 flex space-x-3 active:scale-[0.99] transition-transform duration-200 relative group"
          @click="goDetail(item.product.id)">
          <button class="absolute top-3 right-3 z-10 w-7 h-7 bg-black/40 backdrop-blur-sm rounded-full flex items-center justify-center text-white"
            @click.stop="handleDelete(item.id)">
            <el-icon><Close /></el-icon>
          </button>

          <div class="w-28 h-28 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 relative">
            <img
              :src="getProductImage(item.product.images)"
              class="h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleImgError"
            />
            <div v-if="item.product.status !== 2" class="absolute inset-0 bg-black/50 flex items-center justify-center">
              <span class="text-white text-xs font-bold border border-white px-2 py-1 rounded-full">
                {{ statusText(item.product.status) }}
              </span>
            </div>
          </div>

          <div class="flex-1 min-w-0 flex flex-col justify-between py-1 pr-8">
            <div>
              <h3 class="text-sm font-bold text-gray-900 line-clamp-2 leading-snug">
                <span
                  class="inline-block text-[10px] px-1.5 py-0.5 rounded mr-1 align-middle"
                  :class="statusBadgeClass(item.product.status)"
                >
                  {{ statusText(item.product.status) }}
                </span>
                {{ locale === 'ko' && item.product.titleKo ? item.product.titleKo : item.product.title }}
              </h3>
              <div class="mt-2 flex items-baseline space-x-2">
                <span class="text-red-500 font-bold text-lg">{{ $t('common.currency') }}{{ item.product.price }}</span>
                <span v-if="item.product.originalPrice" class="text-gray-400 text-xs line-through">
                  {{ $t('common.currency') }}{{ item.product.originalPrice }}
                </span>
              </div>
              <div class="flex items-center text-xs text-gray-400 mt-1">
                <el-icon class="mr-1"><Clock /></el-icon>
                {{ formatDate(item.updateTime || item.createTime) }}
                <span class="mx-1">·</span>
                {{ $t('product.viewCount') }} {{ item.product.viewCount || 0 }}
                <span class="mx-1">·</span>
                {{ $t('profile.favoriteCount') }} {{ item.product.favoriteCount || 0 }}
              </div>
            </div>

            <div class="mt-2 text-[11px] text-gray-500 flex items-center justify-between">
              <span>{{ getSchoolName(item.product.schoolId) }}</span>
              <span>{{ formatTime(item.updateTime || item.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="tab === 'post'" class="space-y-3">
        <div v-for="p in contentHistory.postHistory" :key="p.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
          @click="router.push(`/user/forum/detail/${p.id}`)">
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-sm font-bold text-gray-900 line-clamp-2">{{ p.title }}</div>
              <div class="mt-2 text-[11px] text-gray-500 flex items-center justify-between gap-2">
                <span class="truncate">{{ p.userNickname || '校友' }}</span>
                <span class="shrink-0">{{ p.commentCount || 0 }} 评论</span>
              </div>
            </div>
            <button class="text-gray-400" @click.stop="contentHistory.removePost(p.id)">
              <el-icon>
                <Close />
              </el-icon>
            </button>
          </div>
        </div>
        <el-empty v-if="contentHistory.postHistory.length === 0" description="暂无帖子浏览记录" />
      </div>

      <div v-else-if="tab === 'service'" class="space-y-3">
        <div v-for="s in contentHistory.serviceHistory" :key="s.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-sm font-bold text-gray-900 truncate">{{ s.title }}</div>
              <div class="mt-1 text-[11px] text-gray-500 line-clamp-2">{{ s.content }}</div>
            </div>
            <button class="text-gray-400" @click.stop="contentHistory.removeService(s.id)">
              <el-icon>
                <Close />
              </el-icon>
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
import { useI18n } from 'vue-i18n'
import { ArrowLeft, Delete, Close, Clock } from '@element-plus/icons-vue'
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
const { t, locale } = useI18n()
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

const handleImgError = (e) => {
  if (e?.target) e.target.src = ''
}

const getSchoolName = (id) => schoolStore.getSchoolName(String(id)) || '校内'
const formatTime = (t) => dayjs(t).fromNow()
const formatDate = (time) => {
  if (!time) return ''
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) return ''
  const localeName = locale.value === 'ko' ? 'ko-KR' : locale.value === 'en' ? 'en-US' : 'zh-CN'
  return date.toLocaleDateString(localeName, { month: 'short', day: 'numeric' })
}

const statusText = (status) => {
  const labels = {
    0: t('order.draft'),
    1: t('order.pendingReview'),
    2: locale.value === 'ko' ? '판매중' : locale.value === 'en' ? 'Listed' : '已上架',
    3: locale.value === 'ko' ? '내림' : locale.value === 'en' ? 'Unlisted' : '已下架',
    4: locale.value === 'ko' ? '완료' : locale.value === 'en' ? 'Completed' : '已完成'
  }
  return labels[status] || t('common.unknown')
}

const statusBadgeClass = (status) => {
  const classMap = {
    0: 'bg-slate-100 text-slate-500',
    1: 'bg-yellow-50 text-yellow-600',
    2: 'bg-teal-50 text-teal-600',
    3: 'bg-gray-100 text-gray-500',
    4: 'bg-gray-100 text-gray-500'
  }
  return classMap[status] || 'bg-gray-100 text-gray-500'
}

const goDetail = (id) => router.push(`/user/product/${id}`)

const fetchHistory = async () => {
  loading.value = true
  try {
    const params = {
      current: 1,
      size: 50
    }
    const res = await browseApi.getMine(params)
    if (res.code === '200' || res.code === 200) {
      historyList.value = (res.data?.records || []).filter(item => item.product && item.product.id)
    } else {
      historyList.value = []
      if (res.code === '401') {
        ElMessage.warning(t('pleaseLogin'))
      } else {
        ElMessage.error(res.message || t('requestFailed'))
      }
    }
  } catch (error) {
    console.error('获取浏览历史失败:', error)
    ElMessage.error(t('common.requestFailed'))
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
