<template>
  <div class="popular-digital-page min-h-screen bg-[#f8f8fa]">
    <!-- Header -->
    <div class="sticky top-0 z-50 bg-[#8b5cf6] text-white px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ $t('dashboard.popularDigital') }}</h1>
      </div>
      <LangSwitcher glass />
    </div>

    <!-- Category Tabs -->
    <div class="bg-white px-4 py-2 flex items-center justify-between border-b border-gray-100 sticky top-[52px] z-40 shadow-sm">
      <div class="flex gap-4 overflow-x-auto no-scrollbar flex-1">
        <div v-for="tab in tabs" :key="tab.id" 
          class="whitespace-nowrap px-4 py-1.5 rounded-full text-sm transition-colors"
          :class="activeTab === tab.id ? 'bg-[#8b5cf6] text-white font-medium' : 'bg-gray-100 text-gray-600'"
          @click="activeTab = tab.id">
          {{ tab.label }}
        </div>
      </div>
      <div class="ml-2 pl-2 border-l border-gray-100">
        <el-button link @click="showFilter = true">
          <el-icon class="text-lg"><Filter /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- Content -->
    <div class="p-4">
      <div v-if="loading" class="space-y-4">
        <div v-for="i in 4" :key="i" class="bg-white p-3 rounded-2xl flex gap-3 animate-pulse">
          <div class="w-24 h-24 bg-gray-100 rounded-xl"></div>
          <div class="flex-1 space-y-2 py-1">
            <div class="h-4 bg-gray-100 rounded w-3/4"></div>
            <div class="h-4 bg-gray-100 rounded w-1/2"></div>
            <div class="h-6 bg-gray-100 rounded w-1/3"></div>
          </div>
        </div>
      </div>

      <div v-else-if="products.length > 0" class="space-y-4">
        <div v-for="product in products" :key="product.id" 
          class="bg-white rounded-3xl p-4 shadow-sm active:scale-[0.99] transition-all flex gap-4 relative group"
          @click="goDetail(product.id)">
          
          <div class="w-32 h-32 rounded-2xl overflow-hidden bg-gray-50 flex-shrink-0 relative">
            <el-image :src="getProductImage(product.images)" fit="cover" class="w-full h-full" />
            <div class="absolute top-2 left-2 px-2 py-0.5 bg-black/40 backdrop-blur-md rounded-full text-[10px] text-white">
              {{ getConditionText(product.conditionScore) }}
            </div>
          </div>

          <div class="flex-1 flex flex-col justify-between py-1">
            <div>
              <h3 class="text-base font-bold text-gray-900 line-clamp-1 mb-1">{{ product.title }}</h3>
              <p class="text-xs text-gray-500 line-clamp-2 leading-relaxed mb-2">{{ product.description }}</p>
              <div class="flex items-center gap-2">
                <span class="px-2 py-0.5 bg-purple-50 text-[#8b5cf6] text-[10px] font-bold rounded-lg">
                  {{ product.category }}
                </span>
                <span class="text-[10px] text-gray-400 font-medium">{{ getSchoolName(product.schoolId) }}</span>
              </div>
            </div>

            <div class="flex items-center justify-between mt-2">
              <div class="flex items-baseline text-[#8b5cf6]">
                <span class="text-xs font-bold">¥</span>
                <span class="text-xl font-black ml-0.5">{{ product.price }}</span>
                <span v-if="product.originalPrice" class="ml-2 text-xs text-gray-300 line-through">¥{{ product.originalPrice }}</span>
              </div>
              
              <div class="flex items-center gap-2">
                <el-button circle size="small" @click.stop="handleContact(product)">
                  <el-icon><ChatDotRound /></el-icon>
                </el-button>
                <el-button circle size="small" :type="isProductFavorited(product.id) ? 'danger' : 'default'" @click.stop="handleFavorite(product)">
                  <el-icon v-if="isProductFavorited(product.id)"><StarFilled /></el-icon>
                  <el-icon v-else><Star /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="py-20 flex flex-col items-center text-gray-400">
        <el-empty :description="loading ? '正在加载...' : '该分类下暂无商品'" />
      </div>
    </div>

    <!-- Filter Drawer -->
    <el-drawer
      v-model="showFilter"
      title="筛选商品"
      direction="btt"
      size="60%"
      class="filter-drawer"
      :with-header="false">
      <div class="p-6">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-xl font-bold text-gray-900">筛选条件</h2>
          <el-button link @click="resetFilters">重置</el-button>
        </div>

        <div class="space-y-8">
          <!-- Price Range -->
          <div class="space-y-4">
            <h3 class="text-sm font-bold text-gray-700">价格区间 (¥)</h3>
            <div class="flex items-center gap-4">
              <el-input-number v-model="filters.minPrice" :min="0" placeholder="最低" controls-position="right" class="flex-1" />
              <span class="text-gray-300">-</span>
              <el-input-number v-model="filters.maxPrice" :min="0" placeholder="最高" controls-position="right" class="flex-1" />
            </div>
          </div>

          <!-- Condition -->
          <div class="space-y-4">
            <h3 class="text-sm font-bold text-gray-700">成色要求</h3>
            <div class="flex flex-wrap gap-3">
              <div v-for="c in conditionOptions" :key="c.value"
                class="px-4 py-2 rounded-xl text-sm transition-all cursor-pointer"
                :class="filters.minCondition === c.value ? 'bg-[#8b5cf6] text-white font-bold' : 'bg-gray-50 text-gray-600'"
                @click="filters.minCondition = c.value">
                {{ c.label }}
              </div>
            </div>
          </div>

          <!-- Sort -->
          <div class="space-y-4">
            <h3 class="text-sm font-bold text-gray-700">排序方式</h3>
            <div class="flex flex-wrap gap-3">
              <div v-for="s in sortOptions" :key="s.value"
                class="px-4 py-2 rounded-xl text-sm transition-all cursor-pointer"
                :class="filters.sortBy === s.value ? 'bg-[#8b5cf6] text-white font-bold' : 'bg-gray-50 text-gray-600'"
                @click="filters.sortBy = s.value">
                {{ s.label }}
              </div>
            </div>
          </div>
        </div>

        <div class="mt-12">
          <el-button type="primary" class="w-full h-12 rounded-2xl bg-[#8b5cf6] border-none text-lg font-bold" @click="applyFilters">
            确定
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Filter, ChatDotRound, Star, StarFilled } from '@element-plus/icons-vue'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { useUserStore } from '@/stores/user'
import { formatImageUrl } from '@/utils/url'
import { useSchoolStore } from '@/stores/school'
import { ElMessage } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const schoolStore = useSchoolStore()
const userStore = useUserStore()

const loading = ref(false)
const products = ref([])
const activeTab = ref('all')
const showFilter = ref(false)
const favoriteIds = ref(new Set())

const tabs = [
  { id: 'all', label: '全部数码' },
  { id: 'phone', label: '手机', keyword: '手机' },
  { id: 'laptop', label: '电脑', keyword: '电脑|笔记本' },
  { id: 'tablet', label: '平板', keyword: '平板|iPad' },
  { id: 'audio', label: '耳机', keyword: '耳机|AirPods' }
]

const filters = reactive({
  minPrice: null,
  maxPrice: null,
  minCondition: null,
  sortBy: 'createTime'
})

const conditionOptions = [
  { label: '不限', value: null },
  { label: '9成新以上', value: 9 },
  { label: '8成新以上', value: 8 },
  { label: '7成新以上', value: 7 }
]

const sortOptions = [
  { label: '最新发布', value: 'createTime' },
  { label: '价格最低', value: 'priceAsc' },
  { label: '价格最高', value: 'priceDesc' },
  { label: '最热浏览', value: 'viewCount' }
]

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatImageUrl(images)
  }
}

const getSchoolName = (id) => schoolStore.getSchoolName(String(id)) || '校内'
const formatTime = (t) => dayjs(t).fromNow()
const getConditionText = (score) => {
  if (score >= 9) return '99新'
  if (score >= 8) return '95新'
  if (score >= 7) return '9成新'
  return '8成新'
}

const goDetail = (id) => router.push(`/user/product/${id}`)

const fetchProducts = async () => {
  loading.value = true
  try {
    const currentTab = tabs.find(t => t.id === activeTab.value)
    const params = {
      pageNum: 1,
      pageSize: 20,
      category: '电子产品',
      schoolId: schoolStore.selectedSchool ? Number(schoolStore.selectedSchool) : undefined,
      keyword: currentTab?.keyword || '',
      ...filters
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200' || res.code === 200) {
      const records = res.data.records || []
      if (records.length === 0 && schoolStore.selectedSchool) {
        const fallbackParams = { ...params }
        delete fallbackParams.schoolId
        const fallbackRes = await productApi.getProductList(fallbackParams)
        if (fallbackRes.code === '200' || fallbackRes.code === 200) {
          products.value = fallbackRes.data.records || []
        } else {
          products.value = []
        }
      } else {
        products.value = records
      }
      if (userStore.isLoggedIn) {
        fetchFavorites()
      }
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchFavorites = async () => {
  try {
    const res = await favoriteApi.getFavoriteList({ pageNum: 1, pageSize: 100 })
    if (res.code === '200' || res.code === 200) {
      favoriteIds.value = new Set(res.data.records.map(f => f.productId))
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  }
}

const isProductFavorited = (id) => favoriteIds.value.has(id)

const handleFavorite = async (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await favoriteApi.toggleFavorite(product.id)
    if (res.code === '200' || res.code === 200) {
      if (favoriteIds.value.has(product.id)) {
        favoriteIds.value.delete(product.id)
        ElMessage.success('已取消收藏')
      } else {
        favoriteIds.value.add(product.id)
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
  }
}

const handleContact = (product) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: `/user/chat/${product.userId}`,
    query: {
      productId: product.id
    }
  })
}

const applyFilters = () => {
  showFilter.value = false
  fetchProducts()
}

const resetFilters = () => {
  filters.minPrice = null
  filters.maxPrice = null
  filters.minCondition = null
  filters.sortBy = 'createTime'
  applyFilters()
}

watch([activeTab, () => schoolStore.selectedSchool], fetchProducts)
onMounted(fetchProducts)
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
</style>
