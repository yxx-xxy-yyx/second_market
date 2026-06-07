<template>
  <div class="campus-page min-h-screen pb-16" style="padding-top: calc(68px + env(safe-area-inset-top));">
    <!-- 顶部状态栏占位 (移动端) -->
    <div class="h-safe-top bg-[#f8f8fa]"></div>

    <!-- 顶部固定区域 -->
    <div class="sticky z-50 bg-[#f8f8fa]/80 backdrop-blur-md px-4 py-2.5 space-y-3"
      :style="{ top: `calc(65px + env(safe-area-inset-top))` }">
      <!-- 模式切换与学校筛选 -->
      <div class="flex items-center justify-between">
        <div class="flex p-1 bg-[#f0f0f2] rounded-[12px] border border-gray-200/50 shadow-sm">
          <div v-for="mode in modes" :key="mode.value" @click="currentMode = mode.value"
            class="relative px-5 py-1.5 cursor-pointer transition-all duration-300 rounded-[10px] text-sm font-medium overflow-hidden"
            :class="currentMode === mode.value ? 'text-white shadow-inner-custom active-mode-bg' : 'text-gray-500 hover:text-gray-700'">
            {{ mode.label }}
          </div>
        </div>
        <!-- 学校筛选已移除，由顶部状态栏统一管理 -->
      </div>

      <!-- 模式A：发现模式的搜索栏 -->
      <div v-if="currentMode === 'discovery'" class="search-section">
        <div class="relative flex items-center group">
          <input v-model="searchKeyword" type="text" placeholder="搜索校内好物..."
            class="custom-capsule-input w-full pl-10 pr-24 py-2.5 bg-white text-sm outline-none transition-all duration-300"
            @keyup.enter="handleSearch" />
          <div class="absolute left-4 top-1/2 -translate-y-1/2 text-primary">
            <el-icon :size="18" class="font-light">
              <Search />
            </el-icon>
          </div>
          <!-- AI智搜标签 -->
          <div
            class="absolute right-3 top-1/2 -translate-y-1/2 px-3 py-1 bg-primary text-white text-[11px] rounded-full flex items-center space-x-1 cursor-pointer hover:opacity-90 transition-opacity shadow-sm"
            @click="handleAiSearch">
            <el-icon :size="12">
              <MagicStick />
            </el-icon>
            <span class="font-medium">AI智搜</span>
          </div>
        </div>

        <!-- 热门搜索 -->
        <div v-if="showSearchHistory && hotKeywords.length > 0" class="mt-2 flex flex-wrap gap-2">
          <div v-for="word in hotKeywords" :key="word" @click="quickSearch(word)"
            class="px-3 py-1 bg-white border border-gray-100 rounded-full text-[11px] text-gray-500 hover:border-primary/30 hover:text-primary transition-all duration-200 cursor-pointer">
            {{ word }}
          </div>
        </div>
      </div>

      <!-- 模式B：分类模式的横向标签 -->
      <div v-if="currentMode === 'category'" class="category-tabs-v2">
        <div class="flex overflow-x-auto no-scrollbar space-x-5 py-1">
          <div v-for="cat in categoriesList" :key="cat.id" @click="handleMainCategorySelect(cat)"
            class="flex flex-col items-center flex-shrink-0 space-y-1.5 cursor-pointer group clickable-effect">
            <div
              class="w-9 h-9 rounded-[12px] flex items-center justify-center transition-all duration-300 shadow-sm"
              :class="activeCategoryId === cat.id ? 'active-cat-icon text-white' : 'bg-white text-gray-400 group-hover:bg-gray-50'">
              <el-icon :size="18">
                <component :is="getCategoryIcon(cat.id)" />
              </el-icon>
            </div>
            <span class="text-[12px] font-medium transition-colors"
              :class="activeCategoryId === cat.id ? 'text-primary' : 'text-gray-500'">
              {{ $t('common.' + cat.id) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="px-4 py-3 space-y-5">
      <!-- 模式A：发现模式特有内容 -->
      <div v-if="currentMode === 'discovery'" class="space-y-5">
        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h2 class="text-base font-bold text-gray-900 tracking-tight">校园服务</h2>
          </div>
          <div class="grid grid-cols-2 gap-3">
            <div
              class="h-[88px] rounded-[18px] relative overflow-hidden cursor-pointer shadow-sm active:scale-[0.98] transition-all border border-gray-50 bg-gradient-to-br from-[#eef2ff] to-[#f5f3ff]"
              @click="router.push('/user/forum')">
              <div class="p-4 flex flex-col justify-between h-full">
                <div>
                  <div class="text-sm font-bold text-gray-900 tracking-tight">校园论坛</div>
                  <div class="text-[11px] text-gray-500 mt-1">本校/全校帖子实时看</div>
                </div>
                <div class="flex justify-end">
                  <el-icon class="text-gray-400">
                    <ArrowRight />
                  </el-icon>
                </div>
              </div>
            </div>
            <div
              class="h-[88px] rounded-[18px] relative overflow-hidden cursor-pointer shadow-sm active:scale-[0.98] transition-all border border-gray-50 bg-gradient-to-br from-[#ecfeff] to-[#e6fffb]"
              @click="router.push('/user/campus-nearby')">
              <div class="p-4 flex flex-col justify-between h-full">
                <div>
                  <div class="text-sm font-bold text-gray-900 tracking-tight">跑腿互助</div>
                  <div class="text-[11px] text-gray-500 mt-1">发布/接单一站搞定</div>
                </div>
                <div class="flex justify-end">
                  <el-icon class="text-gray-400">
                    <ArrowRight />
                  </el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 推荐分类流 -->
        <div class="recommend-categories grid grid-cols-2 gap-3">
          <div v-for="item in recommendCategories" :key="item.id"
            class="flex-shrink-0 h-[88px] rounded-[18px] relative overflow-hidden group cursor-pointer shadow-sm active:scale-[0.98] transition-all border border-gray-50"
            :style="{ background: item.bg }"
            @click="handleRecommendClick(item)">
            <div class="p-4 flex flex-col justify-between h-full relative z-10">
              <div>
                <span class="text-sm font-bold text-gray-800 block tracking-tight">{{ item.title }}</span>
                <span class="text-[11px] text-gray-500 mt-1 block">{{ item.desc }}</span>
              </div>
              <div class="flex justify-end">
                <el-icon class="text-gray-400 group-hover:translate-x-1 transition-transform duration-300">
                  <ArrowRight />
                </el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- 猜你喜欢 -->
        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h2 class="text-base font-bold text-gray-900 tracking-tight">猜你喜欢</h2>
            <div
              class="text-[11px] text-gray-400 flex items-center cursor-pointer hover:text-primary transition-all duration-300 group">
              <span>换一批</span>
              <el-icon class="ml-1 group-hover:rotate-180 transition-transform duration-500">
                <Refresh />
              </el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 模式B：细分类别区 -->
      <div v-if="currentMode === 'category'" class="space-y-3">
        <div class="flex flex-col space-y-3">
          <!-- 排序选项 -->
          <div class="flex items-center space-x-5 text-[13px]">
            <div v-for="sort in sortOptions" :key="sort.value" @click="currentSort = sort.value"
              class="relative py-1 cursor-pointer transition-all duration-300 font-medium"
              :class="currentSort === sort.value ? 'text-primary' : 'text-gray-400'">
              {{ sort.label }}
              <div v-if="currentSort === sort.value"
                class="absolute -bottom-0.5 left-0 right-0 h-0.5 bg-primary rounded-full"></div>
            </div>
          </div>

          <!-- 子分类标签 -->
          <div class="flex overflow-x-auto no-scrollbar space-x-2 pb-1">
            <div @click="activeSubCategoryId = null"
              class="px-4 py-1.5 rounded-full text-[12px] flex-shrink-0 transition-all duration-300 border font-medium"
              :class="activeSubCategoryId === null ? 'bg-primary border-primary text-white shadow-md shadow-primary/20' : 'bg-white border-gray-100 text-gray-600 hover:border-primary/20'">
              全部
            </div>
            <div v-for="sub in currentSubCategories" :key="sub.id" @click="activeSubCategoryId = sub.id"
              class="px-4 py-1.5 rounded-full text-[12px] flex-shrink-0 transition-all duration-300 border font-medium"
              :class="activeSubCategoryId === sub.id ? 'bg-primary border-primary text-white shadow-md shadow-primary/20' : 'bg-white border-gray-100 text-gray-600 hover:border-primary/20'">
              {{ sub.name }}
            </div>
          </div>
        </div>
      </div>

      <!-- 统一商品列表 -->
      <div class="product-grid">
        <div v-if="loading && products.length === 0" class="grid grid-cols-2 gap-3">
          <div v-for="i in 4" :key="i" class="bg-white p-2 rounded-[12px] animate-pulse">
            <div class="bg-gray-100 aspect-square rounded-[8px]"></div>
            <div class="h-4 bg-gray-100 rounded mt-4 w-3/4"></div>
            <div class="h-6 bg-gray-100 rounded mt-3 w-1/2"></div>
          </div>
        </div>

        <div v-else-if="products.length > 0" class="grid grid-cols-2 gap-3">
          <div v-for="product in products" :key="product.id"
            class="product-card-v2 bg-white rounded-[12px] p-2 transition-all duration-300 group cursor-pointer"
            @click="goDetail(product.id)">
            <!-- 商品图片 -->
            <div class="aspect-square relative overflow-hidden rounded-[8px] bg-gray-50 mb-2">
              <div class="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-primary/60 to-primary z-10"></div>
              <el-image :src="getProductImage(product.images)" fit="cover"
                class="w-full h-full group-hover:scale-105 transition-transform duration-500">
                <template #error>
                  <div class="flex items-center justify-center w-full h-full bg-gray-50 text-gray-300">
                    <el-icon :size="24">
                      <Picture />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <!-- 商品信息 -->
            <div class="px-1 pb-1 flex flex-col justify-between h-[62px]">
              <div class="flex flex-col space-y-1">
                <h3 class="text-[13px] font-medium text-gray-800 line-clamp-1 leading-tight">
                  {{ product.title }}
                </h3>
                <div>
                  <span
                    class="inline-block px-1.5 py-0.5 bg-gray-50 text-gray-400 text-[9px] rounded-md border border-gray-100">
                    {{ getSchoolName(product.schoolId) }}
                  </span>
                </div>
              </div>
              <div class="flex items-baseline justify-end text-primary">
                <span class="text-[10px] font-bold">¥</span>
                <span class="text-[17px] font-black ml-0.5 tracking-tight">{{ product.price }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="py-14 flex flex-col items-center justify-center text-gray-400">
          <el-empty description="暂无相关商品" :image-size="108" />
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="py-8 flex justify-center">
          <el-button :loading="loading" text
            class="text-gray-300 text-[11px] tracking-widest uppercase hover:text-primary transition-colors"
            @click="loadMore">
            {{ loading ? 'Loading...' : 'Load More' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { markRaw, ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search, MagicStick, ArrowRight, Refresh, Picture, Menu } from '@element-plus/icons-vue'
import { CATEGORIES } from '@/utils/categories'
import { CATEGORY_ICONS } from '@/components/icons/categories'
import { productApi } from '@/api/product'
import { schoolApi } from '@/api/school'
import { formatProductImageUrl } from '@/utils/url'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

import { useSchoolStore } from '@/stores/school'

const schoolStore = useSchoolStore()
const router = useRouter()

// 模式定义
const modes = [
  { label: '发现', value: 'discovery' },
  { label: '分类', value: 'category' }
]
const currentMode = ref('discovery')

// 暴露 CATEGORIES 给模板使用
const categoriesList = CATEGORIES

// 学校筛选
const selectedSchoolId = computed(() => {
  return schoolStore.selectedSchool ? Number(schoolStore.selectedSchool) : null
})

// 搜索
const searchKeyword = ref('')
const searchHistory = ref([])
const hotKeywords = ['二手手机', '考研资料', '宿舍神器', '自行车']
const showSearchHistory = ref(true)

// 分类
const activeCategoryId = ref(categoriesList && categoriesList.length > 0 ? categoriesList[0].id : '')
const activeSubCategoryId = ref(null)

// 商品数据
const products = ref([])
const loading = ref(false)
const pageNum = ref(1)
const hasMore = ref(true)
const currentSort = ref('newest')

const sortOptions = [
  { label: '最新', value: 'newest' },
  { label: '价格', value: 'price' }
]

// 推荐分类流
const recommendCategories = [
  { id: 'viewed', title: '我常看的', desc: '快速定位心水好物', bg: '#e0f7fa', path: '/user/recently-viewed' },
  { id: 'digital', title: '热门数码', desc: '学长学姐都在淘', bg: '#f3e5f5', path: '/user/popular-digital' }
]

// 计算当前子分类
const currentSubCategories = computed(() => {
  if (!activeCategoryId.value) return []
  const cat = categoriesList.find(c => c.id === activeCategoryId.value)
  return cat && cat.children ? cat.children : []
})

// 获取分类图标
const getCategoryIcon = (id) => {
  return CATEGORY_ICONS[id] || Menu
}

// 获取商品图片
const getProductImage = (images) => {
  if (!images) return formatProductImageUrl('')
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = Array.isArray(imageArray) ? imageArray[0] : imageArray
    return formatProductImageUrl(firstImage)
  } catch {
    return formatProductImageUrl(images)
  }
}

// 获取学校名称
const getSchoolName = (schoolId) => {
  if (!schoolId) return '校内'
  return schoolStore.getSchoolName(String(schoolId))
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).fromNow()
}

// 获取商品列表
const fetchProducts = async (isLoadMore = false) => {
  if (loading.value) return
  loading.value = true

  try {
    if (!isLoadMore) {
      pageNum.value = 1
      products.value = []
    }

    const params = {
      pageNum: pageNum.value,
      pageSize: 10,
      keyword: searchKeyword.value,
      status: 2
    }
    if (selectedSchoolId.value) {
      params.schoolId = selectedSchoolId.value
    }

    // 分类模式下的筛选
    if (currentMode.value === 'category') {
      const mainCat = categoriesList.find(c => c.id === activeCategoryId.value)
      params.category = mainCat ? mainCat.dbValue : null

      if (activeSubCategoryId.value) {
        const subCat = currentSubCategories.value.find(s => s.id === activeSubCategoryId.value)
        if (subCat) {
          params.keyword = subCat.name // 简单的通过关键词模拟子分类筛选
        }
      }

      if (currentSort.value === 'price') {
        params.sortBy = 'price_asc'
      } else {
        params.sortBy = 'create_time_desc'
      }
    }

    const res = await productApi.getProductList(params)
    if (res.code == 200 || res.success) {
      const newProducts = res.data.records || []
      if (!isLoadMore && newProducts.length === 0 && selectedSchoolId.value) {
        const fallbackParams = { ...params }
        delete fallbackParams.schoolId
        const fallbackRes = await productApi.getProductList(fallbackParams)
        if (fallbackRes.code == 200 || fallbackRes.success) {
          const fallbackRecords = fallbackRes.data.records || []
          products.value = fallbackRecords
          hasMore.value = fallbackRecords.length === 10
        } else {
          products.value = []
          hasMore.value = false
        }
      } else {
        products.value = isLoadMore ? [...products.value, ...newProducts] : newProducts
        hasMore.value = newProducts.length === 10
      }
    }
  } catch (err) {
    if (!isLoadMore) products.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 交互处理
const handleSchoolChange = (value) => {
  schoolStore.setSchool(value)
  fetchProducts()
}

const handleSearch = () => {
  fetchProducts()
}

const quickSearch = (word) => {
  searchKeyword.value = word
  fetchProducts()
}

const handleAiSearch = () => {
  router.push('/user/ai-chat')
}

const handleMainCategorySelect = (cat) => {
  if (activeCategoryId.value === cat.id) return
  activeCategoryId.value = cat.id
  activeSubCategoryId.value = null
  searchKeyword.value = '' // 切换分类时清空搜索词
  fetchProducts()
}

const handleRecommendClick = (item) => {
  if (item.path) {
    router.push(item.path)
  } else if (item.title === '热门数码') {
    activeCategoryId.value = 'electronics'
    currentMode.value = 'category'
    searchKeyword.value = ''
  }
}

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    pageNum.value++
    fetchProducts(true)
  }
}

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

// 监听模式切换
watch(currentMode, () => {
  fetchProducts()
})

// 监听子分类切换
watch(activeSubCategoryId, () => {
  fetchProducts()
})

// 监听排序切换
watch(currentSort, () => {
  fetchProducts()
})

onMounted(() => {
  schoolStore.getSchoolList()
  fetchProducts()
})
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}

.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.campus-page {
  max-width: 768px;
  margin: 0 auto;
  background-color: #f8f8fa;
}

.h-safe-top {
  height: env(safe-area-inset-top);
}

/* 胶囊按钮内阴影 */
.shadow-inner-custom {
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.active-mode-bg {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

/* 自定义输入框与下拉框 */
.custom-capsule-select :deep(.el-input__wrapper) {
  background-color: white !important;
  border-radius: 16px !important;
  border: 2px solid #06b6d4 !important;
  box-shadow: none !important;
  padding: 0 12px !important;
  height: 36px;
}

.custom-capsule-input {
  border-radius: 16px;
  border: 2px solid #06b6d4;
  box-shadow: 0 2px 10px rgba(6, 182, 212, 0.05);
}

.custom-capsule-input:focus {
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.15);
}

/* 分类面性渐变图标 */
.active-cat-icon {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

/* 玻璃拟态卡片 */
.glass-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

/* 商品卡片升级 */
.product-card-v2 {
  box-shadow: 0 8px 12px -4px rgba(6, 182, 212, 0.1);
}

.product-card-v2:hover {
  transform: translateY(-2px);
}
</style>
