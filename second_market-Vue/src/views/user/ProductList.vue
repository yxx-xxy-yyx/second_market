<template>
  <div class="product-list-page">
    <div class="list-container">
      <!-- 左侧筛选面板 -->
      <div class="filter-panel hidden lg:block">
        <div class="filter-section">
          <div class="filter-title">{{ $t('product.category') }}</div>
          <el-radio-group v-model="filters.category" class="filter-radios" @change="handleFilterChange">
            <el-radio label="">{{ $t('common.all') }}</el-radio>
            <el-radio v-for="cat in categories" :key="cat.id" :label="cat.id">
              {{ $t(`categories.${cat.id}`) }}
            </el-radio>
          </el-radio-group>
        </div>

        <el-divider />

        <!-- 价格区间滑块 -->
        <div class="filter-section">
          <div class="filter-title">{{ $t('product.priceRange') }}</div>
          <div class="price-slider-wrapper">
            <el-slider v-model="priceRange" range :min="0" :max="10000" :step="100" :format-tooltip="formatPrice"
              @change="handlePriceChange" />
            <div class="price-range-display">
              <span class="price-text">{{ $t('common.currency') }}{{ priceRange[0] }}</span>
              <span class="price-divider">-</span>
              <span class="price-text">{{ $t('common.currency') }}{{ priceRange[1] }}</span>
            </div>
          </div>
        </div>

        <el-divider />

        <div class="filter-section">
          <div class="filter-title">{{ $t('product.conditionFilter') }}</div>
          <el-checkbox-group v-model="filters.conditionScores" class="filter-checkboxes" @change="handleFilterChange">
            <el-checkbox label="9-10">{{ $t('product.above90') }}</el-checkbox>
            <el-checkbox label="7-9">{{ $t('product.excellent') }}</el-checkbox>
            <el-checkbox label="0-7">{{ $t('product.below70') }}</el-checkbox>
          </el-checkbox-group>
        </div>

        <el-divider />

        <div class="filter-section">
          <div class="filter-title">{{ $t('product.aiRecommend') }}</div>
          <el-radio-group v-model="filters.aiFilter" class="filter-radios" @change="handleFilterChange">
            <el-radio label="">{{ $t('product.allProducts') }}</el-radio>
            <el-radio label="aiOnly">{{ $t('product.aiOnly') }}</el-radio>
            <el-radio label="aiPriority">{{ $t('product.aiPriority') }}</el-radio>
          </el-radio-group>
        </div>

        <el-divider />

        <el-button class="reset-btn" @click="handleReset">{{ $t('common.reset') }}</el-button>

        <!-- 对比功能入口 -->
        <div class="compare-section" v-if="compareList.length > 0">
          <el-badge :value="compareList.length" class="compare-badge">
            <el-button type="primary" plain class="compare-btn" @click="showCompareDrawer = true">
              <el-icon>
                <Operation />
              </el-icon>
              {{ $t('product.compare') }}
            </el-button>
          </el-badge>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 顶部工具栏 -->
        <div class="toolbar">
          <div class="toolbar-left">
            <span class="result-count">{{ $t('product.found') }} <strong>{{ total }}</strong> {{ $t('product.items')
            }}</span>
            <span v-if="filters.keyword" class="search-keyword">
              {{ $t('common.search') }}: <el-tag closable @close="clearKeyword">{{ filters.keyword }}</el-tag>
            </span>
          </div>
          <div class="toolbar-right">
            <el-select v-model="filters.sortBy" :placeholder="$t('product.sortBy')" size="default"
              @change="handleFilterChange">
              <el-option :label="$t('product.sortDefault')" value="default" />
              <el-option :label="$t('product.sortLatest')" value="createTime" />
              <el-option :label="$t('product.sortPriceAsc')" value="priceAsc" />
              <el-option :label="$t('product.sortPriceDesc')" value="priceDesc" />
              <el-option :label="$t('product.sortViews')" value="viewCount" />
            </el-select>
            <el-button class="lg:hidden ml-2" size="default" @click="showFilterDrawer = true">
              {{ $t('common.filter') }}
            </el-button>
          </div>
        </div>

        <!-- 主体区域：商品列表 + 推荐侧边栏 -->
        <div class="main-content">
          <el-row :gutter="15" class="full-height-row">
            <!-- 商品列表 70% -->
            <el-col :xs="24" :sm="24" :md="17" :lg="17" class="full-height-col">
              <div v-loading="loading" class="product-list">
                <el-row :gutter="12" v-if="products.length > 0">
                  <el-col v-for="product in products" :key="product.id" :xs="24" :sm="12" :md="8" :lg="6">
                    <div class="product-card">
                      <!-- 商品图片预览Popover -->
                      <el-popover placement="right" :width="300" trigger="hover" popper-class="product-preview-popover">
                        <template #reference>
                          <div class="product-image-wrapper" @click="handleProductClick(product.id)">
                            <img :src="getProductImage(product.images)" :alt="product.title" class="product-image"
                              @error="handleImgError" />
                            <div class="product-badge" v-if="product.aiAnalyzed">
                              <el-tag size="small" type="success">{{ $t('product.aiAnalyzed') }}</el-tag>
                            </div>
                            <div class="product-actions">
                              <el-button circle size="small" :type="isInCompare(product.id) ? 'primary' : 'default'"
                                @click.stop="toggleCompare(product)">
                                <el-icon>
                                  <Operation />
                                </el-icon>
                              </el-button>
                              <el-button circle size="small" @click.stop="handleFavorite(product.id)">
                                <el-icon>
                                  <Star />
                                </el-icon>
                              </el-button>
                            </div>
                          </div>
                        </template>
                        <!-- 快速预览内容 -->
                        <div class="quick-preview">
                          <div class="preview-image">
                            <el-image :src="getProductImage(product.images)" fit="cover">
                              <template #error>
                                <img :src="DEFAULT_IMAGE" alt="fallback" class="w-full h-full object-cover" />
                              </template>
                            </el-image>
                          </div>
                          <div class="preview-title">{{ product.title }}</div>
                          <div class="preview-price">{{ $t('common.currency') }}{{ product.price }}</div>
                          <div class="preview-meta">
                            <span class="preview-meta-item">
                              <el-icon>
                                <User />
                              </el-icon>
                              {{ product.userNickname || $t('common.anonymous') }}
                            </span>
                            <span class="preview-meta-item">
                              <el-icon>
                                <View />
                              </el-icon>
                              {{ product.viewCount }}
                            </span>
                          </div>
                          <div class="preview-desc">{{ product.description?.substring(0, 60) }}...</div>
                          <el-button type="primary" size="small" class="preview-btn"
                            @click="handleProductClick(product.id)">
                            {{ $t('product.viewDetail') }}
                          </el-button>
                        </div>
                      </el-popover>

                      <div class="product-info" @click="handleProductClick(product.id)">
                        <div class="product-title"
                          v-html="highlightKeyword(locale === 'ko' && product.titleKo ? product.titleKo : product.title)">
                        </div>
                        <div class="product-price-row">
                          <span class="product-price">{{ $t('common.currency') }}{{ product.price }}</span>
                          <span class="product-original-price" v-if="product.originalPrice">
                            {{ $t('common.currency') }}{{ product.originalPrice }}
                          </span>
                        </div>
                        <div class="product-meta">
                          <div class="product-condition">
                            <span class="meta-label">{{ $t('product.condition') }}</span>
                            <el-progress :percentage="product.conditionScore * 10" :show-text="false" :stroke-width="4"
                              color="#409eff" />
                          </div>
                          <div class="product-views">
                            <el-icon>
                              <View />
                            </el-icon>
                            <span>{{ product.viewCount }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </el-col>
                </el-row>

                <!-- 空状态 -->
                <div v-else class="py-10">
                  <el-empty :description="emptyDescription" :image-size="200" />
                  <div v-if="fallbackProducts.length" class="mt-6">
                    <div class="flex items-center justify-between mb-3">
                      <div class="text-sm font-semibold text-gray-800">
                        {{ $t('product.fallbackRecommendTitle') }}
                      </div>
                      <el-button text type="primary" @click="handleClearSchoolFilter">
                        {{ $t('product.viewAllSchools') }}
                      </el-button>
                    </div>
                    <el-row :gutter="12">
                      <el-col v-for="p in fallbackProducts" :key="p.id" :xs="24" :sm="12" :md="8" :lg="6">
                        <div class="product-card">
                          <div class="product-image-wrapper" @click="handleProductClick(p.id)">
                            <img :src="getProductImage(p.images)" :alt="p.title" class="product-image"
                              @error="handleImgError" />
                          </div>
                          <div class="product-info" @click="handleProductClick(p.id)">
                            <div class="product-title">
                              {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
                            </div>
                            <div class="product-price-row">
                              <span class="product-price">{{ $t('common.currency') }}{{ p.price }}</span>
                            </div>
                          </div>
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </div>

                <!-- ✅ 自定义纯中文分页条（核心修改部分） -->
                <div class="pagination" v-if="total > 0">
                  <div class="custom-pagination">
                    <!-- 总数 -->
                    <span class="pagination-total">{{ $t('pagination.total') }} {{ total }} {{ $t('pagination.items')
                    }}</span>

                    <!-- 每页条数选择 -->
                    <el-select v-model="pageSize" @change="handleSizeChange" class="page-size-select">
                      <el-option :label="`12${$t('pagination.perPage')}`" :value="12" />
                      <el-option :label="`24${$t('pagination.perPage')}`" :value="24" />
                      <el-option :label="`48${$t('pagination.perPage')}`" :value="48" />
                    </el-select>

                    <!-- 上一页 -->
                    <el-button :disabled="pageNum === 1" @click="prevPage">{{ $t('pagination.prev') }}</el-button>

                    <!-- 页码组件 -->
                    <el-pagination v-model:current-page="pageNum" :total="total" :page-size="pageSize" layout="pager"
                      @current-change="handlePageChange" />

                    <!-- 下一页 -->
                    <el-button :disabled="pageNum === totalPages" @click="nextPage">{{ $t('pagination.next')
                    }}</el-button>

                    <!-- 跳转 -->
                    <span class="pagination-jumper">{{ $t('pagination.jumpTo') }}</span>
                    <el-input v-model="jumpPage" @keyup.enter="handleJump" class="jump-input" />
                    <span class="pagination-jumper">{{ $t('pagination.pageSuffix') }}</span>
                  </div>
                </div>
              </div>
            </el-col>

            <!-- 推荐侧边栏 30% -->
            <el-col :xs="24" :sm="24" :md="7" :lg="7" class="full-height-col">
              <div class="recommend-sidebar">
                <div class="sidebar-section">
                  <div class="sidebar-title">
                    <el-icon color="#ff9800">
                      <Star />
                    </el-icon>
                    <span>{{ $t('product.hotRecommend') }}</span>
                  </div>
                  <div class="recommend-list">
                    <div v-for="(item, index) in recommendProducts.slice(0, 6)" :key="item.id" class="recommend-item"
                      @click="handleProductClick(item.id)">
                      <div class="recommend-rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
                      <el-image :src="getProductImage(item.images)" fit="cover" class="recommend-image" />
                      <div class="recommend-info">
                        <div class="recommend-title">{{ locale === 'ko' && item.titleKo ? item.titleKo : item.title }}
                        </div>
                        <div class="recommend-price">{{ $t('common.currency') }}{{ item.price }}</div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="sidebar-section">
                  <div class="sidebar-title">
                    <el-icon color="#67c23a">
                      <TrendCharts />
                    </el-icon>
                    <span>{{ $t('product.latestPublish') }}</span>
                  </div>
                  <div class="latest-list">
                    <div v-for="item in latestProducts.slice(0, 4)" :key="item.id" class="latest-item"
                      @click="handleProductClick(item.id)">
                      <el-image :src="getProductImage(item.images)" fit="cover" class="latest-image" />
                      <div class="latest-info">
                        <div class="latest-title">{{ locale === 'ko' && item.titleKo ? item.titleKo : item.title }}
                        </div>
                        <div class="latest-meta">
                          <span class="latest-price">{{ $t('common.currency') }}{{ item.price }}</span>
                          <span class="latest-time">{{ $t('product.justNow') }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>

    <!-- 商品对比抽屉 -->
    <el-drawer v-model="showCompareDrawer" :title="$t('product.compareProducts')" direction="rtl" size="60%">
      <div class="compare-content" v-if="compareList.length > 0">
        <el-alert v-if="compareList.length < 2" :title="$t('product.atLeastTwoProducts')" type="warning"
          :closable="false" style="margin-bottom: 20px;" />
        <el-row :gutter="15">
          <el-col :span="8" v-for="product in compareList" :key="product.id">
            <el-card class="compare-card">
              <template #header>
                <div class="compare-card-header">
                  <span>{{ product.title }}</span>
                  <el-button type="danger" text size="small" @click="removeFromCompare(product.id)">
                    <el-icon>
                      <Close />
                    </el-icon>
                  </el-button>
                </div>
              </template>
              <div class="compare-card-body">
                <el-image :src="getProductImage(product.images)" fit="cover" class="compare-image" />
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.price') }}</span>
                  <span class="compare-value price">{{ $t('common.currency') }}{{ product.price }}</span>
                </div>
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.originalPrice') }}</span>
                  <span class="compare-value">{{ product.originalPrice ? $t('common.currency') + product.originalPrice :
                    $t('common.none') }}</span>
                </div>
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.condition') }}</span>
                  <span class="compare-value">{{ product.conditionScore }}/{{ $t('product.fullScore') }}</span>
                </div>
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.viewCount') }}</span>
                  <span class="compare-value">{{ product.viewCount }}</span>
                </div>
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.favoriteCount') }}</span>
                  <span class="compare-value">{{ product.favoriteCount }}</span>
                </div>
                <div class="compare-item">
                  <span class="compare-label">{{ $t('product.category') }}</span>
                  <span class="compare-value">{{ product.category ? $t(`categories.${product.category}`) :
                    $t('categories.others') }}</span>
                </div>
                <el-button type="primary" class="compare-view-btn" @click="handleProductClick(product.id)">
                  {{ $t('product.viewDetail') }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <el-empty v-else :description="$t('product.noCompareProducts')" />
    </el-drawer>

    <el-drawer v-model="showFilterDrawer" direction="btt" size="80%" :title="$t('common.filter')" class="lg:hidden">
      <div class="filter-section">
        <div class="filter-title">{{ $t('product.category') }}</div>
        <el-radio-group v-model="filters.category" class="filter-radios" @change="handleFilterChange">
          <el-radio label="">{{ $t('common.all') }}</el-radio>
          <el-radio v-for="cat in categories" :key="cat.id" :label="cat.id">
            {{ $t(`categories.${cat.id}`) }}
          </el-radio>
        </el-radio-group>
      </div>

      <el-divider />

      <div class="filter-section">
        <div class="filter-title">{{ $t('product.priceRange') }}</div>
        <div class="price-slider-wrapper">
          <el-slider v-model="priceRange" range :min="0" :max="10000" :step="100" :format-tooltip="formatPrice"
            @change="handlePriceChange" />
          <div class="price-range-display">
            <span class="price-text">{{ $t('common.currency') }}{{ priceRange[0] }}</span>
            <span class="price-divider">-</span>
            <span class="price-text">{{ $t('common.currency') }}{{ priceRange[1] }}</span>
          </div>
        </div>
      </div>

      <el-divider />

      <div class="filter-section">
        <div class="filter-title">{{ $t('product.conditionFilter') }}</div>
        <el-checkbox-group v-model="filters.conditionScores" class="filter-checkboxes" @change="handleFilterChange">
          <el-checkbox label="9-10">{{ $t('product.above90') }}</el-checkbox>
          <el-checkbox label="7-9">{{ $t('product.excellent') }}</el-checkbox>
          <el-checkbox label="0-7">{{ $t('product.below70') }}</el-checkbox>
        </el-checkbox-group>
      </div>

      <el-divider />

      <div class="filter-section">
        <div class="filter-title">{{ $t('product.aiRecommend') }}</div>
        <el-radio-group v-model="filters.aiFilter" class="filter-radios" @change="handleFilterChange">
          <el-radio label="">{{ $t('product.allProducts') }}</el-radio>
          <el-radio label="aiOnly">{{ $t('product.aiOnly') }}</el-radio>
          <el-radio label="aiPriority">{{ $t('product.aiPriority') }}</el-radio>
        </el-radio-group>
      </div>

      <el-divider />

      <el-button class="reset-btn" @click="handleReset">{{ $t('common.reset') }}</el-button>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { productApi } from '@/api/product'
import { favoriteApi } from '@/api/favorite'
import { ElMessage } from 'element-plus'
import { formatImageUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'
import { Star, View, Operation, TrendCharts, User, Close } from '@element-plus/icons-vue'
import { useSchoolStore } from '@/stores/school'
import { CATEGORIES, categoryIdToDbValue, normalizeCategory } from '@/utils/categories'

const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()
const schoolStore = useSchoolStore()

const loading = ref(false)
const products = ref([])
const fallbackProducts = ref([])
const recommendProducts = ref([])
const latestProducts = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const priceRange = ref([0, 10000])
const showCompareDrawer = ref(false)
const showFilterDrawer = ref(false)
const compareList = ref([])

// ✅ 新增分页相关变量
const jumpPage = ref('')
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const categories = CATEGORIES

const filters = ref({
  keyword: route.query.keyword || '',
  category: normalizeCategory(route.query.category || ''),
  minPrice: route.query.minPrice || null,
  maxPrice: route.query.maxPrice || null,
  conditionScores: [],
  sortBy: route.query.sort || 'default',
  aiFilter: ''
})

const DEFAULT_IMAGE = 'https://via.placeholder.com/300x200?text=No+Image'

const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const imageArray = typeof images === 'string' ? JSON.parse(images) : images
    const firstImage = imageArray[0]
    if (!firstImage) return DEFAULT_IMAGE

    // 使用formatImageUrl处理图片路径
    return formatImageUrl(firstImage)
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
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

const highlightKeyword = (text) => {
  if (!filters.value.keyword) return text
  const keyword = filters.value.keyword
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<span style="color: #409eff; font-weight: 600;">$1</span>')
}

const formatPrice = (value) => {
  return `${t('common.currency')}${value}`
}

const isInCompare = (productId) => {
  return compareList.value.some(p => p.id === productId)
}

const toggleCompare = (product) => {
  const index = compareList.value.findIndex(p => p.id === product.id)
  if (index > -1) {
    compareList.value.splice(index, 1)
    ElMessage.info(t('product.removedFromCompare'))
  } else {
    if (compareList.value.length >= 3) {
      ElMessage.warning(t('product.maxThreeProducts'))
      return
    }
    compareList.value.push(product)
    ElMessage.success(t('product.addedToCompare'))
  }
}

const removeFromCompare = (productId) => {
  const index = compareList.value.findIndex(p => p.id === productId)
  if (index > -1) {
    compareList.value.splice(index, 1)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 2
    }

    if (schoolStore.selectedSchool) {
      params.schoolId = Number(schoolStore.selectedSchool)
    }

    if (filters.value.keyword) {
      params.keyword = filters.value.keyword
    }
    if (filters.value.category) {
      params.category = categoryIdToDbValue(filters.value.category)
    }
    if (priceRange.value[0] > 0) {
      params.minPrice = priceRange.value[0]
    }
    if (priceRange.value[1] < 10000) {
      params.maxPrice = priceRange.value[1]
    }

    if (filters.value.conditionScores.length > 0) {
      let minScore = 0
      let maxScore = 10
      filters.value.conditionScores.forEach(range => {
        const [min, max] = range.split('-').map(Number)
        if (min < minScore || minScore === 0) minScore = min
        if (max > maxScore || maxScore === 10) maxScore = max
      })
      params.minCondition = minScore
      params.maxCondition = maxScore
    }

    if (filters.value.sortBy && filters.value.sortBy !== 'default') {
      params.sortBy = filters.value.sortBy
    }

    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      let productList = res.data.records || []

      if (filters.value.aiFilter === 'aiOnly') {
        productList = productList.filter(p => p.aiAnalyzed === 1)
      } else if (filters.value.aiFilter === 'aiPriority') {
        productList = productList.sort((a, b) => {
          if (a.aiAnalyzed === 1 && b.aiAnalyzed !== 1) return -1
          if (a.aiAnalyzed !== 1 && b.aiAnalyzed === 1) return 1
          return 0
        })
      }

      products.value = productList
      total.value = res.data.total || productList.length
      if (products.value.length === 0 && schoolStore.selectedSchool) {
        await fetchFallbackProducts()
      } else {
        fallbackProducts.value = []
      }
    }
  } catch (error) {
    console.error(t('product.fetchFailed'), error)
    ElMessage.error(t('product.fetchFailed'))
  } finally {
    loading.value = false
  }
}

const fetchRecommendProducts = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 10,
      status: 2
    }
    if (schoolStore.selectedSchool) {
      params.schoolId = Number(schoolStore.selectedSchool)
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      recommendProducts.value = res.data.records.sort((a, b) => b.viewCount - a.viewCount)
    }
  } catch (error) {
    console.error(t('product.fetchRecommendFailed'), error)
  }
}

const fetchLatestProducts = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 6,
      status: 2,
      sortBy: 'createTime'
    }
    if (schoolStore.selectedSchool) {
      params.schoolId = Number(schoolStore.selectedSchool)
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200') {
      latestProducts.value = res.data.records || []
    }
  } catch (error) {
    console.error(t('product.fetchLatestFailed'), error)
  }
}

const handlePriceChange = () => {
  handleFilterChange()
}

const handleFilterChange = () => {
  pageNum.value = 1
  fetchProducts()
}

const handleReset = () => {
  filters.value = {
    keyword: '',
    category: '',
    minPrice: null,
    maxPrice: null,
    conditionScores: [],
    sortBy: 'default'
  }
  priceRange.value = [0, 10000]
  pageNum.value = 1
  router.push({ path: '/user/products', query: {} })
  fetchProducts()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchProducts()
}

const handleProductClick = (productId) => {
  router.push(`/user/product/${productId}`)
}

const handleFavorite = async (productId) => {
  try {
    const res = await favoriteApi.addFavorite(productId)
    if (res.code === '200') {
      ElMessage.success(t('product.favoriteSuccess'))
    }
  } catch (error) {
    ElMessage.error(error.message || t('product.favoriteFailed'))
  }
}

const clearKeyword = () => {
  filters.value.keyword = ''
  handleFilterChange()
}

// ✅ 分页按钮事件
const prevPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    handlePageChange(pageNum.value)
  }
}

const nextPage = () => {
  if (pageNum.value < totalPages.value) {
    pageNum.value++
    handlePageChange(pageNum.value)
  }
}

// ✅ 分页跳转事件
const handleJump = () => {
  const page = parseInt(jumpPage.value)
  if (!page || page < 1 || page > totalPages.value) {
    ElMessage.warning(t('pagination.invalidPage'))
    return
  }
  pageNum.value = page
  handlePageChange(page)
  jumpPage.value = ''
}

watch(() => route.query, (newQuery) => {
  filters.value.keyword = newQuery.keyword || ''
  filters.value.category = normalizeCategory(newQuery.category || '')
  filters.value.sortBy = newQuery.sort || 'default'
  pageNum.value = 1
  fetchProducts()
}, { deep: true })

watch(() => schoolStore.selectedSchool, () => {
  pageNum.value = 1
  fetchProducts()
  fetchRecommendProducts()
  fetchLatestProducts()
})

onMounted(() => {
  fetchProducts()
  fetchRecommendProducts()
  fetchLatestProducts()
})
</script>

<style scoped>
.product-list-page {
  min-height: calc(100vh - 110px);
  background: #f0f2f5;
  padding: 15px 0;
}

.list-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 15px;
  display: flex;
  gap: 15px;
  align-items: stretch;
}

/* 左侧筛选面板 */
.filter-panel {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 10px;
  padding: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(235, 238, 245, 0.8);
  display: flex;
  flex-direction: column;
}

.filter-section {
  margin-bottom: 12px;
}

.filter-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
}

/* 关键：分类文字左对齐 */
.filter-radios,
.filter-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 10px;
  text-align: left;
}

.filter-radios :deep(.el-radio),
.filter-checkboxes :deep(.el-checkbox) {
  margin: 0;
  height: auto;
  width: 100%;
  justify-content: flex-start;
}

.filter-radios :deep(.el-radio__label),
.filter-checkboxes :deep(.el-checkbox__label) {
  text-align: left;
  padding-left: 8px;
}

.price-slider-wrapper {
  padding: 0 8px;
}

.price-range-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
}

.price-divider {
  color: #909399;
}

.reset-btn {
  width: 100%;
  margin-top: 8px;
}

.compare-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.compare-badge {
  width: 100%;
}

.compare-btn {
  width: 100%;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 工具栏 */
.toolbar {
  background: #fff;
  border-radius: 10px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.result-count {
  font-size: 14px;
  color: #606266;
}

.result-count strong {
  color: #409eff;
  font-size: 18px;
}

.search-keyword {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 主体内容区：强制和左侧同高 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.full-height-row {
  height: 100%;
  display: flex;
}

.full-height-col {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 商品列表 */
.product-list {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
  margin-bottom: 12px;
}

.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  background: #f5f7fa;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

.product-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 2;
  align-items: center;
  /* 水平居中对齐 */
}

/* 再加这个：强制两个按钮尺寸完全一致 */
.product-actions .el-button {
  width: 28px;
  /* 固定宽度 */
  height: 28px;
  /* 固定高度 */
  margin: 0;
  padding: 0;
  /* 清除内边距 */
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.product-card:hover .product-actions {
  opacity: 1;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 14px;
  color: #2c3e50;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 40px;
  line-height: 20px;
}

.product-price-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.product-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 600;
}

.product-original-price {
  font-size: 12px;
  color: #909399;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.product-condition {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-label {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
}

.product-views {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

/* 快速预览Popover */
:deep(.product-preview-popover) {
  padding: 12px !important;
}

.quick-preview {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-image {
  width: 100%;
  height: 200px;
  border-radius: 6px;
  overflow: hidden;
}

.preview-image .el-image {
  width: 100%;
  height: 100%;
}

.preview-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
}

.preview-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: 700;
}

.preview-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
}

.preview-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.preview-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.preview-btn {
  width: 100%;
}

/* 推荐侧边栏 */
.recommend-sidebar {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 100%;
}

.sidebar-section {
  margin-bottom: 30px;
}

.sidebar-section:last-child {
  margin-bottom: 0;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f2f5;
}

.recommend-list,
.latest-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.recommend-item:hover {
  background: #f0f7ff;
}

.recommend-rank {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #f0f2f5;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.recommend-rank.top-three {
  background: linear-gradient(135deg, #ff9800 0%, #ffc107 100%);
  color: #fff;
}

.recommend-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  flex-shrink: 0;
}

.recommend-info {
  flex: 1;
  min-width: 0;
}

.recommend-title {
  font-size: 13px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

.latest-item {
  display: flex;
  gap: 10px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.latest-item:hover {
  background: #f0f7ff;
}

.latest-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  flex-shrink: 0;
}

.latest-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.latest-title {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.latest-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.latest-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

.latest-time {
  font-size: 11px;
  color: #67c23a;
}

/* 商品对比抽屉 */
.compare-content {
  padding: 10px 0;
}

.compare-card {
  margin-bottom: 16px;
}

.compare-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
}

.compare-card-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.compare-image {
  width: 100%;
  height: 200px;
  border-radius: 6px;
}

.compare-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.compare-label {
  font-size: 13px;
  color: #606266;
}

.compare-value {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}

.compare-value.price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
}

.compare-view-btn {
  width: 100%;
  margin-top: 6px;
}

/* ✅ 自定义分页样式 */
.pagination {
  margin-top: auto;
  padding-top: 16px;
}

.custom-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination-total,
.pagination-jumper {
  font-size: 14px;
  color: #606266;
}

.page-size-select {
  width: 110px;
}

.jump-input {
  width: 60px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .list-container {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-panel {
    width: 100%;
  }

  .filter-panel :deep(.el-divider) {
    display: none;
  }

  .filter-radios,
  .filter-checkboxes {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px 12px;
  }

  .filter-radios :deep(.el-radio),
  .filter-checkboxes :deep(.el-checkbox) {
    margin: 0;
  }
}

@media (max-width: 768px) {
  .list-container {
    padding: 0 12px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-right {
    width: 100%;
  }

  .toolbar-right .el-select {
    width: 100%;
  }

  .product-list {
    padding: 12px;
  }

  .product-info {
    padding: 10px;
  }

  .product-actions {
    opacity: 1;
  }

  .custom-pagination {
    gap: 6px;
  }

  .page-size-select {
    width: 90px;
  }

  .jump-input {
    width: 50px;
  }
}
</style>
