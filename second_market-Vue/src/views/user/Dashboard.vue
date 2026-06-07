<template>
  <div class="dashboard-wrapper">
    <!-- 移动端视图 -->
    <template v-if="isMobileScreen">
      <!-- 页面内容：公告 + 轮播 + 推荐商品 -->
      <div class="px-3 py-4 space-y-5 pb-24">
        <!-- 公告轮播 -->
        <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
          <div class="h-auto px-3 py-2 flex items-center gap-2">
            <el-icon class="sm-text-primary flex-shrink-0" :size="18"><Bell /></el-icon>
            <div class="flex-1 min-w-0">
              <el-carousel height="32px" direction="vertical" :autoplay="true" :interval="4000" indicator-position="none" arrow="never">
                <el-carousel-item v-for="(notice, index) in allNotices" :key="index" class="flex items-center">
                  <div class="w-full h-8 flex items-center gap-2 cursor-pointer hover:bg-gray-50 px-2 rounded transition" @click="handleNoticeClick(notice)">
                    <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'" class="rounded-md flex-shrink-0">
                      {{ getNoticeTypeLabel(notice.type) }}
                    </el-tag>
                    <span class="flex-1 min-w-0 text-sm text-gray-900 truncate font-medium">
                      {{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}
                    </span>
                  </div>
                </el-carousel-item>
              </el-carousel>
            </div>
          </div>
        </div>

        <!-- 轮播图 -->
        <div class="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm">
          <el-carousel height="190px" :interval="4200" arrow="never" indicator-position="bottom">
            <el-carousel-item v-for="(item, index) in banners" :key="index">
              <div class="relative h-full w-full">
                <img :src="item.image" class="absolute inset-0 h-full w-full object-cover" @error="handleBannerImgError" />
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

        <!-- AI功能入口 -->
        <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <div class="h-8 w-8 rounded-xl bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center animate-pulse">
                <el-icon class="text-white" :size="16"><MagicStick /></el-icon>
              </div>
              <div class="text-sm font-semibold text-gray-900">{{ $t('ai.title') }}</div>
            </div>
          </div>
          <div class="grid grid-cols-4 gap-3">
            <button
              v-for="a in aiQuickActions"
              :key="a.key"
              class="rounded-2xl bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-100 shadow-md hover:shadow-lg hover:-translate-y-1 transition-all duration-300 px-2 py-3 text-center"
              @click="router.push(a.path)"
            >
              <div class="h-10 w-10 rounded-xl flex items-center justify-center text-white shadow-lg mx-auto transition-transform hover:scale-110" :style="{ background: a.bg }">
                <el-icon :size="18"><component :is="a.icon" /></el-icon>
              </div>
              <div class="mt-2 text-[11px] font-bold text-gray-900 text-center truncate">{{ a.label }}</div>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-4 gap-3">
          <button
            v-for="a in quickActions"
            :key="a.key"
            class="rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-100 shadow-md hover:shadow-lg hover:-translate-y-1 transition-all duration-300 px-3 py-4 text-center"
            @click="router.push(a.path)"
          >
            <div class="h-10 w-10 rounded-2xl flex items-center justify-center text-white shadow-md mx-auto transition-transform hover:scale-110" :style="{ background: a.bg }">
              <el-icon :size="20"><component :is="a.icon" /></el-icon>
            </div>
            <div class="mt-3 text-xs font-bold text-gray-900">{{ a.label }}</div>
          </button>
        </div>

        <!-- 分类网格 -->
        <div class="grid grid-cols-5 gap-y-4 py-2">
          <div v-for="cat in mobileCategories" :key="cat.id" class="flex flex-col items-center gap-2 active:scale-90 transition-all duration-200 cursor-pointer group" @click="handleCategoryClick(cat.id)">
            <div class="w-12 h-12 rounded-2xl flex items-center justify-center bg-gradient-to-br from-white to-gray-50 shadow-md group-hover:shadow-xl group-hover:-translate-y-1 transition-all duration-300 border border-gray-100">
              <CategoryIcon :category-id="cat.id" :size="26" />
            </div>
            <span class="text-[11px] text-gray-700 font-semibold">{{ $t(`categories.${cat.id}`) }}</span>
          </div>
        </div>

        <!-- AI推荐商品 -->
        <AiRecommendations />

        <!-- 推荐商品 -->
        <div class="rounded-2xl bg-white border border-gray-100 shadow-md p-4">
          <div class="flex items-center justify-between mb-4">
            <div class="text-base font-bold text-gray-900">{{ $t('dashboard.recommend') }}</div>
            <button class="text-sm font-medium sm-text-primary bg-primary" @click="router.push('/user/products')">{{ $t('dashboard.viewMore') }} →</button>
          </div>
          <div v-loading="loading" class="grid grid-cols-2 gap-4">
            <button v-for="p in recommendProducts" :key="p.id" class="rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-100 overflow-hidden shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300 text-left" @click="router.push(`/user/product/${p.id}`)">
              <div class="w-full aspect-[4/3] bg-gradient-to-br from-gray-50 to-gray-100 overflow-hidden">
                <img :src="getProductImage(p.images)" class="h-full w-full object-cover hover:scale-110 transition-transform duration-300" @error="handleImgError" />
              </div>
              <div class="p-4">
                <div class="text-sm font-semibold text-gray-900 line-clamp-2">
                  {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
                </div>
                <div class="mt-2">
                  <div class="text-lg font-bold sm-text-primary">
                    {{ $t('common.currency') }}{{ p.price }}
                  </div>
                </div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </template>

    <template v-else-if="isTabletScreen">
      <div class="px-4 py-4 space-y-5 pb-24">
        <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
          <div class="h-auto px-4 py-3 flex items-center gap-3">
            <el-icon class="text-primary flex-shrink-0" :size="20"><Bell /></el-icon>
            <el-carousel height="36px" direction="vertical" :autoplay="true" :interval="3500" indicator-position="none" arrow="never" class="flex-1">
              <el-carousel-item v-for="(notice, index) in allNotices" :key="index" class="flex items-center">
                <div class="w-full h-9 flex items-center gap-2 cursor-pointer hover:bg-gray-50 px-2 rounded transition" @click="handleNoticeClick(notice)">
                  <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'" class="rounded-md flex-shrink-0">
                    {{ getNoticeTypeLabel(notice.type) }}
                  </el-tag>
                  <span class="flex-1 min-w-0 text-sm text-gray-900 truncate font-medium">
                    {{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}
                  </span>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </div>

        <div class="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm">
          <el-carousel height="220px" :interval="4200" arrow="never" indicator-position="bottom">
            <el-carousel-item v-for="(item, index) in banners" :key="index">
              <div class="relative h-full w-full">
                <img :src="item.image" class="absolute inset-0 h-full w-full object-cover" @error="handleBannerImgError" />
                <div class="absolute inset-0 bg-gradient-to-t from-black/55 to-black/10"></div>
                <div class="absolute left-5 right-5 bottom-5 text-white">
                  <div class="text-lg font-semibold leading-snug">
                    {{ item.title }}
                  </div>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- AI功能入口 -->
        <div class="rounded-2xl bg-white border border-gray-100 shadow-lg p-4">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <div class="h-10 w-10 rounded-xl bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center animate-pulse">
                <el-icon class="text-white" :size="20"><MagicStick /></el-icon>
              </div>
              <div class="text-lg font-bold text-gray-900">{{ $t('ai.title') }}</div>
            </div>
          </div>
          <div class="grid grid-cols-4 gap-4">
            <button
              v-for="a in aiQuickActions"
              :key="a.key"
              class="rounded-2xl bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-200 shadow-md hover:shadow-xl hover:-translate-y-2 transition-all duration-300 px-4 py-5 text-center"
              @click="router.push(a.path)"
            >
              <div class="h-12 w-12 rounded-xl flex items-center justify-center text-white shadow-lg mx-auto transition-transform hover:scale-115" :style="{ background: a.bg }">
                <el-icon :size="24"><component :is="a.icon" /></el-icon>
              </div>
              <div class="mt-3 text-sm font-bold text-gray-900">{{ a.label }}</div>
              <div class="text-xs text-gray-500 mt-1">{{ a.desc }}</div>
            </button>
          </div>
        </div>

        <div class="grid grid-cols-4 gap-4">
          <button
            v-for="a in quickActions"
            :key="a.key"
            class="rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-100 shadow-md hover:shadow-lg hover:-translate-y-1 transition-all duration-300 px-4 py-5 text-center"
            @click="router.push(a.path)"
          >
            <div class="h-12 w-12 rounded-2xl flex items-center justify-center text-white shadow-md mx-auto transition-transform hover:scale-110" :style="{ background: a.bg }">
              <el-icon :size="24"><component :is="a.icon" /></el-icon>
            </div>
            <div class="mt-3 text-sm font-bold text-gray-900">{{ a.label }}</div>
            <div class="text-xs text-gray-500 mt-1">{{ a.desc }}</div>
          </button>
        </div>

        <div class="rounded-2xl bg-white border border-gray-100 shadow-md p-4">
          <div class="flex items-center justify-between mb-4">
            <div class="text-base font-bold text-gray-900">{{ $t('dashboard.quickCategory') }}</div>
            <button class="text-sm font-medium sm-text-primary" @click="router.push('/user/categories')">{{ $t('common.view') }} →</button>
          </div>
          <div class="grid grid-cols-8 gap-y-5 py-2">
            <div v-for="cat in CATEGORIES.slice(0, 16)" :key="cat.id" class="flex flex-col items-center gap-2 active:scale-90 transition-all duration-200 cursor-pointer group" @click="handleCategoryClick(cat.id)">
              <div class="w-14 h-14 rounded-2xl flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 group-hover:bg-primary/10 transition-all duration-300 shadow-md group-hover:shadow-xl group-hover:-translate-y-1 border border-gray-100">
                <CategoryIcon :category-id="cat.id" :size="28" />
              </div>
              <span class="text-[11px] text-gray-700 font-semibold">{{ $t(`categories.${cat.id}`) }}</span>
            </div>
          </div>
        </div>

        <!-- AI推荐商品 -->
        <AiRecommendations />

        <div class="rounded-2xl bg-white border border-gray-100 shadow-md p-4">
          <div class="flex items-center justify-between mb-4">
            <div class="text-base font-bold text-gray-900">{{ $t('dashboard.recommend') }}</div>
            <button class="text-sm font-medium sm-text-primary" @click="router.push('/user/products')">{{ $t('dashboard.viewMore') }} →</button>
          </div>
          <div v-loading="loading" class="grid grid-cols-3 gap-5">
            <button
              v-for="p in recommendProducts.slice(0, 9)"
              :key="p.id"
              class="rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-100 overflow-hidden text-left shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
              @click="router.push(`/user/product/${p.id}`)"
            >
              <div class="w-full aspect-[4/3] bg-gradient-to-br from-gray-50 to-gray-100 overflow-hidden">
                <img :src="getProductImage(p.images)" class="h-full w-full object-cover hover:scale-110 transition-transform duration-300" @error="handleImgError" />
              </div>
              <div class="p-4">
                <div class="text-sm font-semibold text-gray-900 line-clamp-2">
                  {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
                </div>
                <div class="mt-3 flex items-center justify-between">
                  <div class="text-xl font-bold sm-text-primary">{{ $t('common.currency') }}{{ p.price }}</div>
                  <div class="text-xs text-gray-500 flex items-center gap-1">
                    <el-icon :size="14"><View /></el-icon>
                    <span>{{ p.viewCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </template>

    <!-- 桌面端视图 -->
    <template v-else>
      <div class="user-dashboard p-6 max-w-7xl mx-auto space-y-6 pb-24">
        <!-- 平台公告 -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 px-4 py-2 flex items-center gap-3">
          <el-icon class="text-primary" :size="20"><Bell /></el-icon>
          <el-carousel height="40px" direction="vertical" :autoplay="true" :interval="3000" indicator-position="none" arrow="never" class="flex-1">
            <el-carousel-item v-for="(notice, index) in allNotices" :key="index">
              <div class="flex items-center gap-3 h-full cursor-pointer" @click="handleNoticeClick(notice)">
                <el-tag size="small" :type="notice.type === 'system' ? 'primary' : 'warning'">{{ getNoticeTypeLabel(notice.type) }}</el-tag>
                <span class="text-sm text-gray-700 truncate">{{ locale === 'ko' && notice.titleKo ? notice.titleKo : notice.title }}</span>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <!-- 顶部区域 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="md:col-span-2 rounded-2xl overflow-hidden shadow-sm h-[400px]">
            <el-carousel height="400px" :interval="4000">
              <el-carousel-item v-for="(item, index) in banners" :key="index">
                <img :src="item.image" class="w-full h-full object-cover" />
                <div class="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-black/60 to-transparent text-white">
                  <h3 class="text-xl font-bold">{{ item.title }}</h3>
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          <div class="space-y-6">
            <!-- AI功能区域 -->
            <div class="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
              <div class="flex items-center gap-3 mb-4">
                <div class="h-10 w-10 rounded-xl bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center animate-pulse">
                  <el-icon class="text-white" :size="20"><MagicStick /></el-icon>
                </div>
                <h3 class="text-xl font-bold text-gray-900">{{ $t('ai.title') }}</h3>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <button
                  v-for="a in aiQuickActions"
                  :key="a.key"
                  class="rounded-xl bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-200 p-5 text-left hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
                  @click="router.push(a.path)"
                >
                  <div class="flex items-center gap-4">
                    <div class="h-12 w-12 rounded-xl flex items-center justify-center text-white shadow-lg transition-transform hover:scale-110" :style="{ background: a.bg }">
                      <el-icon :size="22"><component :is="a.icon" /></el-icon>
                    </div>
                    <div class="min-w-0">
                      <div class="text-base font-bold text-gray-900 truncate">{{ a.label }}</div>
                      <div class="text-sm text-gray-500 truncate">{{ a.desc }}</div>
                    </div>
                  </div>
                </button>
              </div>
            </div>
            <div class="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
              <h3 class="text-xl font-bold mb-4 text-gray-900">{{ $t('dashboard.quickActions') }}</h3>
              <div class="grid grid-cols-2 gap-4">
                <button
                  v-for="a in quickActions"
                  :key="a.key"
                  class="rounded-xl bg-gradient-to-br from-gray-50 to-gray-100 border border-gray-200 p-5 text-left hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
                  @click="router.push(a.path)"
                >
                  <div class="flex items-center gap-4">
                    <div class="h-12 w-12 rounded-xl flex items-center justify-center text-white shadow-lg transition-transform hover:scale-110" :style="{ background: a.bg }">
                      <el-icon :size="22"><component :is="a.icon" /></el-icon>
                    </div>
                    <div class="min-w-0">
                      <div class="text-base font-bold text-gray-900 truncate">{{ a.label }}</div>
                      <div class="text-sm text-gray-500 truncate">{{ a.desc }}</div>
                    </div>
                  </div>
                </button>
              </div>
            </div>
            <div class="bg-white p-6 rounded-2xl shadow-lg border border-gray-100">
              <h3 class="text-xl font-bold mb-4 text-gray-900">{{ $t('dashboard.quickCategory') }}</h3>
              <div class="grid grid-cols-4 gap-5">
                <div v-for="cat in CATEGORIES.slice(0, 8)" :key="cat.id" class="flex flex-col items-center gap-3 cursor-pointer group" @click="handleCategoryClick(cat.id)">
                  <div class="w-14 h-14 rounded-2xl bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center group-hover:bg-primary/10 transition-all duration-300 shadow-md group-hover:shadow-xl group-hover:-translate-y-1">
                    <CategoryIcon :category-id="cat.id" :size="28" />
                  </div>
                  <span class="text-sm font-medium text-gray-600">{{ $t(`categories.${cat.id}`) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- AI推荐商品 -->
        <AiRecommendations />

        <!-- 商品展示 -->
        <div class="space-y-6">
          <div class="flex items-center justify-between">
            <h2 class="text-2xl font-bold text-gray-900">{{ $t('dashboard.recommend') }}</h2>
            <el-button type="primary" round @click="router.push('/user/products')">{{ $t('dashboard.viewMore') }} →</el-button>
          </div>
          <div v-loading="loading" class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-6">
            <div v-for="p in recommendProducts" :key="p.id" class="bg-white rounded-2xl border border-gray-100 overflow-hidden hover:shadow-xl hover:-translate-y-2 transition-all duration-300 cursor-pointer group" @click="router.push(`/user/product/${p.id}')">
              <div class="aspect-square bg-gradient-to-br from-gray-50 to-gray-100 overflow-hidden">
                <img :src="getProductImage(p.images)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
              </div>
              <div class="p-4">
                <h4 class="text-sm font-semibold text-gray-900 line-clamp-2 mb-2">{{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}</h4>
                <div class="text-xl font-bold text-primary">{{ $t('common.currency') }}{{ p.price }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 公告弹窗 -->
    <el-dialog v-model="showNoticeDetail" :title="selectedNotice?.title" :width="isMobileScreen ? '92%' : '500px'">
      <div v-if="selectedNotice" class="space-y-4">
        <div class="flex items-center justify-between">
          <el-tag :type="selectedNotice.type === 'system' ? 'primary' : 'warning'">{{ getNoticeTypeLabel(selectedNotice.type) }}</el-tag>
          <span class="text-xs text-gray-400">{{ selectedNotice.date }}</span>
        </div>
        <div class="text-sm text-gray-700 leading-relaxed">{{ selectedNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Bell, View, Plus, ShoppingCart, Star, Clock, MagicStick, TrendCharts, Search, Document, ChatDotRound, School, Odometer, Warning } from '@element-plus/icons-vue'
import { useSchoolStore } from '@/stores/school'
import { useDeviceType } from '@/utils/device'
import { productApi } from '@/api/product'
import { noticeApi } from '@/api/admin'
import { DEFAULT_PRODUCT_IMAGE, formatProductImageUrl } from '@/utils/url'
import { CATEGORIES } from '@/utils/categories'
import CategoryIcon from '@/components/icons/CategoryIcon.vue'
import AiRecommendations from '@/components/AiRecommendations.vue'

const router = useRouter()
const schoolStore = useSchoolStore()
const { t, locale } = useI18n()
const { isMobileScreen, isTabletScreen } = useDeviceType()

const loading = ref(false)
const showNoticeDetail = ref(false)
const selectedNotice = ref(null)
const allNotices = ref([])
const recommendProducts = ref([])

const mobileCategories = computed(() => [
  { id: 'campus-nearby', isService: true },
  ...CATEGORIES.slice(0, 9)
])

const banners = computed(() => ([
  { image: DEFAULT_PRODUCT_IMAGE, title: t('dashboard.banner.aiSafe') },
  { image: DEFAULT_PRODUCT_IMAGE, title: t('dashboard.banner.tradeSafe') }
]))

const quickActions = computed(() => ([
  { key: 'publish', label: t('dashboard.stats.publish'), desc: t('nav.home'), path: '/user/publish', icon: Plus, bg: 'linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%)' },
  { key: 'orders', label: t('dashboard.stats.orders'), desc: t('ordersPage.title'), path: '/user/orders', icon: ShoppingCart, bg: 'linear-gradient(135deg, rgba(6,182,212,0.85) 0%, rgba(139,92,246,0.85) 100%)' },
  { key: 'favorites', label: t('dashboard.stats.favorites'), desc: t('favoritesPage.title'), path: '/user/favorites', icon: Star, bg: 'linear-gradient(135deg, rgba(6,182,212,0.70) 0%, rgba(139,92,246,0.70) 100%)' },
  { key: 'history', label: t('dashboard.stats.history'), desc: t('profilePage.menu.history'), path: '/user/recently-viewed', icon: Clock, bg: 'linear-gradient(135deg, rgba(6,182,212,0.65) 0%, rgba(139,92,246,0.65) 100%)' }
]))

const aiQuickActions = computed(() => ([
  { key: 'ai-publish', label: t('ai.oneClickPublish'), desc: t('ai.oneClickPublishDesc'), path: '/user/publish', icon: MagicStick, bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { key: 'ai-authenticate', label: t('ai.authenticate'), desc: t('ai.authenticateDesc'), path: '/user/ai-authenticate', icon: Document, bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { key: 'ai-market', label: t('ai.marketTrend'), desc: t('ai.marketTrendDesc'), path: '/user/ai-market', icon: TrendCharts, bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { key: 'ai-chat', label: t('ai.chatAssistant'), desc: t('ai.chatAssistantDesc'), path: '/user/ai-chat', icon: ChatDotRound, bg: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
]))

const handleCategoryClick = (id) => {
  if (id === 'campus-nearby') {
    router.push('/user/campus-nearby')
  } else {
    router.push({ path: '/user/products', query: { categoryId: id } })
  }
}

const handleNoticeClick = (notice) => {
  selectedNotice.value = notice
  showNoticeDetail.value = true
}

const getNoticeTypeLabel = (type) => {
  if (type === 'system') return t('dashboard.noticeTypes.system')
  if (type === 'activity') return t('dashboard.noticeTypes.activity')
  if (type === 'maintenance') return t('dashboard.noticeTypes.maintenance')
  return type
}

const getProductImage = (images) => {
  if (!images) return DEFAULT_PRODUCT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return DEFAULT_PRODUCT_IMAGE
  }
}

const handleImgError = (e) => { if (e.target) e.target.src = DEFAULT_PRODUCT_IMAGE }
const handleBannerImgError = (e) => { if (e.target) e.target.src = DEFAULT_PRODUCT_IMAGE }

const fetchNotices = async () => {
  try {
    const res = await noticeApi.getList(1, 10, 1)
    if (res.code === '200') {
      allNotices.value = res.data.records.map(n => ({
        ...n,
        type: n.type === 0 ? 'system' : 'activity',
        date: new Date(n.publishTime).toLocaleDateString()
      }))
    }
  } catch (e) {}
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 12, status: 2 }
    if (schoolStore.selectedSchool) params.schoolId = Number(schoolStore.selectedSchool)
    const res = await productApi.getProductList(params)
    if (res.code === '200') recommendProducts.value = res.data.records
  } finally { loading.value = false }
}

onMounted(() => {
  fetchNotices()
  fetchProducts()
})

watch(() => schoolStore.selectedSchool, fetchProducts)
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
