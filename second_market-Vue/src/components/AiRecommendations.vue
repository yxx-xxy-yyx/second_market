<template>
  <div class="ai-recommendations-wrapper">
    <div class="rounded-2xl bg-white border border-gray-100 shadow-md p-4">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-2">
          <div class="h-8 w-8 rounded-xl bg-gradient-to-r from-purple-500 to-blue-500 flex items-center justify-center">
            <el-icon class="text-white" :size="18"><MagicStick /></el-icon>
          </div>
          <div>
            <div class="text-sm font-semibold text-gray-900">{{ $t('ai.recommendations') }}</div>
            <div class="text-xs text-gray-500" v-if="interestTags.length > 0">
              {{ $t('ai.basedOnInterest') }}: {{ interestTags.join(', ') }}
            </div>
          </div>
        </div>
        <el-button 
          link 
          size="small" 
          @click="refreshRecommendations"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          {{ $t('ai.refresh') }}
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="space-y-4">
        <div class="h-24 bg-gray-100 rounded-xl animate-pulse"></div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="i in 4" :key="i" class="aspect-square bg-gray-100 rounded-xl animate-pulse"></div>
        </div>
      </div>

      <!-- AI说明 -->
      <div v-else-if="aiReasoning" class="mb-4 p-3 bg-gradient-to-r from-purple-50 to-blue-50 rounded-xl border border-purple-100">
        <div class="flex items-start gap-2">
          <el-icon class="text-purple-500 mt-0.5 flex-shrink-0"><InfoFilled /></el-icon>
          <div class="text-sm text-gray-700">{{ aiReasoning }}</div>
        </div>
      </div>

      <!-- 推荐商品 -->
        <div v-else-if="products.length > 0" class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <button
                v-for="p in products"
                :key="p.id"
                class="rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-100 overflow-hidden shadow-md hover:shadow-xl hover:-translate-y-1 transition-all duration-300 text-left"
                @click="handleProductClick(p)"
            >
                <div class="w-full aspect-[4/3] bg-gradient-to-br from-gray-50 to-gray-100 overflow-hidden">
                    <img 
                        :src="getProductImage(p.images)" 
                        class="h-full w-full object-cover hover:scale-110 transition-transform duration-300" 
                        @error="handleImgError"
                    />
                </div>
                <div class="p-3">
                    <div class="text-sm font-semibold text-gray-900 line-clamp-2">
                        {{ p.title }}
                    </div>
                    <div class="mt-2">
                        <div class="text-lg font-bold sm-text-primary">
                            {{ $t('common.currency') }}{{ p.price }}
                        </div>
                    </div>
                    <!-- 推荐理由 -->
                    <div v-if="p.reason" class="mt-2 text-xs text-purple-600 bg-purple-50 rounded px-2 py-1">
                        <el-icon :size="12"><TrendCharts /></el-icon>
                        {{ p.reason }}
                    </div>
                </div>
            </button>
        </div>

      <!-- 空状态 -->
      <div v-else class="text-center py-8">
        <el-icon class="text-gray-300" :size="48"><ShoppingCart /></el-icon>
        <div class="mt-3 text-sm text-gray-500">{{ $t('ai.noRecommendations') }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { MagicStick, Refresh, InfoFilled, ShoppingCart, TrendCharts } from '@element-plus/icons-vue'
import { aiRecommendApi } from '@/api/forum'
import { DEFAULT_PRODUCT_IMAGE, formatProductImageUrl } from '@/utils/url'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const { t, locale } = useI18n()

const loading = ref(false)
const products = ref([])
const interestTags = ref([])
const aiReasoning = ref('')

const fetchRecommendations = async () => {
        if (!userStore.isLoggedIn) return
        
        loading.value = true
        try {
            const res = await aiRecommendApi.getRecommendations({
                type: 'home',
                limit: 8
            })
            
            if (res.code === '200' && res.data) {
                products.value = res.data.products || []
                interestTags.value = res.data.interestTags || []
                aiReasoning.value = res.data.aiReasoning || res.data.explanation || ''
            }
        } catch (error) {
            console.error('AI推荐获取失败:', error)
        } finally {
            loading.value = false
        }
    }
    
    const refreshRecommendations = () => {
        fetchRecommendations()
    }
    
    const handleProductClick = (product) => {
        router.push(`/user/product/${product.id}`)
    }

const getProductImage = (images) => {
  if (!images) return DEFAULT_PRODUCT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return DEFAULT_PRODUCT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e.target) e.target.src = DEFAULT_PRODUCT_IMAGE
}

onMounted(() => {
  fetchRecommendations()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
