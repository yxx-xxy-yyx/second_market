<template>
  <div class="px-3 py-4 space-y-4">
    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-4">
      <div class="flex items-center justify-between">
        <div class="text-base font-semibold text-gray-900">{{ $t('favorites.pageTitle') }}</div>
        <button class="text-sm sm-text-primary" @click="refresh">{{ $t('common.refresh') }}</button>
      </div>
      <div class="text-xs text-gray-500 mt-1">{{ $t('favorites.subtitle') }}</div>
    </div>

    <div v-if="loading" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
      {{ $t('common.loading') }}
    </div>

    <div v-else class="space-y-3">
      <div
        v-for="p in products"
        :key="p.id"
        class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden"
      >
        <div class="flex gap-3 p-3">
          <div class="h-20 w-20 rounded-2xl bg-gray-100 overflow-hidden flex-none">
            <img
              :src="getProductImage(p.images)"
              class="h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleImgError"
            />
          </div>
          <div class="min-w-0 flex-1">
            <div class="text-sm font-semibold text-gray-900 line-clamp-2" @click="viewDetail(p.id)">
              {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
            </div>
            <div class="mt-1 flex items-center gap-2">
              <span class="text-sm font-bold sm-text-primary">{{ $t('common.currency') }}{{ p.price }}</span>
              <span v-if="p.originalPrice" class="text-xs text-gray-400 line-through">{{ $t('common.currency') }}{{ p.originalPrice }}</span>
              <el-tag v-if="p.status === 4" size="small" type="danger">{{ $t('favorites.soldOut') }}</el-tag>
            </div>
            <div class="mt-1 text-xs text-gray-500">{{ $t('product.viewCount') }} {{ p.viewCount || 0 }}</div>
          </div>
        </div>

        <div class="px-3 pb-3 flex items-center justify-between">
          <button class="text-sm text-gray-600" @click="viewDetail(p.id)">{{ $t('favorites.viewDetail') }}</button>
          <button class="text-sm text-red-500" @click="removeFavorite(p)">{{ $t('favorites.removeFavorite') }}</button>
        </div>
      </div>

      <div v-if="products.length === 0" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
        <div>{{ $t('favorites.noFavorites') }}</div>
        <button class="mt-3 text-sm sm-text-primary" @click="goShopping">{{ $t('favorites.goShopping') }}</button>
      </div>
    </div>

    <div v-if="total > 0" class="pt-2 flex items-center justify-between">
      <button class="text-sm text-gray-700" :disabled="pageNum === 1" @click="prevPage">
        {{ $t('pagination.prev') }}
      </button>
      <div class="text-xs text-gray-500">
        {{ pageNum }}/{{ totalPages }}
      </div>
      <button class="text-sm text-gray-700" :disabled="pageNum >= totalPages" @click="nextPage">
        {{ $t('pagination.next') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { favoriteApi } from '@/api/favorite'
import { formatImageUrl } from '@/utils/url'

const router = useRouter()
const { t, locale } = useI18n()

const loading = ref(false)
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const isOk = (res) => res && (res.code === '200' || res.success)

const normalizeFavorites = (records) => {
  return (records || []).map((favorite) => ({
    id: favorite.productId,
    title: favorite.productTitle || t('favorites.unknownProduct'),
    titleKo: favorite.productTitleKo || '',
    images: favorite.productImages,
    price: favorite.productPrice,
    originalPrice: favorite.productOriginalPrice,
    status: favorite.productStatus,
    conditionScore: favorite.productConditionScore || 5,
    viewCount: favorite.productViewCount || 0,
    favoriteCount: favorite.productFavoriteCount || 0,
    aiAnalyzed: favorite.productAiAnalyzed || 0,
    createTime: favorite.createTime
  }))
}

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const parsed = typeof images === 'string' ? JSON.parse(images) : images
    const first = Array.isArray(parsed) ? parsed[0] : parsed
    return formatImageUrl(first) || ''
  } catch {
    return ''
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = ''
}

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await favoriteApi.getMyFavorites({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })

    if (isOk(res)) {
      products.value = normalizeFavorites(res.data.records)
      total.value = res.data.total || 0
      return
    }

    ElMessage.error(res.message || t('favorites.getFavoritesFailed'))
  } catch (e) {
    ElMessage.error(e?.message || t('favorites.getFavoritesFailed'))
  } finally {
    loading.value = false
  }
}

const refresh = () => {
  pageNum.value = 1
  loadFavorites()
}

const prevPage = () => {
  if (pageNum.value <= 1) return
  pageNum.value -= 1
  loadFavorites()
}

const nextPage = () => {
  if (pageNum.value >= totalPages.value) return
  pageNum.value += 1
  loadFavorites()
}

const viewDetail = (productId) => {
  router.push(`/user/product/${productId}`)
}

const removeFavorite = async (product) => {
  try {
    await ElMessageBox.confirm(
      t('favorites.confirmRemoveFavorite', { title: product.title }),
      t('favorites.removeFavoriteTitle'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
      }
    )
    const res = await favoriteApi.deleteFavorite(product.id)
    if (isOk(res)) {
      ElMessage.success(t('favorites.removeFavoriteSuccess'))
      if (products.value.length === 1 && pageNum.value > 1) pageNum.value -= 1
      loadFavorites()
      return
    }
    ElMessage.error(res.message || t('favorites.removeFavoriteFailed'))
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.message || t('favorites.removeFavoriteFailed'))
  }
}

const goShopping = () => {
  router.push('/user/products')
}

onMounted(() => {
  loadFavorites()
})
</script>
