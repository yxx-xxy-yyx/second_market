<template>
  <div class="favorites-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 pb-24">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur-xl border-b border-gray-100 shadow-sm px-4 py-4 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center hover:from-gray-200 hover:to-gray-300 transition-all cursor-pointer active:scale-95" @click="router.back()">
              <el-icon :size="20" class="text-gray-700"><ArrowLeft /></el-icon>
            </div>
            <h1 class="text-xl font-black text-gray-900">{{ $t('favoritesPage.title') }}</h1>
          </div>
          <LangSwitcher />
        </div>

        <div class="px-4 pt-4">
          <div class="bg-white/90 backdrop-blur-xl rounded-3xl border border-gray-100 shadow-lg p-2 flex gap-2">
            <button class="flex-1 h-12 rounded-2xl text-base font-black transition-all duration-300" :class="tab === 'product' ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'text-gray-500 hover:bg-gray-100'" @click="tab = 'product'">{{ $t('favoritesPage.tabs.product') }}</button>
            <button class="flex-1 h-12 rounded-2xl text-base font-black transition-all duration-300" :class="tab === 'post' ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'text-gray-500 hover:bg-gray-100'" @click="tab = 'post'">{{ $t('favoritesPage.tabs.post') }}</button>
          </div>
        </div>

        <div class="px-4 py-5 space-y-4">
          <template v-if="tab === 'product'">
            <div v-if="loading && products.length === 0" class="text-center py-16 text-gray-400 text-base font-medium">{{ $t('favoritesPage.loading') }}</div>
            <div v-else class="space-y-4">
              <div v-for="p in products" :key="p.id" class="bg-white/90 backdrop-blur-xl rounded-3xl border border-gray-100 shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300 overflow-hidden relative p-4 flex gap-5 group">
                <div class="h-28 w-28 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden flex-none cursor-pointer shadow-md hover:shadow-lg transition-all" @click="router.push(`/user/product/${p.id}`)">
                  <img :src="getProductImage(p.images)" class="h-full w-full object-cover group-hover:scale-110 transition-transform duration-300" />
                </div>
                <div class="flex-1 min-w-0 pr-10 pt-1">
                  <div class="text-base font-black text-gray-900 line-clamp-2 leading-snug">{{ p.title }}</div>
                  <div class="mt-3 text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ p.price }}</div>
                  <div class="mt-2 text-xs text-gray-500 font-medium">{{ $t('favoritesPage.views') }} {{ p.viewCount || 0 }} · {{ $t('favoritesPage.condition') }} {{ p.conditionScore }}</div>
                </div>
                <button class="absolute right-4 top-1/2 -translate-y-1/2 h-12 w-12 rounded-2xl bg-gradient-to-r from-red-500 to-rose-600 text-white shadow-md hover:shadow-lg transition-all active:scale-95 flex items-center justify-center" @click.stop="removeFavorite(p.id)">
                  <el-icon :size="22"><StarFilled /></el-icon>
                </button>
              </div>
              <el-empty v-if="products.length === 0 && !loading" :description="$t('favoritesPage.empty')" :image-size="130" />
            </div>
          </template>

          <template v-else>
            <div v-loading="postLoading" class="space-y-4">
              <div
                v-for="p in postFavorites"
                :key="p.id"
                class="bg-white/90 backdrop-blur-xl rounded-3xl border border-gray-100 shadow-lg hover:shadow-xl hover:-translate-y-1 transition-all duration-300 overflow-hidden relative p-5 group"
                @click="router.push(`/user/forum/detail/${p.id}`)"
              >
                <div class="flex items-start justify-between gap-4">
                  <div class="min-w-0 flex-1">
                    <div class="text-base font-black text-gray-900 line-clamp-2 leading-snug">{{ p.title }}</div>
                    <div class="mt-3 text-sm text-gray-500 font-medium flex items-center justify-between gap-3">
                      <span class="truncate">{{ p.userNickname || $t('favoritesPage.alumni') }}</span>
                      <span class="shrink-0">{{ p.commentCount || 0 }} {{ $t('favoritesPage.comment') }} · {{ p.likeCount || 0 }} {{ $t('favoritesPage.like') }}</span>
                    </div>
                  </div>
                  <button class="text-red-500 flex-shrink-0 h-10 w-10 rounded-2xl bg-red-50 hover:bg-red-100 transition-all flex items-center justify-center" @click.stop="togglePostFavorite(p.id)">
                    <el-icon :size="20"><StarFilled /></el-icon>
                  </button>
                </div>
              </div>
              <el-empty v-if="!postLoading && postFavorites.length === 0" :description="$t('favoritesPage.emptyPost')" :image-size="130" />
            </div>
          </template>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pt-10 pb-24">
        <div class="px-8 max-w-7xl mx-auto">
          <div class="flex items-center justify-between mb-10">
            <div class="flex items-center gap-5">
              <div class="h-14 w-14 bg-gradient-to-br from-primary to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl">
                <el-icon :size="28" class="text-white"><Star /></el-icon>
              </div>
              <h1 class="text-4xl font-black text-gray-900">{{ $t('favoritesPage.title') }}</h1>
            </div>
            <div class="flex items-center gap-4">
              <div class="bg-white/90 backdrop-blur-xl rounded-2xl shadow-lg border border-gray-100 p-2 flex gap-2">
                <button class="px-6 py-3 rounded-xl text-base font-bold transition-all duration-300" :class="tab === 'product' ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'text-gray-600 hover:bg-gray-100'" @click="tab = 'product'">{{ $t('favoritesPage.tabs.product') }}</button>
                <button class="px-6 py-3 rounded-xl text-base font-bold transition-all duration-300" :class="tab === 'post' ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'text-gray-600 hover:bg-gray-100'" @click="tab = 'post'">{{ $t('favoritesPage.tabs.post') }}</button>
              </div>
              <el-button v-if="tab === 'product'" type="primary" size="large" class="!rounded-2xl !px-6 !py-3 !font-bold !shadow-lg hover:!shadow-xl transition-all" @click="fetchFavorites">{{ $t('favoritesPage.refresh') }}</el-button>
            </div>
          </div>

          <div v-if="tab === 'product'" v-loading="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            <div v-for="p in products" :key="p.id" class="bg-white/90 backdrop-blur-xl rounded-3xl overflow-hidden shadow-xl border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 relative group cursor-pointer" @click="router.push(`/user/product/${p.id}`)">
              <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 relative overflow-hidden">
                <img :src="getProductImage(p.images)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                <div class="absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-all duration-300 scale-90 group-hover:scale-100">
                  <div class="h-12 w-12 bg-gradient-to-r from-red-500 to-rose-600 rounded-2xl shadow-xl flex items-center justify-center text-white cursor-pointer" @click.stop="removeFavorite(p.id)">
                    <el-icon :size="22"><Delete /></el-icon>
                  </div>
                </div>
              </div>
              <div class="p-5">
                <h3 class="text-base font-black text-gray-900 line-clamp-2 h-12 leading-snug">{{ p.title }}</h3>
                <div class="mt-3 flex justify-between items-center">
                  <span class="text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ p.price }}</span>
                  <span class="text-sm text-gray-500 font-medium">{{ p.viewCount }} {{ $t('favoritesPage.views') }}</span>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-if="tab === 'product' && products.length === 0 && !loading" :description="$t('favoritesPage.emptyProduct')" :image-size="160" />

          <div v-else class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-100 overflow-hidden p-6">
            <div v-loading="postLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
              <div v-for="p in postFavorites" :key="p.id" class="bg-gradient-to-r from-gray-50 to-white rounded-2xl border border-gray-100 p-5 cursor-pointer hover:shadow-xl hover:-translate-y-1 transition-all duration-300" @click="router.push(`/user/forum/detail/${p.id}`)">
                <div class="flex items-start justify-between gap-4">
                  <div class="min-w-0 flex-1">
                    <div class="text-lg font-black text-gray-900 line-clamp-2 leading-snug">{{ p.title }}</div>
                    <div class="mt-3 text-sm text-gray-500 font-medium">{{ p.userNickname || $t('favoritesPage.alumni') }} · {{ p.commentCount || 0 }} {{ $t('favoritesPage.comment') }} · {{ p.likeCount || 0 }} {{ $t('favoritesPage.like') }}</div>
                  </div>
                  <el-button type="danger" size="large" class="!rounded-2xl !px-4 !py-2 !font-bold" @click.stop="togglePostFavorite(p.id)">{{ $t('myPublishPage.cancel') }}</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="!postLoading && postFavorites.length === 0" :description="$t('favoritesPage.emptyPost')" :image-size="160" />
          </div>
        </div>
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
import { ArrowLeft, StarFilled, Delete, Star } from '@element-plus/icons-vue'
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
