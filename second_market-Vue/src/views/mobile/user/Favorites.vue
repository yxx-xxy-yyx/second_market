<template>
  <div class="min-h-screen bg-[#f8fafc]">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div class="space-y-4">
        <!-- 渐变背景头部 -->
        <div class="bg-gradient-to-br from-primary to-primaryDark pt-8 pb-16 px-6 rounded-b-[3rem] shadow-lg relative overflow-hidden">
          <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>

          <div class="relative z-10 flex items-center h-10 mb-3">
            <button @click="router.back()" class="absolute left-0 p-1 rounded-full hover:bg-white/10 active:scale-90 transition-all text-white">
              <ChevronLeftIcon class="w-7 h-7" />
            </button>
            <h1 class="w-full text-center text-lg font-bold text-white tracking-wide">{{ $t('favorites.pageTitle') }}</h1>
          </div>
          <div class="text-xs text-white/60 relative z-10">{{ $t('favorites.subtitle') }}</div>
        </div>

        <div class="px-3 -mt-8 relative z-20 space-y-3">
          <div v-if="loading && !refreshing && products.length === 0"
            class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
            {{ $t('common.loading') }}
          </div>

          <div v-else class="space-y-3 product-list">
            <div v-for="p in products" :key="p.id"
              class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden relative">
              <div class="flex gap-4 p-3 items-center">
                <!-- 图片区域：点击跳转详情 -->
                <div class="h-28 w-28 rounded-2xl bg-gray-100 overflow-hidden flex-none relative cursor-pointer"
                  @click="viewDetail(p.id)">
                  <img :src="getProductImage(p.images)" class="h-full w-full object-cover" loading="lazy" />
                  <!-- 售罄遮罩 -->
                  <div v-if="p.status === 4"
                    class="absolute inset-0 bg-black/50 flex items-center justify-center text-white text-xs font-bold">
                    {{ $t('favorites.soldOut') }}
                  </div>
                </div>

                <!-- 文字内容 -->
                <div class="min-w-0 flex-1 pr-10">
                  <div class="text-sm font-semibold text-gray-900 line-clamp-2 leading-snug">
                    {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
                  </div>
                  <div class="mt-2 flex items-baseline gap-1">
                    <span class="text-base font-bold text-red-500">{{ $t('common.currency') }}{{ p.price }}</span>
                    <span v-if="p.originalPrice" class="text-xs text-gray-400 line-through">{{ $t('common.currency') }}{{
                      p.originalPrice }}</span>
                  </div>
                  <div class="mt-2 text-[11px] text-gray-400 flex items-center">
                    <span>{{ $t('product.viewCount') }} {{ p.viewCount || 0 }}</span>
                    <span class="mx-1.5">·</span>
                    <span class="text-orange-500 font-medium">{{ $t('product.condition') }} {{ p.conditionScore }}</span>
                  </div>
                </div>

                <!-- 右侧操作区：上下居中 -->
                <div class="absolute right-3 top-1/2 -translate-y-1/2 flex flex-col space-y-4 items-center">
                  <!-- 收藏按钮 (实心) -->
                  <button class="text-red-500 active:scale-125 transition-transform" @click.stop="removeFavorite(p)">
                    <el-icon :size="22">
                      <StarFilled />
                    </el-icon>
                  </button>
                  <!-- AI评估按钮 -->
                  <button v-if="p.aiAnalyzed" class="text-blue-500 active:scale-125 transition-transform"
                    @click.stop="viewDetail(p.id)">
                    <el-icon :size="22">
                      <MagicStick />
                    </el-icon>
                  </button>
                </div>
              </div>
            </div>

            <div v-if="products.length === 0 && !loading"
              class="rounded-2xl bg-white border border-gray-100 shadow-sm p-10 text-sm text-gray-500 text-center">
              <div>{{ $t('favorites.noFavorites') }}</div>
              <button class="mt-3 text-sm text-blue-500 font-medium" @click="goShopping">{{ $t('favorites.goShopping')
              }}</button>
            </div>

            <!-- 加载更多 -->
            <div v-if="hasMore" class="py-6 flex justify-center">
              <button
                class="px-8 py-2.5 bg-white rounded-full shadow-sm border border-gray-100 text-sm text-gray-500 active:bg-gray-50"
                :disabled="loading" @click="loadMore">
                {{ loading ? $t('common.loading') : $t('common.loadMore') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { favoriteApi } from '@/api/favorite'
import { formatImageUrl } from '@/utils/url'
import { StarFilled, MagicStick, Refresh } from '@element-plus/icons-vue'
import { ChevronLeftIcon } from '@heroicons/vue/24/outline'

const router = useRouter()
const { t, locale } = useI18n()

const loading = ref(false)
const refreshing = ref(false)
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const hasMore = ref(true) // 是否还有更多数据

const isOk = (res) => res && (res.code === '200' || res.success)

const normalizeFavorites = (records) => {
  return (records || []).map((favorite) => ({
    id: favorite.productId,
    title: favorite.productTitle || t('favorites.unknownProduct'),
    titleKo: favorite.productTitleKo || '',
    images: favorite.productImages,
    price: favorite.productPrice,
    originalPrice: favorite.productOriginalPrice,
    status: favorite.productStatus, // 0:上架, 1:下架, 2:待审核, 3:审核拒绝, 4:已售出
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

const loadFavorites = async (isLoadMore = false) => {
  if (!refreshing.value) loading.value = true
  try {
    const res = await favoriteApi.getMyFavorites({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })

    if (isOk(res)) {
      const newProducts = normalizeFavorites(res.data.records)
      products.value = isLoadMore ? [...products.value, ...newProducts] : newProducts
      total.value = res.data.total || 0
      hasMore.value = newProducts.length === pageSize.value // 判断是否还有更多数据
      return
    }

    ElMessage.error(res.message || t('favorites.getFavoritesFailed'))
  } catch (e) {
    ElMessage.error(e?.message || t('favorites.getFavoritesFailed'))
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onRefresh = () => {
  refreshing.value = true
  pageNum.value = 1
  loadFavorites(false)
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
      loadFavorites() // 重新加载当前页数据
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
  loadFavorites();
});

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    pageNum.value++;
    loadFavorites(true);
  }
}
</script>
