<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 渐变背景头部 -->
    <div class="bg-gradient-to-br from-primary to-primaryDark pt-8 pb-16 px-6 rounded-b-[3rem] shadow-lg relative overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>

      <div class="relative z-10 flex items-center h-10 mb-3">
        <button @click="router.back()" class="absolute left-0 p-1 rounded-full hover:bg-white/10 active:scale-90 transition-all text-white">
          <ChevronLeftIcon class="w-7 h-7" />
        </button>
        <h1 class="w-full text-center text-lg font-bold text-white tracking-wide">{{ $t('nav.myProducts') }}</h1>
      </div>

      <div class="relative z-10 flex items-center justify-between mb-3">
        <div class="text-xs text-white/70">{{ total }} {{ totalLabel }}</div>
        <button class="px-4 py-1.5 bg-white/20 backdrop-blur-md text-white text-sm font-medium rounded-full border border-white/20 active:scale-95 transition-transform" @click="router.push('/user/publish')">
          {{ $t('common.postProduct') }}
        </button>
      </div>

      <div class="status-segment relative z-10">
        <button
          v-for="s in statusTabs"
          :key="String(s.value)"
          class="status-segment-item"
          :class="{ active: statusFilter === s.value }"
          @click="setStatus(s.value)"
        >
          {{ s.label }}
        </button>
      </div>
    </div>

    <div class="px-4 -mt-8 relative z-20 space-y-3">
      <div v-if="initialLoading && products.length === 0" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
        {{ $t('common.loading') }}
      </div>

      <van-list
        v-else
        v-model:loading="loading"
        :finished="finished"
        :finished-text="products.length > 0 ? finishedText : ''"
        :immediate-check="false"
        class="space-y-3"
        @load="loadMore"
      >
        <div
          v-for="p in products"
          :key="p.id"
          class="bg-white rounded-2xl p-3 shadow-sm border border-gray-100 flex space-x-3 active:scale-[0.99] transition-transform duration-200"
          @click="router.push(`/user/product/${p.id}`)"
        >
          <div class="w-28 h-28 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 relative">
            <img
              :src="getProductImage(p.images)"
              class="h-full w-full object-cover"
              loading="lazy"
              decoding="async"
              @error="handleImgError"
            />
          <div v-if="p.status !== 2" class="absolute inset-0 bg-black/50 flex items-center justify-center">
            <span class="text-white text-xs font-bold border border-white px-2 py-1 rounded-full">
              {{ statusText(p.status) }}
            </span>
          </div>
          </div>

          <div class="flex-1 min-w-0 flex flex-col justify-between py-1">
            <div>
              <div class="flex justify-between items-start">
                <h3 class="text-sm font-bold text-gray-900 line-clamp-2 flex-1 mr-2 leading-snug">
                  <span
                    class="inline-block text-[10px] px-1.5 py-0.5 rounded mr-1 align-middle"
                    :class="statusBadgeClass(p.status)"
                  >
                    {{ statusText(p.status) }}
                  </span>
                  {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
                </h3>
              </div>
              <div class="mt-2 flex items-baseline space-x-2">
                <span class="text-red-500 font-bold text-lg">{{ $t('common.currency') }}{{ p.price }}</span>
                <span v-if="p.originalPrice" class="text-gray-400 text-xs line-through">
                  {{ $t('common.currency') }}{{ p.originalPrice }}
                </span>
              </div>
              <div class="flex items-center text-xs text-gray-400 mt-1">
                <ClockIcon class="w-3 h-3 mr-1" />
                {{ formatTime(p.createTime) }}
                <span class="mx-1">·</span>
                {{ $t('product.viewCount') }} {{ p.viewCount || 0 }}
                <span class="mx-1">·</span>
                {{ $t('profile.favoriteCount') }} {{ p.favoriteCount || 0 }}
              </div>
            </div>

            <div class="flex justify-end flex-wrap gap-2 mt-2">
              <button
                v-if="p.status === 2"
                class="flex items-center text-xs font-medium text-orange-600 bg-orange-50 px-3 py-1.5 rounded-full active:bg-orange-100 transition-colors"
                @click.stop="toggleStatus(p, 3)"
              >
                <ArchiveBoxIcon class="w-3.5 h-3.5 mr-1" />
                {{ $t('common.unlist') }}
              </button>
              <button
                v-else-if="p.status === 3"
                class="flex items-center text-xs font-medium text-teal-600 bg-teal-50 px-3 py-1.5 rounded-full active:bg-teal-100 transition-colors"
                @click.stop="toggleStatus(p, 2)"
              >
                <ArrowPathIcon class="w-3.5 h-3.5 mr-1" />
                {{ $t('common.list') }}
              </button>
              <button
                class="flex items-center text-xs font-medium text-gray-700 bg-gray-100 px-3 py-1.5 rounded-full active:bg-gray-200 transition-colors"
                @click.stop="handleEdit(p.id)"
              >
                <PencilSquareIcon class="w-3.5 h-3.5 mr-1" />
                {{ $t('common.edit') }}
              </button>
              <button
                class="flex items-center text-xs font-medium text-red-600 bg-red-50 px-3 py-1.5 rounded-full active:bg-red-100 transition-colors"
                @click.stop="removeProduct(p)"
              >
                <TrashIcon class="w-3.5 h-3.5 mr-1" />
                {{ $t('common.delete') }}
              </button>
            </div>
          </div>
        </div>
      </van-list>

      <div v-if="!initialLoading && products.length === 0" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
        {{ $t('common.noData') }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { productApi } from '@/api/product'
import { formatImageUrl } from '@/utils/url'
import {
  ArchiveBoxIcon,
  ArrowPathIcon,
  ChevronLeftIcon,
  ClockIcon,
  PencilSquareIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()
const { t, locale } = useI18n()

const loading = ref(false)
const initialLoading = ref(false)
const finished = ref(false)
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)

const finishedText = computed(() => t('common.noMoreData'))

const totalLabel = computed(() => {
  if (locale.value === 'ko') return '개 상품'
  if (locale.value === 'en') return 'items'
  return '件商品'
})

const statusTabs = computed(() => [
  { value: null, label: t('common.all') },
  { value: 0, label: t('order.draft') },
  { value: 1, label: t('order.pendingReview') },
  { value: 2, label: getMobileStatusLabel(2) },
  { value: 3, label: getMobileStatusLabel(3) },
  { value: 4, label: getMobileStatusLabel(4) }
])

const getMobileStatusLabel = (status) => {
  const textMap = {
    zh: {
      2: '已上架',
      3: '已下架',
      4: '已完成'
    },
    en: {
      2: 'Listed',
      3: 'Unlisted',
      4: 'Completed'
    },
    ko: {
      2: '판매중',
      3: '내림',
      4: '완료'
    }
  }
  return textMap[locale.value]?.[status] || textMap.zh[status]
}

const statusText = (status) => {
  const textMap = {
    0: t('order.draft'),
    1: t('order.pendingReview'),
    2: getMobileStatusLabel(2),
    3: getMobileStatusLabel(3),
    4: getMobileStatusLabel(4)
  }
  return textMap[status] || t('common.unknown')
}

const statusBadgeClass = (status) => {
  const classMap = {
    0: 'bg-slate-100 text-slate-500',
    1: 'bg-yellow-50 text-yellow-600',
    2: 'bg-teal-50 text-teal-600',
    3: 'bg-gray-100 text-gray-500',
    4: 'bg-gray-100 text-gray-500'
  }
  return classMap[status] || 'bg-gray-100 text-gray-500'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) return ''
  const localeName = locale.value === 'ko' ? 'ko-KR' : locale.value === 'en' ? 'en-US' : 'zh-CN'
  return date.toLocaleDateString(localeName, { month: 'short', day: 'numeric' })
}

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const parsed = typeof images === 'string' ? JSON.parse(images) : images
    const first = Array.isArray(parsed) ? parsed[0] : ''
    return formatImageUrl(first || '')
  } catch {
    return formatImageUrl(images)
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = ''
}

const fetchProducts = async ({ reset = false } = {}) => {
  if (loading.value && !reset) return
  if (reset) {
    pageNum.value = 1
    products.value = []
    total.value = 0
    finished.value = false
    initialLoading.value = true
  }

  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await productApi.getMyProducts(params)
    if (res.code === '200' || res.success) {
      const data = res.data || {}
      const records = data.records || []
      products.value = reset ? records : [...products.value, ...records]
      total.value = data.total || 0
      pageNum.value += 1
      finished.value = records.length < pageSize.value || (total.value > 0 && products.value.length >= total.value)
    } else {
      ElMessage.error(res.message || t('common.fetchFailed'))
      finished.value = true
    }
  } catch (e) {
    ElMessage.error(e?.message || t('common.fetchFailed'))
    finished.value = true
  } finally {
    loading.value = false
    initialLoading.value = false
  }
}

const loadMore = () => {
  fetchProducts()
}

const handleEdit = (id) => {
  router.push({
    path: '/user/publish',
    query: { id }
  })
}

const setStatus = (value) => {
  statusFilter.value = value
  fetchProducts({ reset: true })
}

const toggleStatus = async (row, newStatus) => {
  const actionText = newStatus === 2 ? t('common.list') : t('common.unlist')
  try {
    await ElMessageBox.confirm(t('myProducts.confirmToggle', { action: actionText }), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'info'
    })
    const res = await productApi.updateProductStatus(row.id, newStatus)
    if (res.code === '200' || res.success) {
      ElMessage.success(t('myProducts.toggleSuccess', { action: actionText }))
      fetchProducts({ reset: true })
    } else {
      ElMessage.error(res.message || t('myProducts.toggleFail', { action: actionText }))
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(t('myProducts.toggleFail', { action: actionText }))
  }
}

const removeProduct = async (row) => {
  try {
    await ElMessageBox.confirm(t('myProducts.deleteConfirm'), t('myProducts.deleteTitle'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    const res = await productApi.deleteProduct(row.id)
    if (res.code === '200' || res.success) {
      ElMessage.success(t('myProducts.deleteSuccess'))
      fetchProducts({ reset: true })
    } else {
      ElMessage.error(res.message || t('myProducts.deleteFail'))
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(t('myProducts.deleteFail'))
  }
}

onMounted(() => {
  fetchProducts({ reset: true })
})
</script>

<style scoped>
.top-filter-panel {
  border: 1px solid #eef2f7;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  padding: 16px;
}

.publish-btn {
  flex-shrink: 0;
  border: none;
  border-radius: 999px;
  background: linear-gradient(135deg, #14b8a6 0%, #06b6d4 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  line-height: 1;
  padding: 11px 15px;
  box-shadow: 0 8px 18px rgba(20, 184, 166, 0.24);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.publish-btn:active {
  transform: scale(0.97);
  box-shadow: 0 5px 12px rgba(20, 184, 166, 0.18);
}

.status-segment {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  padding: 6px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  scrollbar-width: none;
  -webkit-overflow-scrolling: touch;
}

.status-segment::-webkit-scrollbar {
  display: none;
}

.status-segment-item {
  min-width: 74px;
  flex: 1 0 auto;
  border: none;
  border-radius: 15px;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  font-size: 15px;
  font-weight: 600;
  line-height: 1;
  padding: 13px 16px;
  white-space: nowrap;
  transition: background 0.18s ease, color 0.18s ease, box-shadow 0.18s ease;
}

.status-segment-item.active {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.12);
}
</style>
