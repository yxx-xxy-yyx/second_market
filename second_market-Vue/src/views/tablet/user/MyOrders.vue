<template>
  <div class="px-4 py-5">
    <div class="mx-auto max-w-[980px] space-y-4">
      <div class="rounded-2xl bg-white border border-gray-100 shadow-sm p-5">
        <div class="flex items-center justify-between">
          <div class="text-lg font-semibold text-gray-900">{{ $t('nav.myOrders') }}</div>
          <button class="text-sm sm-text-primary" @click="refresh">{{ $t('common.refresh') }}</button>
        </div>

        <div class="mt-3 flex gap-2">
          <button
            class="flex-1 py-2 rounded-xl text-sm border"
            :class="activeTab === 'buyer' ? 'sm-bg-primary-soft sm-border-primary-soft sm-text-primary' : 'border-gray-100 bg-gray-50 text-gray-700'"
            @click="switchTab('buyer')"
          >
            {{ $t('order.myBuy') }}
          </button>
          <button
            class="flex-1 py-2 rounded-xl text-sm border"
            :class="activeTab === 'seller' ? 'sm-bg-primary-soft sm-border-primary-soft sm-text-primary' : 'border-gray-100 bg-gray-50 text-gray-700'"
            @click="switchTab('seller')"
          >
            {{ $t('order.mySell') }}
          </button>
        </div>

        <div class="mt-3 flex gap-2 overflow-x-auto sm-tap">
          <button
            v-for="s in statusTabs"
            :key="String(s.value)"
            class="px-3 py-2 rounded-xl text-sm border whitespace-nowrap"
            :class="currentStatus === s.value ? 'sm-bg-primary-soft sm-border-primary-soft sm-text-primary' : 'border-gray-100 text-gray-700 bg-gray-50'"
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
          v-for="o in orders"
          :key="o.id"
          class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden"
        >
          <div class="p-4">
            <div class="flex items-center justify-between">
              <div class="text-xs text-gray-500">#{{ o.id }}</div>
              <el-tag size="small" :type="statusType(o.status)">{{ statusText(o.status) }}</el-tag>
            </div>
            <div class="mt-2 flex items-center justify-between">
              <div class="text-base font-semibold text-gray-900">{{ $t('common.currency') }}{{ o.amount }}</div>
              <button class="text-sm sm-text-primary" @click="router.push(`/user/product/${o.productId}`)">{{ $t('common.view') }}</button>
            </div>
          </div>
          <div class="px-4 pb-4 flex items-center justify-end gap-2">
            <el-button v-if="o.status === 0" size="small" type="primary" @click="pay(o)">{{ $t('common.pay') }}</el-button>
            <el-button v-if="o.status === 1" size="small" type="success" @click="confirm(o)">{{ $t('order.confirmReceived') }}</el-button>
            <el-button v-if="o.status === 0" size="small" type="info" @click="cancel(o)">{{ $t('common.cancel') }}</el-button>
          </div>
        </div>

        <div v-if="!loading && orders.length === 0" class="col-span-2 rounded-2xl bg-white border border-gray-100 shadow-sm p-6 text-sm text-gray-500 text-center">
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
import { orderApi } from '@/api/order'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const activeTab = ref('buyer')
const currentStatus = ref(null)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

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

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: activeTab.value === 'buyer' ? 0 : 1
    }
    if (currentStatus.value !== null) params.status = currentStatus.value
    const res = await orderApi.getOrderList(params)
    if (res.code === '200') {
      orders.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || t('myOrders.fetchFailed'))
    }
  } catch (e) {
    ElMessage.error(e?.message || t('myOrders.fetchFailed'))
  } finally {
    loading.value = false
  }
}

const refresh = () => {
  pageNum.value = 1
  fetchOrders()
}

const switchTab = (tab) => {
  activeTab.value = tab
  currentStatus.value = null
  pageNum.value = 1
  fetchOrders()
}

const setStatus = (status) => {
  currentStatus.value = status
  pageNum.value = 1
  fetchOrders()
}

const prevPage = () => {
  if (pageNum.value <= 1) return
  pageNum.value -= 1
  fetchOrders()
}

const nextPage = () => {
  if (pageNum.value >= totalPages.value) return
  pageNum.value += 1
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
