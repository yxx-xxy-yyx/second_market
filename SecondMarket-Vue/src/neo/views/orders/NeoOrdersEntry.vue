<template>
  <div class="space-y-5">
    <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
      <div class="px-5 py-5 bg-gray-50 flex items-start justify-between gap-4">
        <div>
          <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ title }}</div>
          <div class="mt-1 text-[12px] text-gray-500">{{ subtitle }}</div>
        </div>
        <div class="flex items-center gap-2">
          <el-button text class="!text-gray-500" @click="router.push('/neo/home')">{{ $t('neo.pay.backHome') }}</el-button>
        </div>
      </div>

      <div class="px-5 py-4 border-b border-gray-100">
        <div class="flex flex-wrap items-center gap-2">
          <el-button :type="isRoute('pending-pay') ? 'primary' : 'default'" round @click="router.push('/neo/orders/pending-pay')">
            {{ $t('neo.order.pendingPay') }}
          </el-button>
          <el-button :type="isRoute('pending-receive') ? 'primary' : 'default'" round @click="router.push('/neo/orders/pending-receive')">
            {{ $t('neo.order.pendingReceive') }}
          </el-button>
          <el-button :type="isRoute('completed') ? 'primary' : 'default'" round @click="router.push('/neo/orders/completed')">
            {{ $t('neo.order.completed') }}
          </el-button>
          <el-button :type="isRoute('cancelled') ? 'primary' : 'default'" round @click="router.push('/neo/orders/cancelled')">
            {{ $t('neo.order.cancelled') }}
          </el-button>
          <el-button :type="isRoute('pending-ship') ? 'primary' : 'default'" round @click="router.push('/neo/orders/pending-ship')">
            {{ $t('neo.order.pendingShip') }}
          </el-button>
        </div>
      </div>

      <div class="px-5 py-5" v-loading="loading">
        <el-empty v-if="!loading && orders.length === 0" :description="$t('common.noData')" :image-size="140" />

        <div v-else class="space-y-3">
          <div
            v-for="o in orders"
            :key="o.id"
            class="rounded-2xl border border-gray-100 hover:border-[rgba(var(--primary-rgb),0.35)] transition bg-white overflow-hidden"
          >
            <div class="px-4 py-4 flex items-center justify-between gap-3 bg-gray-50">
              <div class="min-w-0">
                <div class="text-[12px] text-gray-500">
                  {{ $t('neo.order.orderNo') }}: <span class="font-semibold text-gray-900">{{ o.orderNo || o.id }}</span>
                </div>
                <div class="mt-1 text-[12px] text-gray-500">
                  {{ $t('neo.order.createdAt') }}: <span class="font-semibold text-gray-900">{{ formatTime(o.createTime) }}</span>
                </div>
              </div>
              <el-tag :type="statusTagType(o.status)" size="small">{{ statusText(o.status) }}</el-tag>
            </div>

            <div class="p-4 flex items-start gap-4">
              <button class="h-[70px] w-[70px] rounded-2xl overflow-hidden bg-gray-50 shrink-0" @click="goDetail(o.productId)">
                <img :src="getProductImage(o.productImages)" class="h-full w-full object-cover" @error="handleImgError" />
              </button>
              <div class="flex-1 min-w-0">
                <div class="text-[14px] font-semibold text-gray-900 line-clamp-2">
                  {{ locale === 'ko' && o.productTitleKo ? o.productTitleKo : o.productTitle }}
                </div>
                <div class="mt-2 text-[12px] text-gray-500 truncate">
                  {{ role === 'seller' ? $t('neo.order.buyer') : $t('neo.order.seller') }}:
                  <span class="text-gray-700 font-semibold">{{ role === 'seller' ? (o.buyerNickname || '-') : (o.sellerNickname || '-') }}</span>
                </div>
              </div>
              <div class="text-right shrink-0">
                <div class="text-[12px] text-gray-500">{{ $t('neo.order.amount') }}</div>
                <div class="text-[16px] font-extrabold text-[color:var(--primary-color)]">{{ currency }}{{ Number(o.amount || 0).toFixed(2) }}</div>
              </div>
            </div>

            <div class="px-4 py-4 border-t border-gray-100 flex flex-wrap items-center justify-end gap-2">
              <el-button v-if="role === 'buyer' && o.status === 0" type="primary" @click="goPay(o.id)">{{ $t('common.pay') }}</el-button>
              <el-button v-if="role === 'buyer' && o.status === 0" @click="cancel(o.id)">{{ $t('common.cancel') }}</el-button>
              <el-button v-if="role === 'buyer' && o.status === 1" type="success" @click="finish(o.id)">{{ $t('common.confirmReceipt') }}</el-button>
              <el-button @click="goDetail(o.productId)">{{ $t('common.view') }}</el-button>
            </div>
          </div>
        </div>

        <div v-if="total > 0" class="mt-6 flex items-center justify-center">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchOrders"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()

const currency = t('common.currency')

const role = computed(() => {
  if (route.path.includes('/pending-ship')) return 'seller'
  return 'buyer'
})

const status = computed(() => {
  if (route.path.includes('/pending-pay')) return 0
  if (route.path.includes('/pending-receive')) return 1
  if (route.path.includes('/completed')) return 2
  if (route.path.includes('/cancelled')) return 3
  if (route.path.includes('/pending-ship')) return 1
  return null
})

const title = computed(() => route.meta?.title || '')
const subtitle = computed(() => (role.value === 'seller' ? t('neo.order.subtitleSeller') : t('neo.order.subtitleBuyer')))

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const isOk = (res) => res && (res.code === '200' || res.success)

const fetchOrders = async () => {
  loading.value = true
  try {
    const req = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (status.value != null) req.status = status.value
    const res = role.value === 'seller' ? await orderApi.getSellerOrders(req) : await orderApi.getBuyerOrders(req)
    if (!isOk(res)) throw new Error(res.message || t('common.requestFailed'))
    const data = res.data || {}
    orders.value = data.records || []
    total.value = Number(data.total || orders.value.length || 0)
  } catch (e) {
    ElMessage.error(e?.message || t('common.requestFailed'))
  } finally {
    loading.value = false
  }
}

watch([role, status], () => {
  pageNum.value = 1
  fetchOrders()
})

onMounted(() => fetchOrders())

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

const goPay = (orderId) => {
  router.push({ path: '/neo/pay-center', query: { orderIds: String(orderId) } })
}

const cancel = async (orderId) => {
  await ElMessageBox.confirm(t('order.confirmCancel'), t('common.tip'), {
    type: 'warning',
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
  const res = await orderApi.cancelOrder(orderId)
  if (!isOk(res)) {
    ElMessage.error(res?.message || t('common.requestFailed'))
    return
  }
  ElMessage.success(t('order.cancelled'))
  fetchOrders()
}

const finish = async (orderId) => {
  await ElMessageBox.confirm(t('order.confirmReceived'), t('common.tip'), {
    type: 'warning',
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel')
  })
  const res = await orderApi.finishOrder(orderId)
  if (!isOk(res)) {
    ElMessage.error(res?.message || t('common.requestFailed'))
    return
  }
  ElMessage.success(t('common.success'))
  fetchOrders()
}

const statusText = (s) => {
  if (s === 0) return t('neo.order.pendingPay')
  if (s === 1) return role.value === 'seller' ? t('neo.order.pendingShip') : t('neo.order.pendingReceive')
  if (s === 2) return t('neo.order.completed')
  if (s === 3) return t('neo.order.cancelled')
  return t('common.unknown')
}

const statusTagType = (s) => {
  if (s === 0) return 'warning'
  if (s === 1) return 'primary'
  if (s === 2) return 'success'
  if (s === 3) return 'info'
  return 'info'
}

const formatTime = (time) => {
  if (!time) return '-'
  try {
    return new Date(time).toLocaleString()
  } catch {
    return String(time)
  }
}

const isRoute = (key) => route.path.includes(`/orders/${key}`)

const DEFAULT_IMAGE = 'https://via.placeholder.com/300x300?text=No+Image'
const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return arr?.[0] || DEFAULT_IMAGE
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
}
</script>

