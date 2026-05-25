<template>
  <div class="product-list-container">
    <template v-if="isMobileScreen">
      <div class="product-list-mobile bg-gray-50 min-h-screen pb-24">
        <!-- 搜索栏 -->
        <div class="sticky top-0 z-50 bg-white px-4 py-2 border-b border-gray-100 flex items-center gap-2">
          <el-icon :size="20" class="text-gray-600" @click="router.back()"><ArrowLeft /></el-icon>
          <div class="flex-1 bg-gray-100 rounded-full px-4 py-1.5 flex items-center gap-2">
            <el-icon class="text-gray-400"><Search /></el-icon>
            <input v-model="searchKeyword" class="bg-transparent border-none outline-none text-sm w-full" :placeholder="$t('common.search')" @keyup.enter="fetchProducts" />
          </div>
          <LangSwitcher />
        </div>

        <!-- 筛选栏 -->
        <div class="sticky top-[49px] z-40 bg-white px-4 py-2 border-b border-gray-50 flex gap-4 overflow-x-auto text-sm text-gray-500 whitespace-nowrap scrollbar-hide">
          <div v-for="sort in sortOptions" :key="sort.value" :class="['flex items-center gap-1', activeSort === sort.value ? 'text-primary font-bold' : '']" @click="activeSort = sort.value">
            {{ sort.label }}
          </div>
        </div>

        <!-- 商品列表 -->
        <div v-loading="loading" class="p-3 grid grid-cols-2 gap-3">
          <div v-for="p in products" :key="p.id" class="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-100" @click="router.push(`/user/product/${p.id}`)">
            <div class="aspect-square bg-gray-50 relative">
              <img :src="getProductImage(p.images)" class="w-full h-full object-cover" />
              <div v-if="p.aiAnalyzed" class="absolute top-2 left-2 bg-black/40 backdrop-blur text-white text-[9px] px-1.5 py-0.5 rounded-full">AI评估</div>
              <div v-if="p.conditionScore" class="absolute top-2 right-2 bg-white/85 backdrop-blur text-gray-800 text-[9px] px-1.5 py-0.5 rounded-full border border-gray-100">成色 {{ p.conditionScore }}</div>
              <div v-if="bargainLabel(p)" class="absolute bottom-2 left-2 bg-primary/90 text-white text-[9px] px-1.5 py-0.5 rounded-full">{{ bargainLabel(p) }}</div>
            </div>
            <div class="p-3">
              <h3 class="text-xs font-bold text-gray-900 line-clamp-2 h-8">{{ p.title }}</h3>
              <div class="mt-2 flex items-end justify-between">
                <div class="text-sm font-black text-primary">¥{{ p.price }}</div>
                <div class="text-[9px] text-gray-400">浏览 {{ p.viewCount || 0 }}</div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-if="products.length === 0 && !loading" description="暂无商品" />
      </div>
    </template>

    <template v-else>
      <div class="product-list-page p-6 max-w-7xl mx-auto pb-24">
        <div class="flex items-center gap-6 mb-8">
          <h1 class="text-2xl font-bold">全部商品</h1>
          <div class="flex-1 max-w-md">
            <el-input v-model="searchKeyword" placeholder="搜索你想要的商品..." clearable @keyup.enter="fetchProducts">
              <template #append>
                <el-button :icon="Search" @click="fetchProducts" />
              </template>
            </el-input>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
          <!-- 侧边筛选 -->
          <div class="lg:col-span-1 space-y-6">
            <el-card shadow="never">
              <template #header><div class="font-bold">筛选</div></template>
              <div class="space-y-4">
                <div>
                  <div class="text-sm text-gray-500 mb-2">价格区间</div>
                  <div class="flex items-center gap-2">
                    <el-input-number v-model="priceRange.min" :min="0" controls-position="right" placeholder="最小" class="!w-full" />
                    <span class="text-gray-300">-</span>
                    <el-input-number v-model="priceRange.max" :min="0" controls-position="right" placeholder="最大" class="!w-full" />
                  </div>
                </div>
                <el-button type="primary" class="w-full" @click="fetchProducts">应用筛选</el-button>
              </div>
            </el-card>
          </div>

          <!-- 商品列表 -->
          <div class="lg:col-span-3">
            <div v-loading="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              <el-card v-for="p in products" :key="p.id" shadow="hover" class="cursor-pointer" :body-style="{ padding: '0' }" @click="router.push(`/user/product/${p.id}`)">
                <div class="aspect-square bg-gray-50">
                  <img :src="getProductImage(p.images)" class="w-full h-full object-cover" />
                </div>
                <div class="p-4">
                  <h3 class="text-sm font-medium line-clamp-2 h-10">{{ p.title }}</h3>
                  <div class="mt-2 flex justify-between items-center">
                    <span class="text-lg font-bold text-primary">¥{{ p.price }}</span>
                    <span class="text-xs text-gray-400">{{ p.viewCount }} 浏览</span>
                  </div>
                </div>
              </el-card>
            </div>
            <el-empty v-if="products.length === 0 && !loading" description="暂无商品" />
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { productApi } from '@/api/product'
import { formatProductImageUrl } from '@/utils/url'
import { ArrowLeft, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { categoryIdToDbValue } from '@/utils/categories'
import { useSchoolStore } from '@/stores/school'

const router = useRouter()
const route = useRoute()
const { isMobileScreen } = useDeviceType()
const schoolStore = useSchoolStore()

const loading = ref(false)
const products = ref([])
const searchKeyword = ref(route.query.keyword || '')
const activeSort = ref('default')
const priceRange = ref({ min: null, max: null })

const sortOptions = [
  { label: '综合', value: 'default' },
  { label: '最新', value: 'newest' },
  { label: '价格↑', value: 'priceAsc' },
  { label: '价格↓', value: 'priceDesc' },
  { label: '热度', value: 'hot' }
]

const resolveSortBy = (v) => {
  if (v === 'newest') return 'createTime'
  if (v === 'hot') return 'viewCount'
  if (v === 'priceAsc') return 'priceAsc'
  if (v === 'priceDesc') return 'priceDesc'
  return ''
}

const resolveCategory = () => {
  const fromQuery = route.query.category
  if (fromQuery) return String(fromQuery)
  const categoryId = route.query.categoryId
  if (categoryId) return categoryIdToDbValue(String(categoryId))
  return ''
}

const bargainLabel = (p) => {
  const text = `${p?.conditionDesc || ''}${p?.description || ''}`
  if (text.includes('不刀')) return '不刀'
  if (text.includes('可小刀') || text.includes('可议价') || text.includes('可刀')) return '可小刀'
  return ''
}

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(images)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: 1,
      pageSize: 20,
      keyword: searchKeyword.value,
      category: resolveCategory(),
      sortBy: resolveSortBy(activeSort.value),
      minPrice: priceRange.value.min,
      maxPrice: priceRange.value.max,
      status: 2,
      schoolId: schoolStore.selectedSchool || undefined
    }
    const res = await productApi.getProductList(params)
    if (res.code === '200') products.value = res.data.records || []
  } catch (e) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchProducts())
watch(() => route.query, fetchProducts)
watch(activeSort, fetchProducts)
</script>
