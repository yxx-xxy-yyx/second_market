<template>
  <div class="product-detail-container" v-loading="loading">
    <template v-if="product">
      <!-- 移动端视图 -->
      <template v-if="isMobileScreen">
        <div class="product-detail-mobile min-h-screen bg-gray-50 pb-24">
          <!-- 顶部导航 -->
          <div class="sticky top-0 z-50 bg-white/90 backdrop-blur px-4 py-3 flex items-center justify-between border-b border-gray-100">
            <el-icon :size="20" @click="router.back()"><ArrowLeft /></el-icon>
            <span class="text-sm font-bold truncate px-4">{{ product.title }}</span>
            <div class="flex items-center gap-3">
              <el-icon :size="20" @click="handleReport"><Warning /></el-icon>
              <el-icon :size="20" @click="handleShare"><Share /></el-icon>
            </div>
          </div>

          <!-- 商品图片轮播 -->
          <div class="w-full aspect-square bg-white relative">
            <el-carousel height="100%" :autoplay="false" arrow="never" @change="i => currentImageIndex = i">
              <el-carousel-item v-for="(img, index) in imageList" :key="index">
                <img :src="img" class="w-full h-full object-contain" />
              </el-carousel-item>
            </el-carousel>
            <div class="absolute bottom-4 right-4 bg-black/40 text-white text-[10px] px-2 py-0.5 rounded-full">
              {{ currentImageIndex + 1 }}/{{ imageList.length }}
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="bg-white p-4 space-y-3">
            <div class="flex items-baseline gap-1 text-primary">
              <span class="text-lg font-bold">¥</span>
              <span class="text-2xl font-black">{{ product.price }}</span>
              <span v-if="product.originalPrice" class="text-xs text-gray-400 line-through ml-2">¥{{ product.originalPrice }}</span>
            </div>
            <h1 class="text-lg font-bold text-gray-900 leading-snug">{{ product.title }}</h1>
            <div class="flex flex-wrap gap-2">
              <el-tag v-if="product.aiAnalyzed" size="small" type="success" effect="dark"><el-icon><MagicStick /></el-icon> AI评估</el-tag>
              <el-tag size="small" type="primary">{{ getCategoryName(product.category) }}</el-tag>
            </div>
          </div>

          <!-- 卖家信息 -->
          <div class="mt-2 bg-white p-4 flex items-center gap-3" @click="showSellerDrawer = true">
            <el-avatar :size="40" :src="formatAvatarUrl(product.userAvatar)" />
            <div class="flex-1">
              <div class="text-sm font-bold text-gray-900">{{ product.userNickname || '匿名用户' }}</div>
              <div class="flex items-center gap-2 mt-0.5">
                <el-rate v-model="sellerRating.averageRating" disabled size="small" />
                <span class="text-[10px] text-gray-400">信用良好</span>
              </div>
            </div>
            <el-icon class="text-gray-300"><ArrowRight /></el-icon>
          </div>

          <!-- 商品描述 -->
          <div class="mt-2 bg-white p-4">
            <h2 class="text-sm font-bold text-gray-900 mb-2">商品详情</h2>
            <p class="text-sm text-gray-600 leading-relaxed whitespace-pre-wrap">{{ product.description }}</p>
          </div>

          <div v-if="similarProducts.length" class="mt-2 bg-white p-4">
            <div class="flex items-center justify-between mb-3">
              <h2 class="text-sm font-bold text-gray-900">同款推荐</h2>
              <button class="text-xs text-primary" @click="router.push({ path: '/user/products', query: { category: product.category } })">查看更多</button>
            </div>
            <div class="grid grid-cols-3 gap-2">
              <div v-for="sp in similarProducts" :key="sp.id" class="rounded-xl overflow-hidden border border-gray-100 bg-white" @click="router.push(`/user/product/${sp.id}`)">
                <div class="aspect-square bg-gray-50">
                  <img :src="getProductImage(sp.images)" class="w-full h-full object-cover" />
                </div>
                <div class="p-2">
                  <div class="text-[11px] font-semibold text-gray-900 line-clamp-2 leading-snug">{{ sp.title }}</div>
                  <div class="mt-1 text-[11px] font-black text-primary">¥{{ sp.price }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部操作栏 -->
          <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 p-3 flex items-center gap-4 z-50">
            <div class="flex flex-col items-center gap-1 min-w-[40px]" @click="toggleFavorite">
              <el-icon :size="20" :class="isFavorited ? 'text-red-500' : 'text-gray-500'">
                <component :is="isFavorited ? StarFilled : Star" />
              </el-icon>
              <span class="text-[10px] text-gray-500">收藏</span>
            </div>
            <div class="flex flex-col items-center gap-1 min-w-[40px]" @click="handleContact">
              <el-icon :size="20" class="text-gray-500"><ChatDotRound /></el-icon>
              <span class="text-[10px] text-gray-500">聊一聊</span>
            </div>
            <div class="flex flex-col items-center gap-1 min-w-[40px]" @click="router.push('/user/cart')">
              <el-icon :size="20" class="text-gray-500"><ShoppingCart /></el-icon>
              <span class="text-[10px] text-gray-500">购物车</span>
            </div>
            <div class="flex-1 flex gap-2">
              <el-button type="warning" class="flex-1 !rounded-full !h-10" @click="handleAddToCart">加入购物车</el-button>
              <el-button type="primary" class="flex-1 !rounded-full !h-10" @click="handleBuy">立即购买</el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 桌面端视图 -->
      <template v-else>
        <div class="product-detail-desktop p-6 max-w-7xl mx-auto space-y-6 pb-24">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/user/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: '/user/products' }">商品列表</el-breadcrumb-item>
            <el-breadcrumb-item>商品详情</el-breadcrumb-item>
          </el-breadcrumb>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- 左侧图片 -->
            <div class="space-y-4">
              <el-card :body-style="{ padding: '0' }" class="aspect-square overflow-hidden flex items-center justify-center bg-gray-50">
                <el-image :src="currentImage" :preview-src-list="imageList" fit="contain" class="w-full h-full" />
              </el-card>
              <div class="flex gap-2 overflow-x-auto py-2">
                <div v-for="(img, index) in imageList" :key="index" :class="['w-20 h-20 border-2 rounded cursor-pointer overflow-hidden', currentImageIndex === index ? 'border-primary' : 'border-transparent']" @click="currentImageIndex = index">
                  <img :src="img" class="w-full h-full object-cover" />
                </div>
              </div>
            </div>

            <!-- 右侧详情 -->
            <div class="space-y-6">
              <h1 class="text-2xl font-bold text-gray-900">{{ product.title }}</h1>
              <div class="bg-gray-50 p-6 rounded-xl space-y-4">
                <div class="flex items-baseline gap-2">
                  <span class="text-3xl font-bold text-primary">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="text-sm text-gray-400 line-through">¥{{ product.originalPrice }}</span>
                </div>
                <div class="grid grid-cols-2 gap-4 text-sm text-gray-500 border-t border-gray-100 pt-4">
                  <div>成色：<el-rate v-model="product.conditionScore" disabled size="small" /></div>
                  <div>浏览：{{ product.viewCount }}</div>
                </div>
              </div>

              <div class="space-y-4">
                <h3 class="font-bold">商品描述</h3>
                <p class="text-gray-600 leading-relaxed whitespace-pre-wrap">{{ product.description }}</p>
              </div>

              <el-card v-if="similarProducts.length" shadow="never">
                <template #header>
                  <div class="flex items-center justify-between">
                    <div class="font-bold">同款推荐</div>
                    <el-button link type="primary" @click="router.push({ path: '/user/products', query: { category: product.category } })">查看更多</el-button>
                  </div>
                </template>
                <div class="grid grid-cols-3 gap-3">
                  <div v-for="sp in similarProducts" :key="sp.id" class="rounded-xl overflow-hidden border border-gray-100 cursor-pointer" @click="router.push(`/user/product/${sp.id}`)">
                    <div class="aspect-square bg-gray-50">
                      <img :src="getProductImage(sp.images)" class="w-full h-full object-cover" />
                    </div>
                    <div class="p-2">
                      <div class="text-sm font-medium line-clamp-2">{{ sp.title }}</div>
                      <div class="mt-1 font-bold text-primary">¥{{ sp.price }}</div>
                    </div>
                  </div>
                </div>
              </el-card>

              <div class="flex gap-4 pt-6 border-t border-gray-100">
                <el-button type="primary" size="large" class="flex-1" @click="handleBuy">立即购买</el-button>
                <el-button type="warning" size="large" class="flex-1" @click="handleAddToCart">加入购物车</el-button>
                <el-button size="large" @click="toggleFavorite">
                  <el-icon class="mr-2"><component :is="isFavorited ? StarFilled : Star" /></el-icon>
                  {{ isFavorited ? '已收藏' : '收藏' }}
                </el-button>
                <el-button size="large" @click="handleReport">举报</el-button>
              </div>

              <el-card class="mt-8" shadow="never">
                <div class="flex items-center gap-4">
                  <el-avatar :size="60" :src="formatAvatarUrl(product.userAvatar)" />
                  <div class="flex-1">
                    <div class="font-bold">{{ product.userNickname || '匿名用户' }}</div>
                    <div class="text-sm text-gray-500 mt-1">卖家评分：<el-rate v-model="sellerRating.averageRating" disabled size="small" /></div>
                  </div>
                  <el-button type="primary" plain @click="handleContact">联系卖家</el-button>
                </div>
              </el-card>
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
import { orderApi } from '@/api/order'
import { reportApi } from '@/api/report'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { useDeviceType } from '@/utils/device'
import { formatAvatarUrl, formatProductImageUrl } from '@/utils/url'
import { 
  ArrowLeft, Share, Star, StarFilled, ChatDotRound, 
  MagicStick, Picture, Warning, ShoppingCart
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
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

const handleBuy = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    await ElMessageBox.confirm('确认使用模拟支付立即购买吗？', '确认下单', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }

  try {
    const createRes = await orderApi.createOrder({ productId: product.value.id })
    if (createRes.code !== '200' || !createRes.data?.id) {
      ElMessage.error(createRes.message || '创建订单失败')
      return
    }
    const payRes = await orderApi.payOrder(createRes.data.id)
    if (payRes.code !== '200') {
      ElMessage.error(payRes.message || '支付失败')
      return
    }
    ElMessage.success('支付成功')
    router.push('/user/orders')
  } catch {
    ElMessage.error('下单失败')
  }
}

const handleAddToCart = () => {
  cartStore.addItem({
    productId: product.value.id,
    title: product.value.title,
    price: product.value.price,
    image: imageList.value?.[0] || '',
    sellerId: product.value.userId,
    schoolId: product.value.schoolId,
    conditionScore: product.value.conditionScore,
    conditionDesc: product.value.conditionDesc
  })
  ElMessage.success('已加入购物车')
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
