<template>
  <div class="product-list-container">
    <template v-if="isMobileScreen">
      <div class="product-list-mobile bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pb-24">
        <!-- 搜索栏 -->
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur-xl px-4 py-3 border-b border-gray-100 shadow-sm flex items-center gap-3">
          <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center hover:from-gray-200 hover:to-gray-300 transition-all cursor-pointer active:scale-95" @click="router.back()">
            <el-icon :size="20" class="text-gray-700"><ArrowLeft /></el-icon>
          </div>
          <div class="flex-1 bg-gradient-to-r from-gray-100 to-gray-50 rounded-2xl px-5 py-3 flex items-center gap-3 shadow-sm">
            <el-icon :size="20" class="text-gray-400"><Search /></el-icon>
            <input v-model="searchKeyword" class="bg-transparent border-none outline-none text-base w-full" :placeholder="$t('common.search')" @keyup.enter="fetchProducts" />
          </div>
          <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-primary to-indigo-600 flex items-center justify-center shadow-lg hover:shadow-xl transition-all cursor-pointer active:scale-95" @click="fetchProducts">
            <el-icon :size="18" class="text-white"><Search /></el-icon>
          </div>
          <LangSwitcher />
        </div>

        <!-- 筛选栏 -->
        <div class="sticky top-[72px] z-40 bg-white/90 backdrop-blur-xl px-4 py-4 border-b border-gray-100 shadow-sm flex gap-3 overflow-x-auto text-base whitespace-nowrap scrollbar-hide">
          <div v-for="sort in sortOptions" :key="sort.value" 
               :class="['px-5 py-2.5 rounded-2xl font-bold transition-all duration-300 cursor-pointer', 
                        activeSort === sort.value ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']" 
               @click="activeSort = sort.value">
            {{ sort.label }}
          </div>
        </div>

        <!-- 商品列表 -->
        <div v-loading="loading" class="p-4 grid grid-cols-2 gap-4">
          <div v-for="p in products" :key="p.id" 
               class="bg-white/90 backdrop-blur-xl rounded-3xl overflow-hidden shadow-lg border border-gray-100 hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 cursor-pointer group" 
               @click="router.push(`/user/product/${p.id}`)">
            <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 relative overflow-hidden">
              <img :src="getProductImage(p.images)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
              <div v-if="p.aiAnalyzed" class="absolute top-3 left-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white text-xs px-3 py-1.5 rounded-2xl shadow-lg backdrop-blur-sm font-bold">
                <el-icon :size="12" class="mr-1"><MagicStick /></el-icon>AI评估
              </div>
              <div v-if="p.conditionScore" class="absolute top-3 right-3 bg-white/95 backdrop-blur-xl text-gray-800 text-xs px-3 py-1.5 rounded-2xl border border-gray-100 shadow-lg font-bold">
                成色 {{ p.conditionScore }}
              </div>
              <div v-if="bargainLabel(p)" class="absolute bottom-3 left-3 bg-gradient-to-r from-primary to-purple-600 text-white text-xs px-3 py-1.5 rounded-2xl shadow-lg font-bold">
                {{ bargainLabel(p) }}
              </div>
            </div>
            <div class="p-4">
              <h3 class="text-sm font-black text-gray-900 line-clamp-2 leading-tight">{{ p.title }}</h3>
              <div class="mt-3 flex items-end justify-between">
                <div class="text-xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ p.price }}</div>
                <div class="flex items-center gap-1 text-xs text-gray-500 font-medium">
                  <el-icon :size="12"><View /></el-icon>
                  {{ p.viewCount || 0 }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="products.length === 0 && !loading" class="pt-16">
          <el-empty description="暂无商品" />
        </div>
      </div>
    </template>

    <template v-else>
      <div class="product-list-page bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pt-8 pb-24">
        <div class="px-8 max-w-7xl mx-auto">
          <div class="flex items-center gap-8 mb-10">
            <h1 class="text-3xl font-black text-gray-900">全部商品</h1>
            <div class="flex-1 max-w-xl">
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-2">
                <el-input v-model="searchKeyword" placeholder="搜索你想要的商品..." clearable @keyup.enter="fetchProducts">
                  <template #append>
                    <el-button type="primary" :icon="Search" class="!rounded-r-2xl !h-12 !px-6" @click="fetchProducts" />
                  </template>
                </el-input>
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
            <!-- 侧边筛选 -->
            <div class="lg:col-span-1 space-y-6">
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-7">
                <div class="flex items-center gap-3 mb-6">
                  <div class="h-10 w-10 bg-gradient-to-br from-indigo-100 to-purple-100 rounded-2xl flex items-center justify-center">
                    <el-icon :size="18" class="text-indigo-600"><Filter /></el-icon>
                  </div>
                  <h3 class="text-xl font-black text-gray-900">筛选</h3>
                </div>
                <div class="space-y-6">
                  <div>
                    <div class="text-sm font-bold text-gray-700 mb-3">价格区间</div>
                    <div class="flex items-center gap-3">
                      <el-input-number v-model="priceRange.min" :min="0" controls-position="right" placeholder="最小" class="!w-full" />
                      <span class="text-gray-400 text-xl">-</span>
                      <el-input-number v-model="priceRange.max" :min="0" controls-position="right" placeholder="最大" class="!w-full" />
                    </div>
                  </div>
                  <el-button type="primary" class="w-full !h-12 !text-base !font-bold !rounded-2xl !shadow-lg hover:!shadow-xl transition-all" @click="fetchProducts">
                    应用筛选
                  </el-button>
                </div>
              </div>
              
              <!-- 排序选项 -->
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-7">
                <div class="flex items-center gap-3 mb-6">
                  <div class="h-10 w-10 bg-gradient-to-br from-amber-100 to-orange-100 rounded-2xl flex items-center justify-center">
                    <el-icon :size="18" class="text-amber-600"><TrendCharts /></el-icon>
                  </div>
                  <h3 class="text-xl font-black text-gray-900">排序</h3>
                </div>
                <div class="space-y-3">
                  <div v-for="sort in sortOptions" :key="sort.value" 
                       :class="['px-4 py-3 rounded-2xl font-bold transition-all duration-300 cursor-pointer flex items-center gap-2', 
                                activeSort === sort.value ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'bg-gray-100 text-gray-600 hover:bg-gray-200']" 
                       @click="activeSort = sort.value">
                    <span>{{ sort.label }}</span>
                    <el-icon v-if="activeSort === sort.value" class="ml-auto"><CircleCheck /></el-icon>
                  </div>
                </div>
              </div>
            </div>

            <!-- 商品列表 -->
            <div class="lg:col-span-3">
              <div v-loading="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                <div v-for="p in products" :key="p.id" 
                     class="bg-white/90 backdrop-blur-xl rounded-3xl overflow-hidden shadow-xl border border-gray-100 hover:shadow-2xl hover:-translate-y-3 transition-all duration-300 cursor-pointer group" 
                     @click="router.push(`/user/product/${p.id}`)">
                  <div class="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 relative overflow-hidden">
                    <img :src="getProductImage(p.images)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                    <div v-if="p.aiAnalyzed" class="absolute top-4 left-4 bg-gradient-to-r from-green-500 to-emerald-600 text-white text-sm px-4 py-2 rounded-2xl shadow-lg backdrop-blur-sm font-bold flex items-center gap-2">
                      <el-icon :size="14"><MagicStick /></el-icon>AI评估
                    </div>
                    <div v-if="p.conditionScore" class="absolute top-4 right-4 bg-white/95 backdrop-blur-xl text-gray-800 text-sm px-4 py-2 rounded-2xl border border-gray-100 shadow-lg font-bold">
                      成色 {{ p.conditionScore }}
                    </div>
                    <div v-if="bargainLabel(p)" class="absolute bottom-4 left-4 bg-gradient-to-r from-primary to-purple-600 text-white text-sm px-4 py-2 rounded-2xl shadow-lg font-bold">
                      {{ bargainLabel(p) }}
                    </div>
                  </div>
                  <div class="p-5">
                    <h3 class="text-base font-black text-gray-900 line-clamp-2 leading-snug mb-2">{{ p.title }}</h3>
                    <div class="flex items-end justify-between">
                      <div class="text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ p.price }}</div>
                      <div class="flex items-center gap-1 text-xs text-gray-500 font-medium">
                        <el-icon :size="14"><View /></el-icon>
                        {{ p.viewCount || 0 }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="products.length === 0 && !loading" class="pt-16">
                <el-empty description="暂无商品" />
              </div>
            </div>
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
import { ArrowLeft, Search, MagicStick, View, Filter, TrendCharts, CircleCheck } from '@element-plus/icons-vue'
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
