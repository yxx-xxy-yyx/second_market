<template>
  <div class="my-orders-container">
    <template v-if="isMobileScreen">
      <div class="bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pb-24">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur-xl border-b border-gray-100 shadow-sm px-4 py-4 flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="h-10 w-10 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center hover:from-gray-200 hover:to-gray-300 transition-all cursor-pointer active:scale-95" @click="router.back()">
              <el-icon :size="20" class="text-gray-700"><ArrowLeft /></el-icon>
            </div>
            <h1 class="text-xl font-black text-gray-900">{{ $t('ordersPage.title') }}</h1>
          </div>
          <LangSwitcher />
        </div>

        <div class="px-4 py-5 space-y-5">
          <div class="bg-white/90 backdrop-blur-xl p-3 rounded-3xl shadow-lg border border-gray-100 flex gap-3">
            <button v-for="tab in roleTabs" :key="tab.value" class="flex-1 py-4 rounded-2xl text-base font-black transition-all duration-300" :class="activeRole === tab.value ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'" @click="activeRole = tab.value">
              {{ tab.label }}
            </button>
          </div>

          <div v-loading="loading" class="space-y-4">
            <div v-for="order in orders" :key="order.id" class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 p-5 group">
              <div class="flex items-center justify-between mb-4">
                <span class="text-sm text-gray-500 font-medium">{{ $t('ordersPage.orderNo') }}: {{ order.id }}</span>
                <el-tag size="small" :type="getStatusType(order.status)" class="!rounded-xl !text-sm !font-bold">{{ getStatusText(order.status) }}</el-tag>
              </div>
              <div class="flex gap-4 mb-5 cursor-pointer" @click="router.push(`/user/product/${order.productId}`)">
                <div class="w-24 h-24 rounded-2xl bg-gradient-to-br from-gray-100 to-gray-200 overflow-hidden shadow-md hover:shadow-lg transition-all">
                  <img :src="getProductImage(order.productImages)" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300" />
                </div>
                <div class="flex-1 min-w-0 pt-1">
                  <div class="text-base font-black text-gray-900 line-clamp-2 leading-snug">{{ order.productTitle }}</div>
                  <div class="mt-3 text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ order.amount }}</div>
                </div>
              </div>
              <div class="flex justify-end gap-3">
                <el-button v-if="order.status === 0" size="large" type="primary" class="!rounded-2xl !font-bold !shadow-md hover:!shadow-lg transition-all" @click="handlePay(order)">{{ $t('ordersPage.toPay') }}</el-button>
                <el-button v-if="order.status === 1" size="large" type="success" class="!rounded-2xl !font-bold !shadow-md hover:!shadow-lg transition-all" @click="handleConfirm(order)">{{ $t('ordersPage.confirmReceive') }}</el-button>
                <el-button size="large" plain class="!rounded-2xl !font-bold" @click="router.push(`/user/product/${order.productId}`)">{{ $t('ordersPage.viewDetail') }}</el-button>
              </div>
            </div>
            <el-empty v-if="orders.length === 0 && !loading" :description="$t('ordersPage.empty')" :image-size="130" />
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pt-10 pb-24">
        <div class="px-8 max-w-7xl mx-auto">
          <div class="flex items-center justify-between mb-10">
            <div class="flex items-center gap-5">
              <div class="h-14 w-14 bg-gradient-to-br from-primary to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl">
                <el-icon :size="28" class="text-white"><Document /></el-icon>
              </div>
              <h1 class="text-4xl font-black text-gray-900">{{ $t('ordersPage.title') }}</h1>
            </div>
            <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-lg border border-gray-100 p-2 flex gap-2">
              <button v-for="tab in roleTabs" :key="tab.value" class="px-8 py-3 rounded-2xl text-base font-bold transition-all duration-300" :class="activeRole === tab.value ? 'bg-gradient-to-r from-primary to-indigo-600 text-white shadow-lg' : 'text-gray-600 hover:bg-gray-100'" @click="activeRole = tab.value">
                {{ tab.label }}
              </button>
            </div>
          </div>

          <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-100 overflow-hidden">
            <el-table v-loading="loading" :data="orders" style="width: 100%" class="mt-4">
              <el-table-column :label="$t('ordersPage.productInfo')" min-width="350">
                <template #default="{ row }">
                  <div class="flex items-center gap-4">
                    <el-image :src="getProductImage(row.productImages)" class="w-20 h-20 rounded-2xl shadow-md" fit="cover" />
                    <div class="min-w-0">
                      <div class="text-base font-black text-gray-900 truncate">{{ row.productTitle }}</div>
                      <div class="text-sm text-gray-500 mt-2">{{ $t('ordersPage.orderNo') }}: {{ row.id }}</div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ordersPage.amount')" width="150">
                <template #default="{ row }">
                  <span class="text-2xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">¥{{ row.amount }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ordersPage.status')" width="130">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" class="!rounded-xl !text-base !font-bold">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ordersPage.createTime')" width="200">
                <template #default="{ row }">
                  <span class="text-sm text-gray-600 font-medium">{{ new Date(row.createTime).toLocaleString() }}</span>
                </template>
              </el-table-column>
              <el-table-column :label="$t('ordersPage.actions')" width="250" fixed="right">
                <template #default="{ row }">
                  <el-button v-if="row.status === 0" size="large" type="primary" class="!rounded-2xl !font-bold !shadow-md hover:!shadow-lg transition-all" @click="handlePay(row)">{{ $t('ordersPage.pay') }}</el-button>
                  <el-button v-if="row.status === 1" size="large" type="success" class="!rounded-2xl !font-bold !shadow-md hover:!shadow-lg transition-all" @click="handleConfirm(row)">{{ $t('ordersPage.confirmReceive') }}</el-button>
                  <el-button size="large" class="!rounded-2xl !font-bold" @click="router.push(`/user/product/${row.productId}`)">{{ $t('ordersPage.detail') }}</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <el-empty v-if="orders.length === 0 && !loading" :description="$t('ordersPage.empty')" :image-size="160" />
        </div>
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
import { ArrowLeft, Document } from '@element-plus/icons-vue'
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
