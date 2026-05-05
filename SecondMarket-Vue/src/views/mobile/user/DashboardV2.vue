<template>
  <div class="px-3 py-4 space-y-5">
    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
      <div class="h-11 px-3 flex items-center gap-2">
        <el-icon class="sm-text-primary" :size="18"><Bell /></el-icon>
        <el-carousel
          height="44px"
          direction="vertical"
          :autoplay="true"
          :interval="3000"
          indicator-position="none"
          arrow="never"
          class="flex-1 min-w-0"
        >
          <el-carousel-item v-for="(notice, index) in allNotices" :key="index">
            <div class="h-11 flex items-center gap-2" @click="handleNoticeClick(notice)">
              <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'" class="rounded-md">
                {{ getNoticeTypeLabel(notice.type) }}
              </el-tag>
              <span class="flex-1 min-w-0 text-sm text-gray-900 truncate">
                {{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}
              </span>
              <span class="text-xs text-gray-400 flex-shrink-0">
                {{ formatNoticeDate(notice.publishTime || notice.date) }}
              </span>
            </div>
          </el-carousel-item>
        </el-carousel>
        <button class="text-xs text-gray-500" @click="showNoticeDrawer = true">
          {{ $t('common.more') }}
        </button>
      </div>
    </div>

    <div class="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm">
      <el-carousel height="190px" :interval="4200" arrow="never" indicator-position="bottom">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="relative h-full w-full">
            <img
              :src="item.image"
              class="absolute inset-0 h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleBannerImgError"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-black/55 to-black/10"></div>
            <div class="absolute left-4 right-4 bottom-4 text-white">
              <div class="text-base font-semibold leading-snug">
                {{ locale === 'ko' ? item.titleKo : item.title }}
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('nav.categories') }}</div>
        <button class="text-sm sm-text-primary" @click="router.push('/user/categories')">{{ $t('common.view') }}</button>
      </div>

      <CategoryGrid :items="categories" :columns="4" :gap="12" :icon-size="22" @select="goCategory" />
    </div>

    <div class="space-y-3">
      <div class="flex items-center justify-between px-1">
        <div class="text-sm font-semibold text-gray-900">{{ $t('common.activity') }}</div>
        <button class="text-sm sm-text-primary" @click="toggleAiAssistant">{{ $t('ai.assistant') }}</button>
      </div>

      <div class="flex gap-3 overflow-x-auto pb-1" style="scrollbar-width: none;">
        <button
          v-for="action in actions"
          :key="action.path"
          class="flex-none w-[220px] rounded-2xl bg-white border border-gray-100 shadow-sm px-4 py-4 text-left active:scale-[0.99] transition"
          @click="router.push(action.path)"
        >
          <div class="flex items-center justify-between">
            <div class="h-11 w-11 rounded-2xl flex items-center justify-center text-white shadow-sm" :style="{ background: action.bg }">
              <el-icon :size="22"><component :is="action.icon" /></el-icon>
            </div>
            <el-icon class="text-gray-300" :size="18"><ArrowRight /></el-icon>
          </div>
          <div class="mt-3 text-sm font-semibold text-gray-900">{{ action.label }}</div>
          <div class="text-xs text-gray-500 mt-1">{{ action.desc }}</div>
        </button>
      </div>
    </div>

    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('dashboard.recommend') }}</div>
        <button class="text-sm sm-text-primary" @click="router.push('/user/products')">{{ $t('dashboard.viewMore') }}</button>
      </div>

      <div v-loading="loading" class="grid grid-cols-2 gap-3">
        <button
          v-for="p in recommendProducts.slice(0, 10)"
          :key="p.id"
          class="rounded-2xl bg-white border border-gray-100 overflow-hidden shadow-sm text-left active:scale-[0.99] transition"
          @click="router.push(`/user/product/${p.id}`)"
        >
          <div class="w-full aspect-[4/3] bg-gray-100">
            <img
              :src="getProductImage(p.images)"
              class="h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleImgError"
            />
          </div>
          <div class="p-3">
            <div class="text-sm font-semibold text-gray-900 line-clamp-2">
              {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
            </div>
            <div class="mt-2 flex items-center justify-between">
              <div class="text-base font-bold sm-text-primary">
                {{ $t('common.currency') }}{{ p.price }}
              </div>
              <div class="text-xs text-gray-500">
                <el-icon :size="12"><View /></el-icon>
                <span class="ml-1">{{ p.viewCount || 0 }}</span>
              </div>
            </div>
          </div>
        </button>
      </div>
    </div>
  </div>

  <el-drawer
    v-model="showNoticeDrawer"
    :title="$t('dashboard.notice')"
    direction="rtl"
    size="88%"
  >
    <div class="px-2 py-2 space-y-3">
      <button
        v-for="(notice, index) in allNotices"
        :key="index"
        class="w-full text-left rounded-2xl bg-white border border-gray-100 shadow-sm px-4 py-4 active:scale-[0.99] transition"
        @click="handleNoticeDetailClick(notice)"
      >
        <div class="flex items-center justify-between">
          <el-tag :type="notice.type === 'system' ? 'primary' : notice.type === 'activity' ? 'success' : 'warning'" size="small">
            {{ notice.typeLabel }}
          </el-tag>
          <span class="text-xs text-gray-400">{{ notice.date }}</span>
        </div>
        <div class="mt-2 text-sm font-semibold text-gray-900">
          {{ notice.title }}
        </div>
        <div class="mt-1 text-xs text-gray-500 line-clamp-2">
          {{ notice.summary }}
        </div>
      </button>
    </div>
  </el-drawer>

  <el-dialog v-model="showNoticeDetail" :title="selectedNotice?.title" width="92%">
    <div v-if="selectedNotice" class="space-y-3">
      <div class="flex items-center justify-between">
        <el-tag :type="selectedNotice.type === 'system' ? 'primary' : selectedNotice.type === 'activity' ? 'success' : 'warning'">
          {{ selectedNotice.typeLabel }}
        </el-tag>
        <span class="text-xs text-gray-400">{{ selectedNotice.date }}</span>
      </div>
      <div class="text-sm text-gray-800 leading-relaxed">
        {{ selectedNotice.content }}
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { productApi } from '@/api/product'
import { userStatisticsApi } from '@/api/statistics'
import { noticeApi } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { formatImageUrl } from '@/utils/url'
import { CATEGORIES } from '@/utils/categories'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import IconPlus from '@/components/icons/IconPlus.vue'
import IconBag from '@/components/icons/IconBag.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import {
  Bell,
  ArrowRight,
  View,
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { t, locale } = useI18n()

const loading = ref(false)
const showNoticeDrawer = ref(false)
const showNoticeDetail = ref(false)
const selectedNotice = ref(null)

const allNotices = ref([])
const recommendProducts = ref([])

const DEFAULT_IMAGE = 'https://via.placeholder.com/480x360?text=No+Image'

const BANNER_FALLBACK =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='400'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%2306b6d4'/><stop offset='1' stop-color='%238b5cf6'/></linearGradient></defs><rect width='1200' height='400' fill='url(%23g)'/><text x='60' y='210' fill='white' font-family='Arial' font-size='44' font-weight='700'>SecondMarket</text><text x='60' y='270' fill='white' font-family='Arial' font-size='22' opacity='0.9'>Campus second-hand marketplace</text></svg>"

const handleBannerImgError = (e) => {
  if (e?.target) e.target.src = BANNER_FALLBACK
}

const getNoticeTypeLabel = (type) => {
  const labels = {
    system: locale.value === 'ko' ? '시스템' : locale.value === 'en' ? 'System' : '系统',
    activity: locale.value === 'ko' ? '이벤트' : locale.value === 'en' ? 'Activity' : '活动',
    maintenance: locale.value === 'ko' ? '점검' : locale.value === 'en' ? 'Maintenance' : '维护'
  }
  return labels[type] || type
}

const formatNoticeDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string' && date.includes('前')) {
    if (locale.value === 'ko') {
      return date.replace('小时前', '시간 전').replace('分钟前', '분 전').replace('天前', '일 전')
    }
    return date
  }
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  const now = new Date()
  const diff = now - d
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 60) return locale.value === 'ko' ? `${minutes}분 전` : `${minutes}分钟前`
  if (hours < 24) return locale.value === 'ko' ? `${hours}시간 전` : `${hours}小时前`
  if (days < 7) return locale.value === 'ko' ? `${days}일 전` : `${days}天前`
  return d.toLocaleDateString(locale.value === 'ko' ? 'ko-KR' : 'zh-CN')
}

const toggleAiAssistant = () => {
  window.dispatchEvent(new CustomEvent('toggle-ai-assistant'))
}

const banners = [
  {
    image: BANNER_FALLBACK,
    title: 'AI智能识别 让交易更放心',
    titleKo: 'AI 스마트 인식으로 더 안심할 수 있는 거래'
  },
  {
    image: BANNER_FALLBACK,
    title: '安全交易保障 售后无忧',
    titleKo: '안전 거래 보장 및 확실한 사후 관리'
  }
]

const categories = CATEGORIES

const actions = ref([
  { label: t('nav.myProducts'), desc: t('common.view'), path: '/user/my-products', icon: IconPlus, bg: 'linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%)' },
  { label: t('nav.myOrders'), desc: t('common.order'), path: '/user/orders', icon: IconBag, bg: 'linear-gradient(135deg, rgba(6,182,212,0.85) 0%, rgba(139,92,246,0.85) 100%)' },
  { label: t('common.myFavorites'), desc: t('common.myFavorites'), path: '/user/favorites', icon: IconHeart, bg: 'linear-gradient(135deg, rgba(6,182,212,0.70) 0%, rgba(139,92,246,0.70) 100%)' }
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

const goCategory = (categoryId) => {
  router.push({
    path: '/user/products',
    query: { category: categoryId }
  })
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
      allNotices.value = records.map((notice) => ({
        type: notice.type === 0 ? 'system' : notice.type === 1 ? 'activity' : 'maintenance',
        typeLabel: notice.type === 0 ? (locale.value === 'zh' ? '系统' : '시스템') :
          notice.type === 1 ? (locale.value === 'zh' ? '活动' : '활동') :
            (locale.value === 'zh' ? '维护' : '유지보수'),
        title: locale.value === 'ko' && notice.titleKo ? notice.titleKo : notice.title,
        content: locale.value === 'ko' && notice.contentKo ? notice.contentKo : notice.content,
        date: formatNoticeDate(notice.publishTime || notice.createTime),
        publishTime: notice.publishTime,
        summary: (locale.value === 'ko' && notice.contentKo ? notice.contentKo : notice.content).replace(/<[^>]+>/g, '').substring(0, 50) + '...'
      }))
    }
  } catch (e) { }
}

const fetchUserStatistics = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await userStatisticsApi.getOverview()
    if (res.code === '200') {
      const data = res.data || {}
      actions.value = [
        { label: t('nav.myProducts'), desc: `${data.publishedProductCount || 0}`, path: '/user/my-products', icon: IconPlus, bg: 'linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%)' },
        { label: t('nav.myOrders'), desc: `${data.purchaseOrderCount || 0}`, path: '/user/orders', icon: IconBag, bg: 'linear-gradient(135deg, rgba(6,182,212,0.85) 0%, rgba(139,92,246,0.85) 100%)' },
        { label: t('common.myFavorites'), desc: `${data.favoriteCount || 0}`, path: '/user/favorites', icon: IconHeart, bg: 'linear-gradient(135deg, rgba(6,182,212,0.70) 0%, rgba(139,92,246,0.70) 100%)' }
      ]
    }
  } catch (e) { }
}

const fetchRecommendProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 12,
      status: 2,
      sortBy: 'createTime'
    }
    if (schoolStore.selectedSchool) params.schoolId = Number(schoolStore.selectedSchool)
    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      recommendProducts.value = res.data.records || []
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchNotices()
  fetchUserStatistics()
  fetchRecommendProducts()
})

watch(
  () => schoolStore.selectedSchool,
  () => {
    fetchRecommendProducts()
  }
)
</script>

<style scoped>
div::-webkit-scrollbar {
  display: none;
}
</style>
