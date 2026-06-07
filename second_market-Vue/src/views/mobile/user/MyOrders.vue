<template>
  <div class="bg-gray-50 min-h-screen">
    <!-- 渐变背景头部 -->
    <div class="bg-gradient-to-br from-primary to-primaryDark pt-8 pb-16 px-6 rounded-b-[3rem] shadow-lg relative overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>

      <div class="relative z-10 flex items-center h-10 mb-3">
        <button @click="router.back()" class="absolute left-0 p-1 rounded-full hover:bg-white/10 active:scale-90 transition-all text-white">
          <ChevronLeftIcon class="w-7 h-7" />
        </button>
        <h1 class="w-full text-center text-lg font-bold text-white tracking-wide">{{ $t('nav.myOrders') }}</h1>
      </div>

      <div class="mt-3 flex gap-2 relative z-10">
        <button
          class="flex-1 py-2 rounded-xl text-sm border backdrop-blur-md"
          :class="activeTab === 'buyer' ? 'bg-white/25 border-white/40 text-white font-semibold' : 'bg-white/10 border-white/20 text-white/70'"
          @click="switchTab('buyer')"
        >
          {{ $t('order.myBuy') }}
        </button>
        <button
          class="flex-1 py-2 rounded-xl text-sm border backdrop-blur-md"
          :class="activeTab === 'seller' ? 'bg-white/25 border-white/40 text-white font-semibold' : 'bg-white/10 border-white/20 text-white/70'"
          @click="switchTab('seller')"
        >
          {{ $t('order.mySell') }}
        </button>
      </div>

      <div class="mt-3 flex gap-2 overflow-x-auto sm-tap relative z-10">
        <button
          v-for="s in statusTabs"
          :key="String(s.value)"
          class="px-3 py-2 rounded-xl text-sm border whitespace-nowrap backdrop-blur-md"
          :class="currentStatus === s.value ? 'bg-white/25 border-white/40 text-white font-semibold' : 'bg-white/10 border-white/20 text-white/70'"
          @click="setStatus(s.value)"
        >
          {{ s.label }}
        </button>
      </div>
    </div>

    <div class="px-3 -mt-8 relative z-20 space-y-3">
      <div v-if="initialLoading && orders.length === 0" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
        {{ $t('common.loading') }}
      </div>

      <van-list
        v-else
        v-model:loading="loading"
        :finished="finished"
        :finished-text="orders.length > 0 ? finishedText : ''"
        :immediate-check="false"
        class="space-y-3"
        @load="loadMore"
      >
        <div
          v-for="o in orders"
          :key="o.id"
          class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden active:scale-[0.99] transition-transform duration-200"
        >
          <div class="p-3 flex gap-3 items-center">
            <!-- 商品图片 -->
            <div class="w-20 h-20 bg-gray-100 rounded-xl overflow-hidden flex-shrink-0 relative cursor-pointer"
              @click="router.push(`/user/product/${o.productId}`)">
              <img v-if="o.productImage" :src="getOrderImage(o.productImage)" class="w-full h-full object-cover" loading="lazy" />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-300">
                <el-icon :size="24"><ShoppingBagIcon class="w-6 h-6" /></el-icon>
              </div>
            </div>

            <!-- 订单信息 -->
            <div class="flex-1 min-w-0 flex flex-col justify-between py-0.5">
              <div>
                <div class="flex items-center justify-between mb-1">
                  <div class="text-xs text-gray-400">#{{ o.id }}</div>
                  <el-tag size="small" :type="statusType(o.status)">{{ statusText(o.status) }}</el-tag>
                </div>
                <div class="text-sm font-semibold text-gray-900 line-clamp-1 leading-snug">
                  {{ o.productTitle || o.productName || $t('common.order') + ' #' + o.id }}
                </div>
                <div class="mt-1 flex items-baseline gap-1">
                  <span class="text-base font-bold text-red-500">{{ $t('common.currency') }}{{ o.amount }}</span>
                </div>
              </div>

              <div class="flex items-center justify-end gap-2 mt-2">
                <el-button v-if="o.status === 0" size="small" type="primary" @click="pay(o)">{{ $t('common.pay') }}</el-button>
                <el-button v-if="o.status === 1" size="small" type="success" @click="confirm(o)">{{ $t('order.confirmReceived') }}</el-button>
                <el-button v-if="o.status === 0" size="small" type="info" @click="cancel(o)">{{ $t('common.cancel') }}</el-button>
              </div>
            </div>
          </div>
        </div>
      </van-list>

      <div v-if="!initialLoading && orders.length === 0" class="rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
        {{ $t('common.noData') }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChevronLeftIcon, ShoppingBagIcon } from '@heroicons/vue/24/outline'
import { orderApi } from '@/api/order'
import { formatImageUrl } from '@/utils/url'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const loading = ref(false)
const initialLoading = ref(true)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const finished = ref(false)
const activeTab = ref(route.query.tab === 'seller' ? 'seller' : 'buyer')
const currentStatus = ref(null)

const finishedText = computed(() => t('common.noMoreData') || '')

const getOrderImage = (images) => {
  if (!images) return ''
  try {
    const parsed = typeof images === 'string' ? JSON.parse(images) : images
    const first = Array.isArray(parsed) ? parsed[0] : parsed
    return formatImageUrl(first) || ''
  } catch {
    return formatImageUrl(images) || ''
  }
}

const statusTabs = computed(() => [
  { value: null, label: t('common.all') },
  { value: 0, label: t('order.pendingPay') },
  { value: 1, label: t('order.pendingReceive') },
  { value: 2, label: t('order.pendingEvaluation') },
  { value: 3, label: t('order.completed') },
  { value: 4, label: t('order.cancelled') }
])

const statusText = (status) => {
  const map = {
    0: t('order.pendingPay'),
    1: t('order.pendingReceive'),
    2: t('order.pendingEvaluation'),
    3: t('order.completed'),
    4: t('order.cancelled')
  }
  return map[status] || t('common.unknown')
}

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

const fetchOrders = async (isLoadMore = false) => {
  if (!isLoadMore) initialLoading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: activeTab.value === 'buyer' ? 0 : 1
    }
    if (currentStatus.value !== null) params.status = currentStatus.value
    const res = await orderApi.getOrderList(params)
    if (res.code === '200') {
      const newOrders = res.data.records || []
      orders.value = isLoadMore ? [...orders.value, ...newOrders] : newOrders
      total.value = res.data.total || 0
      finished.value = newOrders.length < pageSize.value
    } else {
      ElMessage.error(res.message || t('myOrders.fetchFailed'))
    }
  } catch (e) {
    ElMessage.error(e?.message || t('myOrders.fetchFailed'))
  } finally {
    loading.value = false
    initialLoading.value = false
  }
}

const loadMore = () => {
  if (finished.value || loading.value) return
  pageNum.value++
  fetchOrders(true)
}

const switchTab = (tab) => {
  activeTab.value = tab
  currentStatus.value = null
  pageNum.value = 1
  finished.value = false
  orders.value = []
  fetchOrders()
}

const setStatus = (status) => {
  currentStatus.value = status
  pageNum.value = 1
  finished.value = false
  orders.value = []
  fetchOrders()
}

const pay = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmPay'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    const res = await orderApi.payOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.message || t('common.requestFailed'))
  }
}

const confirm = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmReceived'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    const res = await orderApi.confirmOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.message || t('common.requestFailed'))
  }
}

const cancel = async (order) => {
  try {
    await ElMessageBox.confirm(t('order.confirmCancel'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    })
    const res = await orderApi.cancelOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('common.save'))
      fetchOrders()
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.message || t('common.requestFailed'))
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
