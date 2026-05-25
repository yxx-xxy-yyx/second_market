<template>
  <div class="my-orders-container">
    <template v-if="isMobileScreen">
      <div class="bg-[#f8fafc] min-h-screen pb-24">
        <div class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-100 px-4 py-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon class="text-xl text-gray-700 cursor-pointer" @click="router.back()"><ArrowLeft /></el-icon>
            <h1 class="text-lg font-bold text-gray-900">{{ $t('ordersPage.title') }}</h1>
          </div>
          <LangSwitcher />
        </div>

        <div class="px-3 py-4 space-y-4">
          <div class="bg-white p-4 rounded-2xl shadow-sm border border-gray-100 flex gap-2">
            <button v-for="tab in roleTabs" :key="tab.value" class="flex-1 py-2 rounded-xl text-sm border" :class="activeRole === tab.value ? 'bg-primary/10 border-primary/20 text-primary' : 'bg-gray-50 border-gray-100 text-gray-500'" @click="activeRole = tab.value">
              {{ tab.label }}
            </button>
          </div>

          <div v-loading="loading" class="space-y-3">
            <div v-for="order in orders" :key="order.id" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <div class="flex items-center justify-between mb-3">
                <span class="text-xs text-gray-400">{{ $t('ordersPage.orderNo') }}: {{ order.id }}</span>
                <el-tag size="small" :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
              </div>
              <div class="flex gap-3 mb-4" @click="router.push(`/user/product/${order.productId}`)">
                <div class="w-16 h-16 rounded-lg bg-gray-50 overflow-hidden">
                  <img :src="getProductImage(order.productImages)" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-bold truncate">{{ order.productTitle }}</div>
                  <div class="mt-1 text-lg font-black text-primary">¥{{ order.amount }}</div>
                </div>
              </div>
              <div class="flex justify-end gap-2">
                <el-button v-if="order.status === 0" size="small" type="primary" @click="handlePay(order)">{{ $t('ordersPage.toPay') }}</el-button>
                <el-button v-if="order.status === 1" size="small" type="success" @click="handleConfirm(order)">{{ $t('ordersPage.confirmReceive') }}</el-button>
                <el-button size="small" plain @click="router.push(`/user/product/${order.productId}`)">{{ $t('ordersPage.viewDetail') }}</el-button>
              </div>
            </div>
            <el-empty v-if="orders.length === 0 && !loading" :description="$t('ordersPage.empty')" />
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="my-orders-page p-6 max-w-7xl mx-auto pb-24">
        <h1 class="text-2xl font-bold mb-6">{{ $t('ordersPage.title') }}</h1>
        <el-tabs v-model="activeRole" class="order-tabs">
          <el-tab-pane :label="$t('ordersPage.buyer')" name="buyer" />
          <el-tab-pane :label="$t('ordersPage.seller')" name="seller" />
        </el-tabs>

        <el-table v-loading="loading" :data="orders" style="width: 100%" class="mt-4">
          <el-table-column :label="$t('ordersPage.productInfo')" min-width="300">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <el-image :src="getProductImage(row.productImages)" class="w-16 h-16 rounded" fit="cover" />
                <div class="min-w-0">
                  <div class="font-bold truncate">{{ row.productTitle }}</div>
                  <div class="text-xs text-gray-500 mt-1">{{ $t('ordersPage.orderNo') }}: {{ row.id }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('ordersPage.amount')" width="120">
            <template #default="{ row }">
              <span class="font-bold text-primary">¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="$t('ordersPage.status')" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="$t('ordersPage.createTime')" width="180">
            <template #default="{ row }">
              {{ new Date(row.createTime).toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column :label="$t('ordersPage.actions')" width="200" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" size="small" type="primary" @click="handlePay(row)">{{ $t('ordersPage.pay') }}</el-button>
              <el-button v-if="row.status === 1" size="small" type="success" @click="handleConfirm(row)">{{ $t('ordersPage.confirmReceive') }}</el-button>
              <el-button size="small" @click="router.push(`/user/product/${row.productId}`)">{{ $t('ordersPage.detail') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { orderApi } from '@/api/order'
import { formatProductImageUrl } from '@/utils/url'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const { t } = useI18n()
const activeRole = ref('buyer')
const orders = ref([])
const loading = ref(false)

const roleTabs = computed(() => [
  { label: t('ordersPage.buyer'), value: 'buyer' },
  { label: t('ordersPage.seller'), value: 'seller' }
])

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(images)
  }
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: t('ordersPage.statusText.s0'),
    1: t('ordersPage.statusText.s1'),
    2: t('ordersPage.statusText.s2'),
    3: t('ordersPage.statusText.s3')
  }
  return texts[status] || t('ordersPage.statusText.unknown')
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = activeRole.value === 'buyer'
      ? await orderApi.getBuyerOrders({ pageNum: 1, pageSize: 50 })
      : await orderApi.getSellerOrders({ pageNum: 1, pageSize: 50 })
    if (res.code === '200') orders.value = res.data?.records || []
  } catch (e) {
    ElMessage.error(t('ordersPage.fetchFail'))
  } finally {
    loading.value = false
  }
}

const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm(t('ordersPage.confirmPay'), t('ordersPage.confirmPayTitle'), {
      confirmButtonText: t('ordersPage.confirmPayOk'),
      cancelButtonText: t('common.cancel')
    })
    const res = await orderApi.payOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('ordersPage.paySuccess'))
      fetchOrders()
    } else {
      ElMessage.error(res.message || t('ordersPage.payFail'))
    }
  } catch {}
}
const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm(t('ordersPage.confirmReceiveTip'), t('common.tip'))
    const res = await orderApi.confirmOrder(order.id)
    if (res.code === '200') {
      ElMessage.success(t('ordersPage.receiveSuccess'))
      fetchOrders()
    }
  } catch (e) {}
}

onMounted(() => fetchOrders())
watch(activeRole, () => fetchOrders())
</script>
