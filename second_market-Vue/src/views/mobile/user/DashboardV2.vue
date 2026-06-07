<template>
  <!-- 顶部导航：Logo + 搜索框(内置放大镜) + 语言切换 -->
  <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100"
    :style="{ paddingTop: 'env(safe-area-inset-top)' }">
    <div class="h-[52px] px-3 flex items-center justify-between gap-3">
      <!-- 左侧 Logo -->
      <button class="flex items-center gap-2 min-w-0" @click="router.push('/user/dashboard')">
        <BrandLogo title="" class="text-[color:var(--primary-color)]" />
      </button>

      <!-- 中间：搜索框入口（点击跳转搜索页） -->
      <div
        class="flex-1 h-[32px] flex items-center gap-2 bg-white border border-gray-300 rounded-full px-3 cursor-pointer hover:border-gray-400 transition"
        @click="router.push('/user/search')">
        <el-icon class="text-gray-400" :size="14">
          <Search />
        </el-icon>
        <span class="text-sm text-gray-400 truncate">{{ $t('common.search') || '搜索商品...' }}</span>
      </div>

    </div>
  </div>

  <!-- 原有页面内容 👇 清理后：只保留 公告 + 轮播 + 推荐商品 -->
  <div class="px-3 py-4 space-y-5">
    <!-- 公告轮播 -->
    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
      <div class="h-auto px-3 py-2 flex items-center gap-2">
        <el-icon class="sm-text-primary flex-shrink-0" :size="18">
          <Bell />
        </el-icon>
        <div class="flex-1 min-w-0">
          <el-carousel height="44px" direction="vertical" :autoplay="true" :interval="4000" indicator-position="none"
            arrow="never" class="w-full">
            <el-carousel-item v-for="(notice, index) in allNotices" :key="index" class="flex items-center">
              <div class="w-full h-11 flex items-center gap-2 cursor-pointer hover:bg-gray-50 px-2 rounded transition"
                @click="handleNoticeClick(notice)">
                <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'"
                  class="rounded-md flex-shrink-0">
                  {{ getNoticeTypeLabel(notice.type) }}
                </el-tag>
                <span class="flex-1 min-w-0 text-sm text-gray-900 truncate font-medium">
                  {{ getNoticeTitle(notice) }}
                </span>
                <span class="text-xs text-gray-400 flex-shrink-0 whitespace-nowrap">
                  {{ formatNoticeDate(notice.publishTime) }}
                </span>
              </div>
            </el-carousel-item>
            <el-carousel-item v-if="allNotices.length === 0" class="flex items-center">
              <div class="w-full h-11 flex items-center gap-2 px-2 text-sm text-gray-500">
                {{ $t('common.noRecentNotice') }}
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        <button class="text-xs text-gray-500 flex-shrink-0 hover:text-gray-700 transition"
          @click="router.push('/user/notices')">
          {{ $t('common.more') }}
        </button>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm">
      <el-carousel height="190px" :interval="4200" arrow="never" indicator-position="bottom">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="relative h-full w-full">
            <img :src="item.image" class="absolute inset-0 h-full w-full object-cover" loading="lazy" decoding="async"
              @error="handleBannerImgError" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/55 to-black/10"></div>
            <div class="absolute left-4 right-4 bottom-4 text-white">
              <div class="text-base font-semibold leading-snug">
                {{ item.title }}
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 推荐商品 -->
    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('dashboard.recommend') }}</div>
        <button class="text-sm sm-text-primary" @click="router.push('/user/products')">{{ $t('dashboard.viewMore')
          }}</button>
      </div>

      <div v-loading="loading" class="grid grid-cols-2 gap-3">
        <button v-for="p in recommendProducts.slice(0, 10)" :key="p.id"
          class="flex flex-col rounded-2xl bg-white border border-gray-100 overflow-hidden shadow-sm text-left active:scale-[0.99] transition h-full"
          @click="router.push(`/user/product/${p.id}`)">
          <div class="w-full aspect-[4/3] bg-gray-100 rounded-t-2xl overflow-hidden flex-shrink-0">
            <img :src="getProductImage(p.images)" class="h-full w-full object-cover display-block" loading="lazy"
              decoding="async" @error="handleImgError" />
          </div>
          <div class="p-3 flex-1 flex flex-col">
            <div class="text-sm font-semibold text-gray-900 line-clamp-2 h-10 leading-[20px] mb-2">
              {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
            </div>
            <div class="mt-auto flex items-center justify-between">
              <div class="text-base font-bold sm-text-primary">
                {{ $t('common.currency') }}{{ p.price }}
              </div>
              <div class="text-xs text-gray-500">
                <el-icon :size="12">
                  <View />
                </el-icon>
                <span class="ml-1">{{ p.viewCount || 0 }}</span>
              </div>
            </div>
          </div>
        </button>
      </div>
    </div>
  </div>

  <!-- 公告抽屉 -->
  <el-drawer v-model="showNoticeDrawer" :title="$t('dashboard.notice')" direction="rtl" size="88%">
    <div class="px-2 py-2 space-y-3">
      <button v-for="(notice, index) in allNotices" :key="index"
        class="w-full text-left rounded-2xl bg-white border border-gray-100 shadow-sm px-4 py-4 active:scale-[0.99] transition"
        @click="handleNoticeDetailClick(notice)">
        <div class="flex items-center justify-between">
          <el-tag :type="notice.type === 'system' ? 'primary' : notice.type === 'activity' ? 'success' : 'warning'"
            size="small">
            {{ getNoticeTypeLabel(notice.type) }}
          </el-tag>
          <span class="text-xs text-gray-400">{{ formatNoticeDate(notice.publishTime) }}</span>
        </div>
        <div class="mt-2 text-sm font-semibold text-gray-900">
          {{ getNoticeTitle(notice) }}
        </div>
        <div class="mt-1 text-xs text-gray-500 line-clamp-2">
          {{ getNoticeContent(notice).replace(/<[^>]+>/g, '').substring(0, 50) + '...' }}
        </div>
      </button>
    </div>
  </el-drawer>

  <!-- 公告弹窗 -->
  <el-dialog v-model="showNoticeDetail" :title="selectedNotice ? getNoticeTitle(selectedNotice) : ''" width="92%">
    <div v-if="selectedNotice" class="space-y-3">
      <div class="flex items-center justify-between">
        <el-tag
          :type="selectedNotice.type === 'system' ? 'primary' : selectedNotice.type === 'activity' ? 'success' : 'warning'">
          {{ getNoticeTypeLabel(selectedNotice.type) }}
        </el-tag>
        <span class="text-xs text-gray-400">{{ formatNoticeDate(selectedNotice.publishTime) }}</span>
      </div>
      <div class="text-sm text-gray-800 leading-relaxed">
        {{ getNoticeContent(selectedNotice) }}
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { productApi } from '@/api/product'
import { userStatisticsApi } from '@/api/statistics'
import { noticeApi } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { formatImageUrl } from '@/utils/url'
import BrandLogo from '@/components/BrandLogo.vue'
import {
  Bell,
  View,
  Search
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const { t, locale } = useI18n()

const loading = ref(false)
const showNoticeDrawer = ref(false)
const showNoticeDetail = ref(false)
const selectedNotice = ref(null)
// 搜索已移至搜索页面 /user/search

// 语言切换
const allNotices = ref([])
const recommendProducts = ref([])

const recentNotices = computed(() => {
  const now = new Date()
  const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)

  return allNotices.value.filter(notice => {
    const timeStr = notice.publishTime || notice.createTime
    if (!timeStr) return true

    const noticeTime = new Date(timeStr)
    if (isNaN(noticeTime.getTime())) return true

    return noticeTime >= sevenDaysAgo && noticeTime <= now
  }).slice(0, 10)
})

const DEFAULT_IMAGE = 'https://via.placeholder.com/480x360?text=No+Image'

const BANNER_FALLBACK =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='400'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%2306b6d4'/><stop offset='1' stop-color='%238b5cf6'/></linearGradient></defs><rect width='1200' height='400' fill='url(%23g)'/><text x='60' y='210' fill='white' font-family='Arial' font-size='44' font-weight='700'>SecondMarket</text><text x='60' y='270' fill='white' font-family='Arial' font-size='22' opacity='0.9'>Campus second-hand marketplace</text></svg>"

const handleBannerImgError = (e) => {
  if (e?.target) e.target.src = BANNER_FALLBACK
}

// 公告标题前端翻译映射（后端无 titleEn 时使用）
const noticeTitleMap = computed(() => ({
  '欢迎使用智能二手商城': t('messages.templates.welcomeTitle'),
  '订单支付成功': t('messages.templates.orderPaidTitle'),
  '您的商品已售出': t('messages.templates.productSoldTitle'),
  '收到新评价': t('messages.templates.newReviewTitle')
}))

const noticeContentMap = computed(() => ({
  '欢迎使用智能二手商城': t('messages.templates.welcomeContent'),
}))

const getNoticeTitle = (notice) => {
  if (locale.value === 'en' && notice.titleEn) return notice.titleEn
  if (locale.value === 'ko' && notice.titleKo) return notice.titleKo
  if (locale.value !== 'zh' && noticeTitleMap.value[notice.title]) return noticeTitleMap.value[notice.title]
  return notice.title
}

const getNoticeContent = (notice) => {
  if (locale.value === 'en' && notice.contentEn) return notice.contentEn
  if (locale.value === 'ko' && notice.contentKo) return notice.contentKo
  if (locale.value !== 'zh' && noticeContentMap.value[notice.content]) return noticeContentMap.value[notice.content]
  return notice.content
}

const getNoticeTypeLabel = (type) => {
  const labels = {
    system: t('common.system'),
    activity: t('common.activity'),
    maintenance: t('common.maintenance')
  }
  return labels[type] || type
}

const formatNoticeDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 60) return t('messages.minutesAgo', { count: minutes })
  if (hours < 24) return t('messages.hoursAgo', { count: hours })
  if (days < 7) return t('messages.daysAgo', { count: days })
  return d.toLocaleDateString(locale.value === 'ko' ? 'ko-KR' : locale.value === 'en' ? 'en-US' : 'zh-CN')
}

const toggleAiAssistant = () => {
  window.dispatchEvent(new CustomEvent('toggle-ai-assistant'))
}

const banners = computed(() => [
  { image: BANNER_FALLBACK, title: t('dashboard.bannerAiTitle') },
  { image: BANNER_FALLBACK, title: t('dashboard.bannerSafeTitle') }
])

const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0]
    if (!firstImage) return DEFAULT_IMAGE
    return formatImageUrl(firstImage)
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
}

const handleNoticeClick = (notice) => {
  selectedNotice.value = notice
  showNoticeDetail.value = true
}

const handleNoticeDetailClick = (notice) => {
  selectedNotice.value = notice
  showNoticeDrawer.value = false
  showNoticeDetail.value = true
}

const fetchNotices = async () => {
  try {
    const res = await noticeApi.getList(1, 10, 1)
    if (res.code === '200') {
      const records = res.data.records || []
      allNotices.value = records.map(notice => ({
        type: notice.type === 0 ? 'system' : notice.type === 1 ? 'activity' : 'maintenance',
        title: notice.title,
        titleKo: notice.titleKo || '',
        titleEn: notice.titleEn || '',
        content: notice.content,
        contentKo: notice.contentKo || '',
        contentEn: notice.contentEn || '',
        publishTime: notice.publishTime || notice.createTime
      }))
    }
  } catch (e) { }
}

const fetchRecommendProducts = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 12, status: 2, sortBy: 'createTime' }
    const res = await productApi.getProductList(params)
    if (res.code === '200') recommendProducts.value = res.data.records || []
  } finally { loading.value = false }
}

onMounted(() => {
  fetchNotices()
  fetchRecommendProducts()
})
</script>

<style scoped>
div::-webkit-scrollbar {
  display: none;
}
</style>
