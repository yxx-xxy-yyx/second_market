<template>
  <div class="product-detail-container" v-loading="loading">
    <template v-if="product">
      <!-- 移动端视图 -->
      <template v-if="isMobileScreen">
        <div class="product-detail-mobile min-h-screen bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 pb-32">
          <!-- 顶部导航 -->
          <div class="sticky top-0 z-50 bg-white/85 backdrop-blur-xl px-4 py-3 flex items-center justify-between border-b border-gray-100 shadow-sm">
            <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center cursor-pointer hover:from-gray-200 hover:to-gray-300 transition-all active:scale-95 shadow-sm" @click="router.back()">
              <el-icon :size="18" class="text-gray-700"><ArrowLeft /></el-icon>
            </div>
            <span class="text-sm font-bold truncate px-3 text-gray-800">{{ product.title }}</span>
            <div class="flex items-center gap-2">
              <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-orange-100 to-orange-200 flex items-center justify-center cursor-pointer hover:from-orange-200 hover:to-orange-300 transition-all active:scale-95 shadow-sm" @click="handleReport">
                <el-icon :size="18" class="text-orange-600"><Warning /></el-icon>
              </div>
              <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-blue-100 to-blue-200 flex items-center justify-center cursor-pointer hover:from-blue-200 hover:to-blue-300 transition-all active:scale-95 shadow-sm" @click="handleShare">
                <el-icon :size="18" class="text-blue-600"><Share /></el-icon>
              </div>
            </div>
          </div>

          <!-- 商品图片轮播 -->
          <div class="w-full aspect-square bg-gradient-to-br from-gray-100 via-white to-gray-50 relative overflow-hidden">
            <el-carousel height="100%" :autoplay="false" arrow="always" indicator-position="outside" @change="i => currentImageIndex = i">
              <el-carousel-item v-for="(img, index) in imageList" :key="index">
                <div class="w-full h-full flex items-center justify-center">
                  <img :src="img" class="w-full h-full object-cover transition-all duration-500 hover:scale-105" />
                </div>
              </el-carousel-item>
            </el-carousel>
            <div class="absolute bottom-4 right-4 bg-gradient-to-r from-gray-800/90 to-gray-900/90 text-white text-xs px-3 py-1.5 rounded-2xl shadow-lg backdrop-blur-sm border border-white/20">
              {{ currentImageIndex + 1 }}/{{ imageList.length }}
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="bg-white/80 backdrop-blur-xl p-6 space-y-4 mx-3 -mt-6 rounded-3xl border border-gray-100 shadow-xl relative z-10">
            <div class="flex items-end gap-2">
              <span class="text-xl font-extrabold text-primary">¥</span>
              <span class="text-4xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">{{ product.price }}</span>
              <span v-if="product.originalPrice" class="text-sm text-gray-400 line-through ml-3 font-medium">¥{{ product.originalPrice }}</span>
            </div>
            <h1 class="text-xl font-extrabold text-gray-900 leading-tight">{{ product.title }}</h1>
            <div class="flex flex-wrap gap-2">
              <div v-if="product.aiAnalyzed" class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-2xl bg-gradient-to-r from-green-500 to-emerald-600 text-white text-xs font-bold shadow-md">
                <el-icon><MagicStick /></el-icon> AI评估
              </div>
              <div class="inline-flex items-center px-3 py-1.5 rounded-2xl bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-xs font-bold shadow-md">
                {{ getCategoryName(product.category) }}
              </div>
            </div>
          </div>

          <!-- 卖家信息 -->
          <div class="mt-4 bg-white/80 backdrop-blur-xl p-5 mx-3 rounded-3xl border border-gray-100 shadow-lg cursor-pointer hover:shadow-xl transition-all" @click="showSellerDrawer = true">
            <div class="flex items-center gap-4">
              <div class="relative">
                <el-avatar :size="50" :src="formatAvatarUrl(product.userAvatar)" class="ring-4 ring-gray-100 shadow-lg" />
                <div class="absolute -bottom-1 -right-1 h-5 w-5 rounded-full bg-gradient-to-br from-green-400 to-emerald-500 flex items-center justify-center border-2 border-white shadow-sm">
                  <el-icon :size="10" class="text-white"><CircleCheck /></el-icon>
                </div>
              </div>
              <div class="flex-1">
                <div class="text-base font-bold text-gray-900">{{ product.userNickname || '匿名用户' }}</div>
                <div class="flex items-center gap-3 mt-1">
                  <div class="flex items-center gap-1">
                    <el-rate v-model="sellerRating.averageRating" disabled size="small" />
                    <span class="text-xs text-gray-500 font-medium">信用良好</span>
                  </div>
                </div>
              </div>
              <div class="h-10 w-10 rounded-full bg-gradient-to-br from-primary/10 to-indigo-100 flex items-center justify-center">
                <el-icon :size="18" class="text-primary"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>

          <!-- 商品描述 -->
          <div class="mt-4 bg-white/80 backdrop-blur-xl p-6 mx-3 rounded-3xl border border-gray-100 shadow-lg">
            <div class="flex items-center gap-2 mb-4">
              <div class="h-8 w-8 rounded-xl bg-gradient-to-br from-indigo-100 to-purple-100 flex items-center justify-center">
                <el-icon :size="16" class="text-indigo-600"><Document /></el-icon>
              </div>
              <h2 class="text-base font-bold text-gray-900">商品详情</h2>
            </div>
            <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-wrap bg-gray-50/50 p-4 rounded-2xl border border-gray-100">{{ product.description }}</p>
          </div>

          <div v-if="similarProducts.length" class="mt-4 bg-white/80 backdrop-blur-xl p-5 mx-3 rounded-3xl border border-gray-100 shadow-lg">
            <div class="flex items-center justify-between mb-5">
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 rounded-xl bg-gradient-to-br from-rose-100 to-pink-100 flex items-center justify-center">
                  <el-icon :size="16" class="text-rose-600"><Star /></el-icon>
                </div>
                <h2 class="text-base font-bold text-gray-900">同款推荐</h2>
              </div>
              <div class="flex items-center gap-1 text-primary font-medium text-sm cursor-pointer hover:text-purple-600 transition-colors" @click="router.push({ path: '/user/products', query: { category: product.category } })">
                <span>查看更多</span>
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
            <div class="grid grid-cols-3 gap-3">
              <div v-for="sp in similarProducts" :key="sp.id" class="rounded-2xl overflow-hidden border border-gray-100 bg-gradient-to-br from-white to-gray-50 shadow-md hover:shadow-xl hover:-translate-y-1 transition-all cursor-pointer" @click="router.push(`/user/product/${sp.id}`)">
                <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden">
                  <img :src="getProductImage(sp.images)" class="w-full h-full object-cover hover:scale-110 transition-transform duration-300" />
                </div>
                <div class="p-3">
                  <div class="text-xs font-bold text-gray-800 line-clamp-2 leading-snug">{{ sp.title }}</div>
                  <div class="mt-2 text-base font-black text-primary">¥{{ sp.price }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部操作栏 -->
          <div class="fixed bottom-0 left-0 right-0 bg-white/95 backdrop-blur-xl border-t border-gray-100 px-4 py-4 flex items-center gap-4 z-50 shadow-2xl">
            <div class="flex flex-col items-center gap-1 min-w-[50px] cursor-pointer hover:scale-105 transition-transform" @click="toggleFavorite">
              <div :class="['h-10 w-10 rounded-2xl flex items-center justify-center shadow-md transition-all', isFavorited ? 'bg-gradient-to-br from-red-500 to-rose-600' : 'bg-gradient-to-br from-gray-100 to-gray-200']">
                <el-icon :size="20" :class="isFavorited ? 'text-white' : 'text-gray-600'">
                  <component :is="isFavorited ? StarFilled : Star" />
                </el-icon>
              </div>
              <span class="text-xs text-gray-600 font-medium">收藏</span>
            </div>
            <div class="flex flex-col items-center gap-1 min-w-[50px] cursor-pointer hover:scale-105 transition-transform" @click="handleContact">
              <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-green-100 to-emerald-200 flex items-center justify-center shadow-md">
                <el-icon :size="20" class="text-green-600"><ChatDotRound /></el-icon>
              </div>
              <span class="text-xs text-gray-600 font-medium">聊一聊</span>
            </div>
            <div class="flex-1">
              <el-button type="primary" class="w-full !rounded-full !h-12 !font-bold !text-base shadow-lg" @click="handleBuy">立即购买</el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 桌面端视图 -->
      <template v-else>
        <div class="product-detail-desktop bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pb-24">
          <div class="p-8 max-w-7xl mx-auto space-y-8">
            <el-breadcrumb separator="/" class="text-sm">
              <el-breadcrumb-item :to="{ path: '/user/dashboard' }" class="hover:text-primary transition-colors">首页</el-breadcrumb-item>
              <el-breadcrumb-item :to="{ path: '/user/products' }" class="hover:text-primary transition-colors">商品列表</el-breadcrumb-item>
              <el-breadcrumb-item class="text-gray-800 font-medium">商品详情</el-breadcrumb-item>
            </el-breadcrumb>

            <div class="grid grid-cols-1 lg:grid-cols-2 gap-10">
              <!-- 左侧图片 -->
              <div class="space-y-6">
                <div class="bg-white/85 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-4 aspect-square overflow-hidden flex items-center justify-center">
                  <el-image :src="currentImage" :preview-src-list="imageList" fit="contain" class="w-full h-full transition-all duration-300 hover:scale-102" />
                </div>
                <div class="bg-white/85 backdrop-blur-xl rounded-2xl shadow-lg border border-gray-100 p-4">
                  <div class="flex gap-3 overflow-x-auto py-2">
                    <div v-for="(img, index) in imageList" :key="index" 
                         :class="['w-24 h-24 border-3 rounded-2xl cursor-pointer overflow-hidden transition-all duration-300 hover:scale-105', 
                                  currentImageIndex === index ? 'border-primary shadow-lg scale-105' : 'border-gray-100 hover:border-primary/50']" 
                         @click="currentImageIndex = index">
                      <img :src="img" class="w-full h-full object-cover" />
                    </div>
                  </div>
                </div>
              </div>

              <!-- 右侧详情 -->
              <div class="space-y-8">
                <h1 class="text-3xl font-black text-gray-900 leading-tight">{{ product.title }}</h1>
                <div class="bg-gradient-to-br from-white/90 to-gray-50/90 backdrop-blur-xl p-8 rounded-3xl shadow-xl border border-gray-100 space-y-6">
                  <div class="flex items-end gap-4">
                    <span class="text-5xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ product.price }}</span>
                    <span v-if="product.originalPrice" class="text-xl text-gray-400 line-through font-medium">¥{{ product.originalPrice }}</span>
                  </div>
                  <div class="flex flex-wrap gap-3 pt-4 border-t border-gray-100">
                    <div v-if="product.aiAnalyzed" class="inline-flex items-center gap-2 px-4 py-2 rounded-2xl bg-gradient-to-r from-green-500 to-emerald-600 text-white text-sm font-bold shadow-lg">
                      <el-icon><MagicStick /></el-icon> AI评估
                    </div>
                    <div class="inline-flex items-center px-4 py-2 rounded-2xl bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-sm font-bold shadow-lg">
                      {{ getCategoryName(product.category) }}
                    </div>
                  </div>
                  <div class="grid grid-cols-2 gap-6 text-sm pt-4 border-t border-gray-100">
                    <div class="flex items-center gap-2 text-gray-600">
                      <span class="font-medium">成色：</span>
                      <el-rate v-model="product.conditionScore" disabled size="default" />
                    </div>
                    <div class="flex items-center gap-2 text-gray-600">
                      <span class="font-medium">浏览：</span>
                      <span class="font-bold text-gray-800">{{ product.viewCount }}</span>
                    </div>
                  </div>
                </div>

                <div class="space-y-4 bg-white/85 backdrop-blur-xl p-6 rounded-3xl shadow-lg border border-gray-100">
                  <div class="flex items-center gap-3">
                    <div class="h-10 w-10 rounded-xl bg-gradient-to-br from-indigo-100 to-purple-100 flex items-center justify-center">
                      <el-icon :size="18" class="text-indigo-600"><Document /></el-icon>
                    </div>
                    <h3 class="text-xl font-bold text-gray-900">商品描述</h3>
                  </div>
                  <p class="text-base text-gray-600 leading-relaxed whitespace-pre-wrap bg-gray-50/70 p-6 rounded-2xl border border-gray-100">{{ product.description }}</p>
                </div>

                <div v-if="similarProducts.length" class="bg-white/85 backdrop-blur-xl rounded-3xl shadow-lg border border-gray-100 overflow-hidden">
                  <div class="p-6 border-b border-gray-100 flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <div class="h-10 w-10 rounded-xl bg-gradient-to-br from-rose-100 to-pink-100 flex items-center justify-center">
                        <el-icon :size="18" class="text-rose-600"><Star /></el-icon>
                      </div>
                      <span class="font-bold text-gray-900 text-lg">同款推荐</span>
                    </div>
                    <el-button type="primary" link class="text-base font-medium" @click="router.push({ path: '/user/products', query: { category: product.category } })">查看更多 →</el-button>
                  </div>
                  <div class="p-6">
                    <div class="grid grid-cols-3 gap-5">
                      <div v-for="sp in similarProducts" :key="sp.id" class="group rounded-2xl overflow-hidden border border-gray-100 bg-gradient-to-br from-white to-gray-50 shadow-md hover:shadow-xl hover:-translate-y-2 transition-all duration-300 cursor-pointer" @click="router.push(`/user/product/${sp.id}`)">
                        <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden">
                          <img :src="getProductImage(sp.images)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                        </div>
                        <div class="p-4">
                          <div class="text-sm font-bold text-gray-800 line-clamp-2 leading-snug">{{ sp.title }}</div>
                          <div class="mt-2 text-xl font-black text-primary">¥{{ sp.price }}</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="flex flex-wrap gap-4 pt-8 border-t border-gray-200">
                  <el-button type="primary" size="large" class="flex-1 !h-14 !text-lg !font-bold !rounded-full !shadow-lg hover:!shadow-xl transition-all" @click="handleBuy">立即购买</el-button>
                  <el-button :type="isFavorited ? 'danger' : 'default'" size="large" class="!h-14 !rounded-full !shadow-lg hover:!shadow-xl transition-all" @click="toggleFavorite">
                    <el-icon class="mr-2"><component :is="isFavorited ? StarFilled : Star" /></el-icon>
                    {{ isFavorited ? '已收藏' : '收藏' }}
                  </el-button>
                  <el-button size="large" class="!h-14 !rounded-full !shadow-lg hover:!shadow-xl transition-all" @click="handleReport">
                    <el-icon class="mr-2"><Warning /></el-icon>
                    举报
                  </el-button>
                </div>

                <div class="bg-white/85 backdrop-blur-xl rounded-3xl shadow-lg border border-gray-100 p-6">
                  <div class="flex items-center gap-6">
                    <div class="relative">
                      <el-avatar :size="80" :src="formatAvatarUrl(product.userAvatar)" class="ring-6 ring-gray-100 shadow-xl" />
                      <div class="absolute -bottom-2 -right-2 h-8 w-8 rounded-full bg-gradient-to-br from-green-400 to-emerald-500 flex items-center justify-center border-4 border-white shadow-lg">
                        <el-icon :size="14" class="text-white"><CircleCheck /></el-icon>
                      </div>
                    </div>
                    <div class="flex-1">
                      <div class="text-xl font-black text-gray-900">{{ product.userNickname || '匿名用户' }}</div>
                      <div class="text-base text-gray-500 mt-2 flex items-center gap-2">
                        <span class="font-medium">卖家评分：</span>
                        <el-rate v-model="sellerRating.averageRating" disabled size="default" />
                      </div>
                    </div>
                    <el-button type="primary" plain size="large" class="!h-12 !px-8 !rounded-full !font-bold !shadow-lg hover:!shadow-xl transition-all" @click="handleContact">联系卖家</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </template>
    <el-empty v-else-if="!loading" description="商品不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { reportApi } from '@/api/report'
import { useUserStore } from '@/stores/user'
import { useDeviceType } from '@/utils/device'
import { formatAvatarUrl, formatProductImageUrl } from '@/utils/url'
import { 
  ArrowLeft, Share, Star, StarFilled, ChatDotRound, 
  MagicStick, Picture, Warning, Document, CircleCheck
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isMobileScreen } = useDeviceType()
const { locale } = useI18n()

const loading = ref(false)
const product = ref(null)
const currentImageIndex = ref(0)
const isFavorited = ref(false)
const sellerRating = ref({ averageRating: 4.5, totalCount: 10 })
const similarProducts = ref([])

const imageList = computed(() => {
  if (!product.value?.images) return []
  try {
    const arr = typeof product.value.images === 'string' ? JSON.parse(product.value.images) : product.value.images
    return Array.isArray(arr) ? arr.map(img => formatProductImageUrl(img)) : [formatProductImageUrl(arr)]
  } catch {
    return [formatProductImageUrl(product.value.images)]
  }
})

const currentImage = computed(() => imageList.value[currentImageIndex.value] || '')

const getCategoryName = (cat) => {
  return cat || '其他'
}

const fetchProduct = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await productApi.getProductById(id)
    if (res.code === '200') {
      product.value = res.data
      await Promise.all([checkFavorite(), fetchSimilar()])
    }
  } catch (e) {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const checkFavorite = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await favoriteApi.checkFavorite(product.value.id)
    if (res.code === '200') isFavorited.value = !!res.data
  } catch (e) {}
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    if (isFavorited.value) {
      const res = await favoriteApi.deleteFavorite(product.value.id)
      if (res.code === '200') {
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      }
      return
    }
    const res = await favoriteApi.addFavorite(product.value.id)
    if (res.code === '200') {
      isFavorited.value = true
      ElMessage.success('已收藏')
    }
  } catch (e) {}
}

const handleBuy = () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  router.push({
    path: '/neo/checkout',
    query: { productId: product.value.id }
  })
}

const handleContact = () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  router.push(`/user/chat/${product.value.userId}`)
}

const handleShare = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制浏览器地址')
  }
}

const handleReport = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    const { value } = await ElMessageBox.prompt('请输入举报原因', '举报', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：虚假信息、违规内容、诈骗等'
    })
    const reason = (value || '').trim()
    if (!reason) return
    const res = await reportApi.add({ targetType: 1, targetId: product.value.id, reason })
    if (res.code === '200') ElMessage.success('举报已提交')
    else ElMessage.error(res.message || '举报失败')
  } catch {}
}

const fetchSimilar = async () => {
  if (!product.value?.category) return
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      category: product.value.category,
      status: 2,
      sortBy: 'viewCount'
    })
    if (res.code === '200') {
      const list = res.data?.records || []
      similarProducts.value = list.filter((p) => String(p.id) !== String(product.value.id)).slice(0, 6)
    }
  } catch {}
}

onMounted(() => fetchProduct())
</script>

<style scoped>
.product-detail-container :deep(.el-carousel__container) {
  height: 100%;
}
</style>
