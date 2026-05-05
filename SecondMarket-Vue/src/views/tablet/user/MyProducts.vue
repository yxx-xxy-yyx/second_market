<template>
  <div class="px-4 py-5">
    <div class="mx-auto max-w-[980px] space-y-4">
      <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
        <div class="flex items-center justify-between">
          <div class="text-lg font-semibold text-gray-900">{{ $t('nav.myProducts') }}</div>
          <button class="text-sm sm-text-primary" @click="router.push('/user/publish')">{{ $t('common.postProduct') }}</button>
        </div>
        <div class="mt-3 flex gap-2 overflow-x-auto sm-tap">
          <button
            v-for="s in statusTabs"
            :key="String(s.value)"
            class="px-3 py-2 rounded-xl text-sm border whitespace-nowrap"
            :class="statusFilter === s.value ? 'sm-bg-primary-soft sm-border-primary-soft sm-text-primary' : 'border-gray-100 text-gray-700 bg-gray-50'"
            @click="setStatus(s.value)"
          >
            {{ s.label }}
          </button>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div v-if="loading" class="col-span-2 rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
          {{ $t('common.loading') }}
        </div>
        <div
          v-else
          v-for="p in products"
          :key="p.id"
          class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden"
        >
          <div class="flex gap-3 p-4">
            <div class="h-24 w-24 rounded-2xl bg-gray-100 overflow-hidden flex-none">
              <img
                :src="getProductImage(p.images)"
                class="h-full w-full object-cover"
                loading="lazy"
                decoding="async"
                @error="handleImgError"
              />
            </div>
            <div class="min-w-0 flex-1">
              <div class="text-sm font-semibold text-gray-900 line-clamp-2" @click="router.push(`/user/product/${p.id}`)">
                {{ locale === 'ko' && p.titleKo ? p.titleKo : p.title }}
              </div>
              <div class="mt-1 flex items-center gap-2">
                <span class="text-base font-bold sm-text-primary">{{ $t('common.currency') }}{{ p.price }}</span>
                <el-tag size="small" type="info">{{ statusText(p.status) }}</el-tag>
              </div>
              <div class="mt-1 text-xs text-gray-500">
                {{ $t('product.viewCount') }} {{ p.viewCount || 0 }} · {{ $t('profile.favoriteCount') }} {{ p.favoriteCount || 0 }}
              </div>
            </div>
          </div>
          <div class="px-4 pb-4 flex items-center justify-between">
            <button class="text-sm text-gray-600" @click="router.push(`/user/product/${p.id}`)">{{ $t('common.view') }}</button>
            <div class="flex items-center gap-3">
              <button v-if="p.status === 2" class="text-sm text-gray-600" @click="toggleStatus(p, 3)">{{ $t('common.unlist') }}</button>
              <button v-else-if="p.status === 3" class="text-sm text-gray-600" @click="toggleStatus(p, 2)">{{ $t('common.list') }}</button>
              <button class="text-sm text-red-500" @click="removeProduct(p)">{{ $t('common.delete') }}</button>
            </div>
          </div>
        </div>

        <div v-if="!loading && products.length === 0" class="col-span-2 rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
          {{ $t('common.noData') }}
        </div>
      </div>

      <div v-if="total > 0" class="pt-2 flex items-center justify-between">
        <button class="text-sm text-gray-700" :disabled="pageNum === 1" @click="prevPage">
          {{ $t('pagination.prev') }}
        </button>
        <div class="text-xs text-gray-500">
          {{ pageNum }}/{{ totalPages }}
        </div>
        <button class="text-sm text-gray-700" :disabled="pageNum >= totalPages" @click="nextPage">
          {{ $t('pagination.next') }}
        </button>
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

const router = useRouter()
const { t, locale } = useI18n()

const loading = ref(false)
const products = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const statusFilter = ref(null)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const statusTabs = computed(() => [
  { value: null, label: t('common.all') },
  { value: 1, label: t('order.pendingReview') },
  { value: 2, label: t('order.onSale') },
  { value: 3, label: t('order.offSale') },
  { value: 4, label: t('order.sold') }
])

const statusText = (status) => {
  const textMap = {
    0: t('order.draft'),
    1: t('order.pendingReview'),
    2: t('order.onSale'),
    3: t('order.offSale'),
    4: t('order.sold')
  }
  return textMap[status] || t('common.unknown')
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

const fetchProducts = async () => {
  loading.value = true
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await productApi.getMyProducts(params)
    if (res.code === '200') {
      products.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || t('common.fetchFailed'))
    }
  } catch (e) {
    ElMessage.error(e?.message || t('common.fetchFailed'))
  } finally {
    loading.value = false
  }
}

const setStatus = (value) => {
  statusFilter.value = value
  pageNum.value = 1
  fetchProducts()
}

const prevPage = () => {
  if (pageNum.value <= 1) return
  pageNum.value -= 1
  fetchProducts()
}

const nextPage = () => {
  if (pageNum.value >= totalPages.value) return
  pageNum.value += 1
  fetchProducts()
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
    if (res.code === '200') {
      ElMessage.success(t('myProducts.toggleSuccess', { action: actionText }))
      fetchProducts()
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
    if (res.code === '200') {
      ElMessage.success(t('myProducts.deleteSuccess'))
      if (products.value.length === 1 && pageNum.value > 1) pageNum.value -= 1
      fetchProducts()
    } else {
      ElMessage.error(res.message || t('myProducts.deleteFail'))
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(t('myProducts.deleteFail'))
  }
}

onMounted(() => {
  fetchProducts()
})
</script>
