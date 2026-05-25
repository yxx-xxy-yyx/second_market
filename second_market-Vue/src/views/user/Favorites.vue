<template>
  <div class="favorites-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-[#f8fafc] pb-24">
        <div class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-100 px-4 py-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon class="text-xl text-gray-700 cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
            <h1 class="text-lg font-bold text-gray-900">{{ $t('favoritesPage.title') }}</h1>
          </div>
          <LangSwitcher />
        </div>

        <div class="px-3 pt-3">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-1 flex">
            <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'product' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'product'">{{ $t('favoritesPage.tabs.product') }}</button>
            <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'post' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'post'">{{ $t('favoritesPage.tabs.post') }}</button>
          </div>
        </div>

        <div class="px-3 py-4 space-y-4">
          <template v-if="tab === 'product'">
            <div v-if="loading && products.length === 0" class="text-center py-10 text-gray-400 text-sm">{{ $t('favoritesPage.loading') }}</div>
            <div v-else class="space-y-3">
              <div v-for="p in products" :key="p.id" class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden relative p-3 flex gap-4">
                <div class="h-24 w-24 rounded-xl bg-gray-100 overflow-hidden flex-none cursor-pointer" @click="router.push(`/user/product/${p.id}`)">
                  <img :src="getProductImage(p.images)" class="h-full w-full object-cover" />
                </div>
                <div class="flex-1 min-w-0 pr-8">
                  <div class="text-sm font-bold text-gray-900 line-clamp-2">{{ p.title }}</div>
                  <div class="mt-2 text-primary font-bold">¥{{ p.price }}</div>
                  <div class="mt-1 text-[10px] text-gray-400">{{ $t('favoritesPage.views') }} {{ p.viewCount || 0 }} · {{ $t('favoritesPage.condition') }} {{ p.conditionScore }}</div>
                </div>
                <button class="absolute right-3 top-1/2 -translate-y-1/2 text-red-500" @click.stop="removeFavorite(p.id)">
                  <el-icon :size="20"><StarFilled /></el-icon>
                </button>
              </div>
              <el-empty v-if="products.length === 0 && !loading" :description="$t('favoritesPage.empty')" />
            </div>
          </template>

          <template v-else>
            <div v-loading="postLoading" class="space-y-3">
              <div
                v-for="p in postFavorites"
                :key="p.id"
                class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden relative p-4"
                @click="router.push(`/user/forum/detail/${p.id}`)"
              >
                <div class="flex items-start justify-between gap-3">
                  <div class="min-w-0">
                    <div class="text-sm font-bold text-gray-900 line-clamp-2">{{ p.title }}</div>
                    <div class="mt-2 text-[11px] text-gray-400 flex items-center justify-between gap-2">
                      <span class="truncate">{{ p.userNickname || $t('favoritesPage.alumni') }}</span>
                      <span class="shrink-0">{{ p.commentCount || 0 }} {{ $t('favoritesPage.comment') }} · {{ p.likeCount || 0 }} {{ $t('favoritesPage.like') }}</span>
                    </div>
                  </div>
                  <button class="text-red-500 flex-shrink-0" @click.stop="togglePostFavorite(p.id)">
                    <el-icon :size="18"><StarFilled /></el-icon>
                  </button>
                </div>
              </div>
              <el-empty v-if="!postLoading && postFavorites.length === 0" :description="$t('favoritesPage.emptyPost')" />
            </div>
          </template>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="favorites-page p-6 max-w-7xl mx-auto pb-24">
        <div class="flex items-center justify-between mb-6">
          <h1 class="text-2xl font-bold">{{ $t('favoritesPage.title') }}</h1>
          <div class="flex items-center gap-2">
            <el-radio-group v-model="tab" size="small">
              <el-radio-button label="product">{{ $t('favoritesPage.tabs.product') }}</el-radio-button>
              <el-radio-button label="post">{{ $t('favoritesPage.tabs.post') }}</el-radio-button>
            </el-radio-group>
            <el-button v-if="tab === 'product'" type="primary" plain @click="fetchFavorites">{{ $t('favoritesPage.refresh') }}</el-button>
          </div>
        </div>

        <div v-if="tab === 'product'" v-loading="loading" class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-6">
          <el-card v-for="p in products" :key="p.id" shadow="hover" class="relative group cursor-pointer" :body-style="{ padding: '0' }" @click="router.push(`/user/product/${p.id}`)">
            <div class="aspect-square bg-gray-50 relative">
              <img :src="getProductImage(p.images)" class="w-full h-full object-cover" />
              <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <el-button type="danger" circle size="small" @click.stop="removeFavorite(p.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div class="p-4">
              <h3 class="text-sm font-medium line-clamp-2 h-10">{{ p.title }}</h3>
              <div class="mt-2 flex justify-between items-center">
                <span class="text-lg font-bold text-primary">¥{{ p.price }}</span>
                <span class="text-xs text-gray-400">{{ p.viewCount }} {{ $t('favoritesPage.views') }}</span>
              </div>
            </div>
          </el-card>
        </div>
        <el-empty v-if="tab === 'product' && products.length === 0 && !loading" :description="$t('favoritesPage.emptyProduct')" />

        <el-card v-else shadow="never">
          <div v-loading="postLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div v-for="p in postFavorites" :key="p.id" class="rounded-xl border border-gray-100 p-4 cursor-pointer" @click="router.push(`/user/forum/detail/${p.id}`)">
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <div class="font-bold line-clamp-2">{{ p.title }}</div>
                  <div class="mt-2 text-xs text-gray-500">{{ p.userNickname || $t('favoritesPage.alumni') }} · {{ p.commentCount || 0 }} {{ $t('favoritesPage.comment') }} · {{ p.likeCount || 0 }} {{ $t('favoritesPage.like') }}</div>
                </div>
                <el-button type="danger" plain size="small" @click.stop="togglePostFavorite(p.id)">{{ $t('myPublishPage.cancel') }}</el-button>
              </div>
            </div>
          </div>
          <el-empty v-if="!postLoading && postFavorites.length === 0" :description="$t('favoritesPage.emptyPost')" />
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { favoriteApi } from '@/api/favorite'
import { forumApi } from '@/api/forum'
import { formatProductImageUrl } from '@/utils/url'
import { ArrowLeft, StarFilled, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const { t } = useI18n()
const products = ref([])
const loading = ref(false)
const tab = ref('product')
const postFavorites = ref([])
const postLoading = ref(false)

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(images)
  }
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await favoriteApi.getMyFavorites({ pageNum: 1, pageSize: 100 })
    if (res.code === '200') products.value = res.data?.records || res.data || []
  } catch (e) {
    ElMessage.error(t('favoritesPage.fetchFail'))
  } finally {
    loading.value = false
  }
}

const fetchPostFavorites = async () => {
  postLoading.value = true
  try {
    const res = await forumApi.getMyFavorites({ current: 1, size: 100 })
    if (res.code === '200') postFavorites.value = res.data?.records || res.data || []
  } finally {
    postLoading.value = false
  }
}

const removeFavorite = async (id) => {
  try {
    const res = await favoriteApi.deleteFavorite(id)
    if (res.code === '200') ElMessage.success(t('favoritesPage.cancelFavorite'))
    fetchFavorites()
  } catch (e) {}
}

const togglePostFavorite = async (id) => {
  try {
    const res = await forumApi.toggleFavorite(id)
    if (res.code === '200') {
      ElMessage.success(res.data?.favorited ? t('favoritesPage.favorited') : t('favoritesPage.cancelFavorite'))
      fetchPostFavorites()
    } else {
      ElMessage.error(res.message || t('favoritesPage.operateFail'))
    }
  } catch {}
}

watch(tab, (v) => {
  if (v === 'post' && postFavorites.value.length === 0) fetchPostFavorites()
})

onMounted(() => {
  fetchFavorites()
  if (tab.value === 'post') fetchPostFavorites()
})
</script>
