<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="sticky top-0 z-40 bg-white border-b border-gray-100">
      <div class="h-[52px] px-3 flex items-center" :style="{ paddingTop: 'env(safe-area-inset-top)' }">
        <el-input v-model="searchKey" :placeholder="$t('common.search')" size="small" clearable @keyup.enter="doSearch"
          autofocus class="flex-1">
          <template #prefix>
            <el-icon class="cursor-pointer" :size="18" @click="router.back()">
              <ArrowLeft />
            </el-icon>
          </template>
          <template #suffix>
            <el-icon class="cursor-pointer text-cyan-600" :size="18" @click="doSearch">
              <Search />
            </el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="px-3 py-4 space-y-6">
      <!-- 历史搜索 -->
      <div v-if="searchHistory.length > 0" class="space-y-2">
        <div class="flex items-center justify-between">
          <div class="text-sm font-semibold text-gray-900">{{ $t('common.searchHistory') }}</div>
          <button class="text-xs text-gray-400 hover:text-red-500" @click="clearSearchHistory">
            {{ $t('common.clearHistory') }}
          </button>
        </div>
        <div class="flex flex-wrap gap-2">
          <button v-for="(item, index) in searchHistory" :key="index"
            class="px-2.5 py-1.5 text-[11px] bg-gray-100 text-gray-700 rounded-full hover:bg-gray-200 active:scale-95 transition"
            @click="searchKey = item; doSearch()">
            {{ item }}
          </button>
        </div>
      </div>

      <!-- 猜你可能想找 -->
      <div v-if="recommendations.length > 0" class="space-y-2">
        <div class="text-sm font-semibold text-gray-900">{{ $t('common.guessYouLike') }}</div>
        <div class="grid grid-cols-2 gap-2">
          <button v-for="(item, index) in recommendations" :key="index"
            class="rounded-lg bg-white border border-gray-100 p-2.5 text-left hover:border-blue-300 hover:shadow-sm active:scale-95 transition"
            @click="searchKey = item.keyword; doSearch()">
            <div class="text-sm font-medium text-gray-900 line-clamp-2">{{ item.keyword }}</div>
            <div class="mt-1 text-[11px] text-gray-500">{{ item.count }}{{ $t('common.peopleSearch') }}</div>
          </button>
        </div>
      </div>

      <div
        v-if="hotRecommendProducts.length || latestProducts.length || recommendProducts.length || recentTrades.length"
        class="search-tab-section space-y-3">
        <div class="search-tabs flex items-center rounded-xl bg-gray-100 p-1">
          <button v-for="tab in searchTabs" :key="tab.key"
            :class="['search-tab flex-1 rounded-lg py-2 text-sm font-medium transition', activeSearchTab === tab.key ? 'bg-white text-cyan-600 shadow-sm' : 'text-gray-600']"
            @click="activeSearchTab = tab.key">
            {{ tab.label }}
          </button>
        </div>

        <div class="search-tab-content space-y-3">
          <div v-if="activeSearchTab === 'hot'" class="space-y-3">
            <div v-for="product in hotRecommendProducts" :key="product.id"
              class="search-product-card rounded-xl bg-white p-3 shadow-sm border border-gray-100"
              @click="handleProductClick(product.id)">
              <div class="flex items-center gap-3">
                <el-image :src="getProductImage(product.images)" fit="cover" class="w-20 h-20 rounded-xl" />
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ locale === 'ko' && product.titleKo ?
                    product.titleKo : product.title }}</div>
                  <div class="mt-2 text-xs text-gray-500">¥{{ product.price }} · {{ product.viewCount }} {{
                    $t('product.viewCount') }}</div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="activeSearchTab === 'latest'" class="space-y-3">
            <div v-for="product in latestProducts" :key="product.id"
              class="search-product-card rounded-xl bg-white p-3 shadow-sm border border-gray-100"
              @click="handleProductClick(product.id)">
              <div class="flex items-center gap-3">
                <el-image :src="getProductImage(product.images)" fit="cover" class="w-20 h-20 rounded-xl" />
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ locale === 'ko' && product.titleKo ?
                    product.titleKo : product.title }}</div>
                  <div class="mt-2 text-xs text-gray-500">¥{{ product.price }} · {{ formatTimeAgo(product.createTime ||
                    product.createDate) }}</div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="activeSearchTab === 'recommend'" class="space-y-3">
            <div v-for="product in recommendProducts" :key="product.id"
              class="search-product-card rounded-xl bg-white p-3 shadow-sm border border-gray-100"
              @click="handleProductClick(product.id)">
              <div class="flex items-center gap-3">
                <el-image :src="getProductImage(product.images)" fit="cover" class="w-20 h-20 rounded-xl" />
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ locale === 'ko' && product.titleKo ?
                    product.titleKo : product.title }}</div>
                  <div class="mt-2 text-xs text-gray-500">¥{{ product.price }} · {{ product.viewCount }} {{
                    $t('product.viewCount') }}</div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="activeSearchTab === 'live'" class="space-y-3">
            <div v-for="(trade, index) in recentTrades" :key="index"
              class="rounded-xl bg-white p-3 shadow-sm border border-gray-100">
              <div class="flex items-center gap-3">
                <div
                  :class="['w-12 h-12 rounded-xl flex items-center justify-center text-sm font-semibold text-white', trade.gradientClass]">
                  {{ trade.username ? trade.username.charAt(0) : '' }}
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-semibold text-gray-900">{{ trade.username }} {{ locale === 'zh' ? '购买了' :
                    '구매했습니다' }}</div>
                  <div class="mt-1 text-xs text-gray-500">{{ trade.productName }} · ¥{{ trade.price }}</div>
                </div>
                <div class="text-xs text-gray-400">{{ trade.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div
        v-if="searchHistory.length === 0 && recommendations.length === 0 && hotRecommendProducts.length === 0 && latestProducts.length === 0"
        class="py-12 text-center">
        <el-empty :description="$t('common.noData')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { productApi } from '@/api/product'
import { searchHistoryApi } from '@/api/search-history'
import { ElMessage } from 'element-plus'
import {
  Search,
  ArrowLeft
} from '@element-plus/icons-vue'

const router = useRouter()
const { t, locale } = useI18n()

const searchKey = ref('')
const searchHistory = ref([])
const recommendations = ref([])
const activeSearchTab = ref('hot')
const searchTabs = ref([
  { key: 'hot', label: t('product.hotRecommend') },
  { key: 'latest', label: t('product.latestPublish') },
  { key: 'recommend', label: t('dashboard.recommend') },
  { key: 'live', label: t('dashboard.tradeDynamic') }
])
const hotRecommendProducts = ref([])
const latestProducts = ref([])
const recommendProducts = ref([])
const gradientClasses = [
  'bg-gradient-to-br from-[#409eff] to-[#66b1ff]',
  'bg-gradient-to-br from-[#ff9800] to-[#ffc107]',
  'bg-gradient-to-br from-[#67c23a] to-[#85ce61]',
  'bg-gradient-to-br from-[#f56c6c] to-[#ff8a89]',
  'bg-gradient-to-br from-[#9155fd] to-[#b76bff]',
  'bg-gradient-to-br from-[#13c2c2] to-[#36d3d3]',
  'bg-gradient-to-br from-[#ff7f50] to-[#ffb066]',
  'bg-gradient-to-br from-[#00b96b] to-[#52c41a]'
]
const recentTrades = ref([
  { username: '张同学', productName: 'iPhone 13 Pro', price: 4999, time: '2分钟前', gradientClass: gradientClasses[0] },
  { username: '李同学', productName: '小米手环7', price: 199, time: '5分钟前', gradientClass: gradientClasses[1] },
  { username: '王同学', productName: '机械键盘', price: 299, time: '8分钟前', gradientClass: gradientClasses[2] },
  { username: '赵同学', productName: '运动鞋', price: 399, time: '12分钟前', gradientClass: gradientClasses[3] },
  { username: '刘同学', productName: '专业相机', price: 2999, time: '15分钟前', gradientClass: gradientClasses[4] },
  { username: '陈同学', productName: '蓝牙耳机', price: 399, time: '20分钟前', gradientClass: gradientClasses[5] }
])

const MAX_HISTORY = 10

const loadSearchHistory = async () => {
  try {
    const res = await searchHistoryApi.getHistory(MAX_HISTORY)
    if (res.code === '200' && Array.isArray(res.data)) {
      searchHistory.value = res.data.map(item => item.keyword).filter(Boolean)
    }
  } catch (error) {
    console.error('获取搜索历史失败:', error)
  }
}

const saveSearchHistory = async (keyword) => {
  if (!keyword.trim()) return
  await searchHistoryApi.record(keyword)
  await loadSearchHistory()
}

const clearSearchHistory = async () => {
  try {
    const res = await searchHistoryApi.clear()
    if (res.code === '200') {
      searchHistory.value = []
      ElMessage.success(t('common.historyCleared'))
    }
  } catch (error) {
    console.error('清空搜索历史失败:', error)
  }
}

const doSearch = async () => {
  const keyword = searchKey.value?.trim()
  if (!keyword) {
    ElMessage.warning(t('common.pleaseEnterKeyword'))
    return
  }

  try {
    await saveSearchHistory(keyword)
  } catch (error) {
    console.error('保存搜索历史失败:', error)
  }
  router.push({
    path: '/user/products',
    query: { keyword }
  })
}

const fetchRecommendations = async () => {
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      status: 2,
      sortBy: 'viewCount'
    })

    if (res.code === '200' && res.data.records) {
      const keywords = new Set()
      res.data.records.forEach(product => {
        const words = product.title.split(/[\s\-_\/\\。，、]+/).filter(w => w.length > 2)
        words.forEach(w => {
          if (keywords.size < 6) {
            keywords.add(w)
          }
        })
      })

      recommendations.value = Array.from(keywords).map(keyword => ({
        keyword,
        count: Math.floor(Math.random() * 100) + 10
      }))
    }
  } catch (error) {
    console.error('获取推荐搜索失败:', error)
  }
}

const fetchHotRecommendProducts = async () => {
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      status: 2,
      sortBy: 'viewCount'
    })
    if (res.code === '200' && res.data.records) {
      hotRecommendProducts.value = res.data.records.slice(0, 8)
    }
  } catch (error) {
    console.error('获取热门推荐商品失败:', error)
  }
}

const fetchRecommendProducts = async () => {
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      status: 2
    })
    if (res.code === '200' && res.data.records) {
      recommendProducts.value = res.data.records.slice(0, 8)
    }
  } catch (error) {
    console.error('获取为你推荐商品失败:', error)
  }
}

const fetchLatestProducts = async () => {
  try {
    const res = await productApi.getProductList({
      pageNum: 1,
      pageSize: 8,
      status: 2,
      sortBy: 'createTime'
    })
    if (res.code === '200' && res.data.records) {
      latestProducts.value = res.data.records
        .slice(0, 8)
        .sort((a, b) => new Date(b.createTime || b.createDate) - new Date(a.createTime || a.createDate))
    }
  } catch (error) {
    console.error('获取最新上架商品失败:', error)
  }
}

const getProductImage = (images) => {
  if (!images) return 'https://via.placeholder.com/200x200?text=No+Image'
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0]
    return firstImage || 'https://via.placeholder.com/200x200?text=No+Image'
  } catch {
    return 'https://via.placeholder.com/200x200?text=No+Image'
  }
}

const formatTimeAgo = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const diff = Date.now() - date.getTime()
  if (isNaN(diff) || diff < 0) return ''
  if (diff < 60000) return locale.value === 'ko' ? '방금 전' : '刚刚'
  if (diff < 3600000) return locale.value === 'ko' ? `${Math.floor(diff / 60000)}분 전` : `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return locale.value === 'ko' ? `${Math.floor(diff / 3600000)}시간 전` : `${Math.floor(diff / 3600000)}小时前`
  return locale.value === 'ko' ? `${Math.floor(diff / 86400000)}일 전` : `${Math.floor(diff / 86400000)}天前`
}

const handleProductClick = (productId) => {
  router.push(`/user/product/${productId}`)
}

onMounted(() => {
  loadSearchHistory()
  fetchRecommendations()
  fetchHotRecommendProducts()
  fetchRecommendProducts()
  fetchLatestProducts()
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #fff;
  padding-bottom: 60px;
}

.search-tab-section {
  margin-top: 8px;
}

.search-tabs {
  gap: 8px;
}

.search-tab {
  border: none;
  outline: none;
}

.search-tab:hover {
  background-color: #f3f4f6;
}

.search-product-card {
  cursor: pointer;
}
</style>
