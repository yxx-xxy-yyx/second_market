<template>
  <div class="app-product-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        :placeholder="$t('common.search')"
        size="small"
        clearable
        @keyup.enter="doSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon class="cursor-pointer" :size="16" @click="goBack"><ArrowLeft /></el-icon>
        </template>
        <template #suffix>
          <el-icon class="cursor-pointer text-cyan-600" :size="16" @click="doSearch"><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 主筛选栏 -->
    <div class="filter-bar scroll-x">
      <div class="filter-item expandable" @click="showSortMenu = !showSortMenu">
        <span>{{ sortLabel }}</span>
        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
      </div>
      <div class="filter-item price-sort-item" :class="{ active: isPriceSortActive }" @click="togglePriceSort">
        <span>价格</span>
        <div class="price-arrows">
          <span class="arrow up" :class="{ active: !priceSortAsc }">↑</span>
          <span class="arrow down" :class="{ active: priceSortAsc }">↓</span>
        </div>
      </div>
      <div class="filter-item" @click="filterByPriceDrop">降价</div>
      <div class="filter-item" @click="filterByNewest">新发</div>
      <div class="filter-item" @click="filterByHot">浏览量</div>
      <div class="filter-item expandable" @click="openAreaMenu">
        <span>{{ schoolStore.selectedSchool ? schoolStore.currentSchoolName : '区域' }}</span>
        <el-icon class="arrow-icon"><ArrowDown /></el-icon>
      </div>
    </div>

    <!-- 排序下拉菜单 -->
    <div class="sort-dropdown" v-if="showSortMenu">
      <div class="sort-dropdown-item" :class="{ active: filters.sortBy === 'default' }" @click="selectSort('default')">综合排序</div>
      <div class="sort-dropdown-item" :class="{ active: filters.sortBy === 'credit' }" @click="selectSort('credit')">商家信用度</div>
    </div>

    <!-- 商品列表头部 -->
    <div class="product-header">
      <div class="result-tip">找到 {{ total }} 件商品</div>
      <div class="header-right">
        <div class="view-filter-btn" @click="showFullFilterMenu = true">
          筛选
        </div>
        <div class="view-toggle-btn" @click="toggleListView">
          <span class="view-icon">
            {{ listViewType === 'grid' ? '▦' : '☰' }}
          </span>
        </div>
      </div>
    </div>

    <!-- 4. 商品列表：下拉加载更多 -->
    <div class="product-container" v-loading="loading" ref="productListRef" @scroll="handleScroll">
      <div class="product-list-wrapper" :class="listViewType">
        <div 
          class="product-card" 
          v-for="product in products" 
          :key="product.id"
          @click="handleProductClick(product.id)"
        >
          <div class="product-img-wrap">
            <img 
              :src="getProductImage(product.images)" 
              :alt="product.title" 
              class="product-img"
              loading="lazy"
              decoding="async"
              @error="handleImgError"
            />
            <div class="ai-tag" v-if="product.aiAnalyzed">AI评估</div>
          </div>
          
          <div class="product-info-wrap">
            <div class="product-title" v-html="highlightKeyword(locale === 'ko' && product.titleKo ? product.titleKo : product.title)"></div>
            <div class="price-row">
              <span class="current-price">{{ $t('common.currency') }}{{ product.price }}</span>
              <span class="original-price" v-if="product.originalPrice">{{ $t('common.currency') }}{{ product.originalPrice }}</span>
            </div>
            <div class="attr-row">
              <span class="condition-score">{{ product.conditionScore || '' }}</span>
              <span class="view-count">
                <el-icon><View /></el-icon>{{ product.viewCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div class="load-more-tip" v-if="loadingMore">{{ $t('common.loading') }}</div>
      <div class="no-more-tip" v-if="!hasMore && !loading">已加载全部商品</div>

      <div v-if="!loading && products.length === 0" class="empty-state">
        <el-empty :description="emptyDescription" :image-size="150" />
        <div v-if="fallbackProducts.length" class="mt-4">
          <div class="flex items-center justify-between px-2">
            <div class="text-sm font-semibold text-gray-800">{{ $t('product.fallbackRecommendTitle') }}</div>
            <button class="text-sm text-orange-500" @click="handleClearSchoolFilter">{{ $t('product.viewAllSchools') }}</button>
          </div>
          <div class="mt-3 space-y-3">
            <div
              v-for="p in fallbackProducts"
              :key="p.id"
              class="product-card"
              @click="handleProductClick(p.id)"
            >
              <div class="product-img-wrap">
                <img :src="getProductImage(p.images)" class="product-img" loading="lazy" decoding="async" @error="handleImgError" />
              </div>
              <div class="product-info-wrap">
                <div class="product-title">{{ p.title }}</div>
                <div class="price-row">
                  <span class="current-price">{{ $t('common.currency') }}{{ p.price }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ========== 基础筛选菜单 ========== -->
    <div class="overlay" v-if="showAnyMenu && !showFullFilterMenu" @click="closeAllMenus"></div>

    <!-- 分类菜单 -->
    <div class="filter-menu-container" v-if="showCategoryMenu">
      <div class="menu-content">
        <CategoryGrid v-model="tempCategory" :items="categories" :columns="3" :gap="10" :icon-size="18" :show-all="true" />
        <div class="menu-actions">
          <el-button @click="resetCategory">重置</el-button>
          <el-button type="primary" @click="confirmCategory">确定</el-button>
        </div>
      </div>
    </div>

    <!-- 3. 成色菜单 -->
    <div class="filter-menu-container" v-if="showConditionMenu">
      <div class="menu-content">
        <div class="menu-grid">
          <div class="menu-item" :class="{ active: tempCondition === '9-10' }" @click="tempCondition = '9-10'">9成新以上</div>
          <div class="menu-item" :class="{ active: tempCondition === '7-9' }" @click="tempCondition = '7-9'">7-9成新</div>
          <div class="menu-item" :class="{ active: tempCondition === '0-7' }" @click="tempCondition = '0-7'">7成新以下</div>
        </div>
        <div class="menu-actions">
          <el-button @click="resetCondition">重置</el-button>
          <el-button type="primary" @click="confirmCondition">确定</el-button>
        </div>
      </div>
    </div>

    <!-- 4. AI推荐菜单 -->
    <div class="filter-menu-container" v-if="showAiMenu">
      <div class="menu-content">
        <div class="menu-grid">
          <div class="menu-item" :class="{ active: tempAi === '' }" @click="tempAi = ''">全部商品</div>
          <div class="menu-item" :class="{ active: tempAi === 'only' }" @click="tempAi = 'only'">仅AI评估商品</div>
          <div class="menu-item" :class="{ active: tempAi === 'priority' }" @click="tempAi = 'priority'">AI评估优先</div>
        </div>
        <div class="menu-actions">
          <el-button @click="resetAi">重置</el-button>
          <el-button type="primary" @click="confirmAi">确定</el-button>
        </div>
      </div>
    </div>

    <!-- 5. 价格菜单 -->
    <div class="filter-menu-container" v-if="showPriceMenu">
      <div class="menu-content price-menu">
        <div class="price-quick-grid">
          <div class="price-item" :class="{ active: priceQuick === '0-100' }" @click="selectPriceQuick('0-100')">100元以下</div>
          <div class="price-item" :class="{ active: priceQuick === '100-500' }" @click="selectPriceQuick('100-500')">100-500元</div>
          <div class="price-item" :class="{ active: priceQuick === '500-1000' }" @click="selectPriceQuick('500-1000')">500-1000元</div>
          <div class="price-item" :class="{ active: priceQuick === '1000-3000' }" @click="selectPriceQuick('1000-3000')">1000-3000元</div>
          <div class="price-item" :class="{ active: priceQuick === '3000-5000' }" @click="selectPriceQuick('3000-5000')">3000-5000元</div>
          <div class="price-item" :class="{ active: priceQuick === '5000-10000' }" @click="selectPriceQuick('5000-10000')">5000-10000元</div>
        </div>
        
        <div class="price-custom-wrap">
          <el-input v-model.number="customMinPrice" placeholder="最低价" type="number" />
          <span class="price-split">-</span>
          <el-input v-model.number="customMaxPrice" placeholder="最高价" type="number" />
        </div>
        
        <div class="price-slider-wrap">
          <el-slider v-model="priceRange" range :min="0" :max="10000" :step="100" />
          <div class="price-range-text">¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}</div>
        </div>
        
        <div class="menu-actions">
          <el-button @click="resetPriceFilter">重置</el-button>
          <el-button type="primary" @click="confirmPriceFilter">确定</el-button>
        </div>
      </div>
    </div>

    <!-- 6. 区域/学校选择菜单 -->
    <div class="filter-menu-container" v-if="showAreaMenu">
      <div class="menu-content">
        <div class="menu-grid">
          <div class="menu-item" :class="{ active: schoolStore.selectedSchool === '' }" @click="selectSchool('')">全部学校</div>
          <div class="menu-item" :class="{ active: schoolStore.selectedSchool === school.value }" @click="selectSchool(school.value)" v-for="school in schoolStore.schoolList" :key="school.value">{{ school.label }}</div>
        </div>
      </div>
    </div>

    <!-- 7. 完整筛选菜单（已修复：文字不重复） -->
    <div class="full-filter-overlay" v-if="showFullFilterMenu" @click.self="closeAllMenus">
      <div class="full-filter-container">
        <div class="filter-sidebar">
          <div class="sidebar-item" :class="{ active: activeFilterTab === 'category' }" @click="scrollToSectionWithLock('category')">分类</div>
          <div class="sidebar-item" :class="{ active: activeFilterTab === 'price' }" @click="scrollToSectionWithLock('price')">价格区间</div>
          <div class="sidebar-item" :class="{ active: activeFilterTab === 'condition' }" @click="scrollToSectionWithLock('condition')">成色筛选</div>
          <div class="sidebar-item" :class="{ active: activeFilterTab === 'ai' }" @click="scrollToSectionWithLock('ai')">AI推荐</div>
        </div>
        <div class="filter-content-area" ref="filterContentRef" @scroll="handleFilterScroll">
          <!-- 分类（已修复：文字不重复，原版8个分类） -->
          <div class="filter-section" id="category">
            <h3 class="section-title">分类</h3>
            <CategoryGrid v-model="filters.category" :items="categories" :columns="3" :gap="10" :icon-size="18" :show-all="true" />
          </div>

          <!-- 价格区间 -->
          <div class="filter-section" id="price">
            <h3 class="section-title">价格区间</h3>
            <div class="price-quick-grid">
              <div class="price-item" :class="{ active: priceQuick === '0-100' }" @click="selectPriceQuick('0-100')">100元以下</div>
              <div class="price-item" :class="{ active: priceQuick === '100-500' }" @click="selectPriceQuick('100-500')">100-500元</div>
              <div class="price-item" :class="{ active: priceQuick === '500-1000' }" @click="selectPriceQuick('500-1000')">500-1000元</div>
              <div class="price-item" :class="{ active: priceQuick === '1000-3000' }" @click="selectPriceQuick('1000-3000')">1000-3000元</div>
              <div class="price-item" :class="{ active: priceQuick === '3000-5000' }" @click="selectPriceQuick('3000-5000')">3000-5000元</div>
              <div class="price-item" :class="{ active: priceQuick === '5000-10000' }" @click="selectPriceQuick('5000-10000')">5000-10000元</div>
            </div>
            
            <div class="price-custom-wrap">
              <el-input v-model.number="customMinPrice" placeholder="最低价" type="number" />
              <span class="price-split">-</span>
              <el-input v-model.number="customMaxPrice" placeholder="最高价" type="number" />
            </div>
            
            <div class="price-slider-wrap">
              <el-slider v-model="priceRange" range :min="0" :max="10000" :step="100" />
              <div class="price-range-text">¥{{ priceRange[0] }} - ¥{{ priceRange[1] }}</div>
            </div>
          </div>

          <!-- 成色筛选 -->
          <div class="filter-section" id="condition">
            <h3 class="section-title">成色筛选</h3>
            <div class="option-grid">
              <label class="option-item">
                <el-checkbox v-model="filters.conditionScores" value="9-10" />
                9成新以上
              </label>
              <label class="option-item">
                <el-checkbox v-model="filters.conditionScores" value="7-9" />
                7-9成新
              </label>
              <label class="option-item">
                <el-checkbox v-model="filters.conditionScores" value="0-7" />
                7成新以下
              </label>
            </div>
          </div>

          <!-- AI推荐 -->
          <div class="filter-section" id="ai">
            <h3 class="section-title">AI推荐</h3>
            <div class="option-grid">
              <label class="option-item">
                <el-radio v-model="filters.aiFilter" label="">全部商品</el-radio>
              </label>
              <label class="option-item">
                <el-radio v-model="filters.aiFilter" label="only">仅AI评估商品</el-radio>
              </label>
              <label class="option-item">
                <el-radio v-model="filters.aiFilter" label="priority">AI评估优先</el-radio>
              </label>
            </div>
          </div>
        </div>
        <div class="filter-footer">
          <el-button class="reset-btn" @click="handleReset">重置筛选</el-button>
          <el-button type="primary" class="confirm-btn" @click="confirmFullFilter">确定</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { View, ArrowDown, Search, ArrowLeft } from '@element-plus/icons-vue'
import { useSchoolStore } from '@/stores/school'
import { CATEGORIES, categoryIdToDbValue, normalizeCategory } from '@/utils/categories'
import CategoryGrid from '@/components/category/CategoryGrid.vue'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
const schoolStore = useSchoolStore()

// ========== 搜索栏 ==========
const searchKeyword = ref(route.query.keyword || '')

const doSearch = () => {
  const keyword = searchKeyword.value.trim()
  filters.value.keyword = keyword
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/user/search')
  }
}

// ========== 基础状态 ==========
const loading = ref(false)
const products = ref([])
const fallbackProducts = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const priceRange = ref([0, 10000])

const listViewType = ref('grid')

const filters = ref({
  keyword: route.query.keyword || '',
  category: normalizeCategory(route.query.category || ''),
  minPrice: null,
  maxPrice: null,
  conditionScores: [],
  sortBy: route.query.sort || 'default',
  aiFilter: '',
  subFilter: 'personal'
})

const tempCategory = ref(filters.value.category)
const tempCondition = ref(filters.value.conditionScores[0] || '')
const tempAi = ref(filters.value.aiFilter)

const priceQuick = ref('')
const customMinPrice = ref(null)
const customMaxPrice = ref(null)

const priceSortAsc = ref(false)
const isPriceSortActive = computed(() => filters.value.sortBy === 'priceAsc' || filters.value.sortBy === 'priceDesc')

const DEFAULT_IMAGE = 'https://via.placeholder.com/300x300?text=No+Image'

// 已恢复你原版的8个分类
const categories = CATEGORIES

const activeFilterTab = ref('category')
const filterContentRef = ref(null)
const productListRef = ref(null)

const loadingMore = ref(false)
const hasMore = ref(true)

// ========== 菜单控制 ==========
const showAreaMenu = ref(false)
const showCategoryMenu = ref(false)
const showPriceMenu = ref(false)
const showConditionMenu = ref(false)
const showAiMenu = ref(false)
const showFullFilterMenu = ref(false)

const showAnyMenu = computed(() => 
  showSortMenu.value || showCategoryMenu.value || showPriceMenu.value || 
  showConditionMenu.value || showAiMenu.value || showFullFilterMenu.value || showAreaMenu.value
)

// ========== 核心方法 ==========
const toggleListView = () => {
  listViewType.value = listViewType.value === 'grid' ? 'list' : 'grid'
}

const togglePriceSort = () => {
  priceSortAsc.value = !priceSortAsc.value
  filters.value.sortBy = priceSortAsc.value ? 'priceAsc' : 'priceDesc'
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const closeAllMenus = () => {
  showSortMenu.value = false
  showCategoryMenu.value = false
  showPriceMenu.value = false
  showConditionMenu.value = false
  showAiMenu.value = false
  showAreaMenu.value = false
  showFullFilterMenu.value = false
}

const scrollToSection = (sectionId) => {
  activeFilterTab.value = sectionId
  nextTick(() => {
    const section = document.getElementById(sectionId)
    if (section && filterContentRef.value) {
      // 计算section在滚动容器中的位置
      const container = filterContentRef.value
      const containerRect = container.getBoundingClientRect()
      const sectionRect = section.getBoundingClientRect()
      const offsetTop = sectionRect.top - containerRect.top + container.scrollTop
      container.scrollTo({ top: offsetTop - 10, behavior: 'smooth' })
    }
  })
}

// 滚动内容区域时自动更新左侧导航高亮
const sectionIds = ['category', 'price', 'condition', 'ai']
let isScrollingFromClick = false

const handleFilterScroll = () => {
  if (!filterContentRef.value) return
  if (isScrollingFromClick) return

  const container = filterContentRef.value
  const scrollTop = container.scrollTop
  const maxScroll = container.scrollHeight - container.clientHeight
  if (maxScroll <= 0) return

  // 根据滚动比例切换导航高亮
  const ratio = scrollTop / maxScroll
  if (ratio < 0.05) {
    activeFilterTab.value = 'category'
  } else if (ratio < 0.5) {
    activeFilterTab.value = 'price'
  } else if (ratio < 0.85) {
    activeFilterTab.value = 'condition'
  } else {
    activeFilterTab.value = 'ai'
  }
}

// 点击导航时临时禁止滚动监听自动切换
const originalScrollToSection = scrollToSection
const scrollToSectionWithLock = (sectionId) => {
  isScrollingFromClick = true
  activeFilterTab.value = sectionId
  nextTick(() => {
    const section = document.getElementById(sectionId)
    if (section && filterContentRef.value) {
      const container = filterContentRef.value
      const containerRect = container.getBoundingClientRect()
      const sectionRect = section.getBoundingClientRect()
      const offsetTop = sectionRect.top - containerRect.top + container.scrollTop
      container.scrollTo({ top: offsetTop - 10, behavior: 'smooth' })
    }
    setTimeout(() => {
      isScrollingFromClick = false
    }, 600)
  })
}

// 下拉加载更多
const handleScroll = () => {
  if (!hasMore.value || loadingMore.value || loading.value) return
  const container = productListRef.value
  if (!container) return
  
  const { scrollTop, scrollHeight, clientHeight } = container
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    loadingMore.value = true
    pageNum.value++
    fetchProducts(true)
  }
}

// ========== 数据请求 ==========
const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return arr[0] || DEFAULT_IMAGE
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
}

const highlightKeyword = (text) => {
  if (!filters.value.keyword) return text
  return text.replace(new RegExp(`(${filters.value.keyword})`, 'gi'), `<span style="color: #ff4400; font-weight:600;">$1</span>`)
}

const fetchProducts = async (isLoadMore = false) => {
  if (isLoadMore) {
    loadingMore.value = true
  } else {
    loading.value = true
    products.value = []
    pageNum.value = 1
  }

  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 2
    }
    if (schoolStore.selectedSchool) params.schoolId = Number(schoolStore.selectedSchool)
    if (filters.value.keyword) params.keyword = filters.value.keyword
    if (filters.value.category) params.category = categoryIdToDbValue(filters.value.category)
    if (priceRange.value[0] > 0) params.minPrice = priceRange.value[0]
    if (priceRange.value[1] < 10000) params.maxPrice = priceRange.value[1]
    if (filters.value.conditionScores.length > 0) {
      let min = 0, max = 10
      filters.value.conditionScores.forEach(range => {
        const [rMin, rMax] = range.split('-').map(Number)
        if (rMin < min) min = rMin
        if (rMax > max) max = rMax
      })
      params.minCondition = min
      params.maxCondition = max
    }
    if (filters.value.sortBy !== 'default') params.sortBy = filters.value.sortBy
    if (filters.value.aiFilter) params.aiFilter = filters.value.aiFilter

    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      const newProducts = res.data.records || []
      total.value = res.data.total || 0
      
      if (isLoadMore) {
        products.value.push(...newProducts)
      } else {
        products.value = newProducts
      }

      hasMore.value = products.value.length < total.value
      if (!isLoadMore && products.value.length === 0 && schoolStore.selectedSchool) {
        await fetchFallbackProducts()
      } else if (!isLoadMore) {
        fallbackProducts.value = []
      }
    }
  } catch (error) {
    ElMessage.error(t('product.fetchFailed'))
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const emptyDescription = computed(() => {
  if (schoolStore.selectedSchool && filters.value.category) {
    return t('product.emptyBySchoolCategory')
  }
  if (schoolStore.selectedSchool) {
    return t('product.emptyBySchool')
  }
  return t('product.noProducts')
})

const fetchFallbackProducts = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 6,
      status: 2,
      sortBy: 'createTime'
    }
    if (filters.value.keyword) params.keyword = filters.value.keyword
    if (filters.value.category) params.category = categoryIdToDbValue(filters.value.category)
    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      fallbackProducts.value = res.data.records || []
    }
  } catch (e) {
    fallbackProducts.value = []
  }
}

const handleClearSchoolFilter = () => {
  schoolStore.setSchool('')
}

// ========== 筛选操作 ==========
const switchCategory = (category) => {
  filters.value.category = category
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const filterByNewest = () => {
  filters.value.sortBy = 'createTime'
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const filterByPriceDrop = () => {
  ElMessage.info(t('common.featureInDev'))
}

const filterByHot = () => {
  filters.value.sortBy = 'hot'
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

// ========== 菜单操作方法 ==========
const showSortMenu = ref(false)

const sortLabel = computed(() => filters.value.sortBy === 'credit' ? '商家信用度' : '综合排序')

const selectSort = (val) => {
  filters.value.sortBy = val
  showSortMenu.value = false
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const resetCategory = () => { tempCategory.value = '' }
const confirmCategory = () => {
  filters.value.category = tempCategory.value
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const resetCondition = () => { tempCondition.value = '' }
const confirmCondition = () => {
  filters.value.conditionScores = tempCondition.value ? [tempCondition.value] : []
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const resetAi = () => { tempAi.value = '' }
const confirmAi = () => {
  filters.value.aiFilter = tempAi.value
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const selectPriceQuick = (range) => {
  priceQuick.value = range
  const [min, max] = range.split('-').map(Number)
  priceRange.value = [min, max]
}
const resetPriceFilter = () => {
  priceQuick.value = ''
  customMinPrice.value = null
  customMaxPrice.value = null
  priceRange.value = [0, 10000]
}
const confirmPriceFilter = () => {
  if (customMinPrice.value !== null || customMaxPrice.value !== null) {
    priceRange.value = [customMinPrice.value || 0, customMaxPrice.value || 10000]
  }
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

// ========== 区域/学校选择 ==========
const openAreaMenu = () => {
  showAreaMenu.value = true
}

const selectSchool = (schoolValue) => {
  schoolStore.setSchool(schoolValue)
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

const confirmFullFilter = () => {
  closeAllMenus()
  pageNum.value = 1
  products.value = []
  fetchProducts()
}
const handleReset = () => {
  filters.value = {
    keyword: '',
    category: '',
    minPrice: null,
    maxPrice: null,
    conditionScores: [],
    sortBy: 'priceDesc',
    aiFilter: '',
    subFilter: 'personal'
  }
  priceRange.value = [0, 10000]
  priceQuick.value = ''
  customMinPrice.value = null
  customMaxPrice.value = null
  pageNum.value = 1
  products.value = []
  fetchProducts()
}

// ========== 路由 ==========
const handleProductClick = (productId) => {
  router.push(`/user/product/${productId}`)
}

watch(() => route.query, (newQuery) => {
  filters.value.keyword = newQuery.keyword || ''
  filters.value.category = normalizeCategory(newQuery.category || '')
  filters.value.sortBy = newQuery.sort || 'priceDesc'
  pageNum.value = 1
  products.value = []
  tempCategory.value = filters.value.category
  fetchProducts()
}, { deep: true })

watch(() => schoolStore.selectedSchool, () => {
  pageNum.value = 1
  products.value = []
  fetchProducts()
})

onMounted(() => {
  tempCategory.value = filters.value.category
  tempCondition.value = filters.value.conditionScores[0] || ''
  tempAi.value = filters.value.aiFilter
  fetchProducts()
})
</script>

<style scoped>
/* 搜索栏 */
.search-bar {
  position: sticky;
  top: 0;
  z-index: 110;
  background-color: #fff;
  padding: 8px 12px;
  border-bottom: 1px solid #eee;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

/* 全局样式 */
.app-product-list {
  min-height: 100vh;
  background-color: #fff;
  font-size: 14px;
  color: #333;
  position: relative;
  padding-bottom: 60px;
}

/* 通用横向滚动样式 */
.scroll-x {
  display: flex;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  position: relative;
}
.scroll-x::-webkit-scrollbar {
  display: none;
}

/* 顶部筛选栏 */
.filter-bar {
  background-color: #fff;
  padding: 8px 15px;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 52px;
  z-index: 100;
}
.sort-dropdown {
  position: fixed;
  top: 92px;
  left: 15px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
  z-index: 200;
  min-width: 130px;
  overflow: hidden;
}
.sort-dropdown-item {
  padding: 10px 16px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: background-color 0.15s;
}
.sort-dropdown-item:hover {
  background-color: #f5f5f5;
}
.sort-dropdown-item.active {
  color: var(--primary-color);
  font-weight: 600;
}
.filter-item {
  padding: 4px 8px;
  margin-right: 4px;
  white-space: nowrap;
  font-size: 14px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease, transform 0.05s ease;
}
.filter-item:active {
  transform: scale(0.99);
}
.filter-item.active {
  background-color: rgba(6, 182, 212, 0.12);
  color: var(--primary-color);
  font-weight: 600;
}
.price-sort-item.active {
  background-color: rgba(6, 182, 212, 0.12);
  color: var(--primary-color);
  font-weight: 600;
}
.filter-item.expandable { 
  background-color: #fff;
  color: #666;
}
.arrow-icon {
  font-size: 10px;
  color: #999;
}
.reset-btn {
  background-color: #fff;
  color: #666;
  border: 1px solid #eee;
}

/* 价格双箭头样式 */
.price-sort-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.price-arrows {
  display: flex;
  flex-direction: column;
  line-height: 1;
}
.price-arrows .arrow {
  font-size: 10px;
  color: #999;
}
.price-arrows .arrow.active {
  color: var(--primary-color);
  font-weight: bold;
  font-size: 12px;
}

/* 商品列表头部 */
.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  /* 让头部固定在顶部，不随内容滚动 */
  position: sticky;
  top: 92px; /* 搜索栏52px + 筛选栏40px */
  z-index: 99;
}
.result-tip {
  font-size: 13px;
  color: #666;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
/* 筛选按钮样式：和右边图标同高 */
.view-filter-btn {
  padding: 0 12px;
  height: 36px; /* 和 view-toggle-btn 高度一致 */
  line-height: 36px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  background-color: #fff;
  font-size: 14px;
  /* 文字居中 */
  display: flex;
  align-items: center;
  justify-content: center;
}
.view-toggle-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}
.view-icon {
  font-size: 18px;
  color: var(--primary-color);
  font-weight: bold;
}

/* 商品列表 */
.product-container { 
  padding: 10px;
  
  overflow-y: auto;
}
.product-list-wrapper {
  width: 100%;
}
.product-list-wrapper.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.product-list-wrapper.list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.product-list-wrapper.list .product-card {
  display: flex;
  gap: 10px;
  padding: 10px;
}
.product-list-wrapper.list .product-img-wrap {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.product-card {
  background-color: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}
.product-img-wrap {
  width: 100%;
  aspect-ratio: 1/1;
  position: relative;
}
.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.ai-tag {
  position: absolute;
  top: 5px;
  left: 5px;
  background-color: var(--primary-color);
  color: #fff;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 3px;
}
.product-info-wrap { 
  padding: 8px;
}
.product-title {
  font-size: 14px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.price-row { 
  margin: 6px 0; 
}
.current-price {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-color);
}
.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
  margin-left: 4px;
}
.attr-row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}
.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 加载更多提示 */
.load-more-tip, .no-more-tip {
  text-align: center;
  padding: 15px;
  color: #666;
  font-size: 13px;
}

/* 空状态 */
.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 基础筛选菜单 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 199;
}
.filter-menu-container {
  position: fixed;
  top: 110px;
  left: 0;
  right: 0;
  z-index: 200;
  background-color: #fff;
  border-radius: 12px 12px 0 0;
  max-height: 60vh;
  overflow-y: auto;
}
.menu-content {
  padding: 15px;
}
.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}
.menu-item {
  padding: 12px 0;
  text-align: center;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}
.menu-item.active {
  border-color: var(--primary-color);
  color: var(--primary-color);
  font-weight: 600;
}
.menu-actions {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
  margin: 0 -15px -15px;
  background-color: #fff;
}
.menu-actions .el-button {
  flex: 1;
  height: 44px;
  border-radius: 8px;
}
.menu-actions .el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  color: #fff;
}

/* 价格菜单样式 */
.price-menu {
  padding: 15px;
}
.price-quick-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}
.price-item {
  padding: 12px 0;
  text-align: center;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
}
.price-item.active {
  border-color: var(--primary-color);
  color: var(--primary-color);
}
.price-custom-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.price-custom-wrap .el-input {
  flex: 1;
}
.price-split {
  color: #999;
}
.price-slider-wrap {
  margin-bottom: 20px;
}
.price-range-text {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  color: var(--primary-color);
  font-size: 14px;
}

/* 完整筛选菜单 */
.full-filter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 200;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.full-filter-container {
  background-color: #fff;
  width: 100%;
  height: 70vh;
  border-radius: 0 0 12px 12px;
  display: flex;
  position: relative;
  flex-shrink: 0;
}
.filter-sidebar {
  width: 100px;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}
.sidebar-item {
  padding: 15px 10px;
  text-align: center;
  font-size: 14px;
  cursor: pointer;
}
.sidebar-item.active {
  background-color: #fff;
  color: #ff4400;
  font-weight: 600;
}
.filter-content-area {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  padding-bottom: 80px;
}
.filter-section {
  margin-bottom: 30px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}
.option-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}
.option-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
}
.filter-footer {
  display: flex;
  gap: 10px;
  padding: 15px;
  border-top: 1px solid #eee;
  background-color: #fff;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 10;
}
.filter-footer .el-button {
  flex: 1;
  height: 44px;
  border-radius: 8px;
}
.filter-footer .el-button--primary {
  background-color: #ff4400;
  border-color: #ff4400;
  color: #fff;
}

/* 适配移动端 */
@media (max-width: 768px) {
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .price-quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .option-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
