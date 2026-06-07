<template>
  <div class="px-4 py-4 space-y-5">
    <div class="rounded-2xl overflow-hidden bg-white shadow-sm border border-gray-100">
      <el-carousel height="220px" :interval="4000" arrow="never" indicator-position="bottom">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="relative h-full w-full">
            <img
              :src="item.image"
              class="absolute inset-0 h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleBannerImgError"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-black/50 to-black/10"></div>
            <div class="absolute left-5 right-5 bottom-5 text-white">
              <div class="text-lg font-semibold leading-snug">
                {{ locale === 'ko' ? item.titleKo : item.title }}
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="grid grid-cols-3 gap-3">
      <button
        v-for="action in actions"
        :key="action.path"
        class="rounded-2xl bg-white border border-gray-100 shadow-sm px-4 py-4 text-left active:scale-[0.99] transition"
        @click="router.push(action.path)"
      >
        <div class="h-10 w-10 rounded-2xl flex items-center justify-center text-white shadow-sm" :style="{ background: action.bg }">
          <el-icon :size="22"><component :is="action.icon" /></el-icon>
        </div>
        <div class="mt-3 text-sm font-semibold text-gray-900">{{ action.label }}</div>
        <div class="text-xs text-gray-500 mt-1">{{ action.desc }}</div>
      </button>
    </div>

    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('nav.categories') }}</div>
        <button class="text-sm sm-text-primary" @click="router.push('/user/categories')">{{ $t('common.view') }}</button>
      </div>
      <CategoryGrid :items="categories" :columns="4" :gap="12" :icon-size="22" @select="goCategory" />
    </div>

    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between mb-3">
        <div class="text-sm font-semibold text-gray-900">{{ $t('dashboard.recommend') }}</div>
        <button class="text-sm sm-text-primary" @click="router.push('/user/products')">{{ $t('dashboard.viewMore') }}</button>
      </div>

      <div v-loading="loading" class="grid grid-cols-2 gap-3">
        <button
          v-for="p in recommendProducts.slice(0, 8)"
          :key="p.id"
          class="rounded-2xl bg-white border border-gray-100 overflow-hidden text-left shadow-sm active:scale-[0.99] transition"
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { productApi } from '@/api/product'
import { DEFAULT_PRODUCT_IMAGE, formatProductImageUrl } from '@/utils/url'
import { CATEGORIES, categoryIdToDbValue } from '@/utils/categories'
import CategoryGrid from '@/components/category/CategoryGrid.vue'
import IconPlus from '@/components/icons/IconPlus.vue'
import IconBag from '@/components/icons/IconBag.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import {
  View,
} from '@element-plus/icons-vue'

const router = useRouter()
const { locale, t } = useI18n()

const loading = ref(false)
const recommendProducts = ref([])

const DEFAULT_IMAGE = DEFAULT_PRODUCT_IMAGE

const BANNER_FALLBACK =
  "data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='400'><defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'><stop offset='0' stop-color='%2306b6d4'/><stop offset='1' stop-color='%238b5cf6'/></linearGradient></defs><rect width='1200' height='400' fill='url(%23g)'/><text x='60' y='210' fill='white' font-family='Arial' font-size='44' font-weight='700'>SecondMarket</text><text x='60' y='270' fill='white' font-family='Arial' font-size='22' opacity='0.9'>Campus second-hand marketplace</text></svg>"

const handleBannerImgError = (e) => {
  if (e?.target) e.target.src = BANNER_FALLBACK
}

const banners = [
  {
    image: BANNER_FALLBACK,
    title: 'AI智能识别 让交易更放心',
    titleKo: 'AI 스마트 인식으로 더 안심할 수 있는 거래'
  },
  {
    image: BANNER_FALLBACK,
    title: '优质二手商品 物超所值',
    titleKo: '가성비 최고의 프리미엄 중고 상품'
  }
]

const actions = [
  { label: t('nav.myProducts'), desc: t('common.view'), path: '/user/my-products', icon: IconPlus, bg: 'linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%)' },
  { label: t('nav.myOrders'), desc: t('common.order'), path: '/user/orders', icon: IconBag, bg: 'linear-gradient(135deg, rgba(6,182,212,0.85) 0%, rgba(139,92,246,0.85) 100%)' },
  { label: t('common.myFavorites'), desc: t('common.mine'), path: '/user/favorites', icon: IconHeart, bg: 'linear-gradient(135deg, rgba(6,182,212,0.70) 0%, rgba(139,92,246,0.70) 100%)' }
]

const categories = CATEGORIES

const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0]
    if (!firstImage) return DEFAULT_IMAGE
    return formatProductImageUrl(firstImage)
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

const fetchRecommendProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 12,
      status: 2,
      sortBy: 'createTime'
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      recommendProducts.value = res.data.records || []
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRecommendProducts()
})
</script>
