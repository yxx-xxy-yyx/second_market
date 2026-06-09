<template>
  <div class="pay-fail-container">
    <!-- 移动端视图 -->
    <div v-if="isMobileScreen" class="pay-fail-mobile min-h-screen bg-gradient-to-br from-red-50 to-orange-100 flex flex-col">
      <div class="flex-1 flex items-center justify-center p-6">
        <div class="text-center">
          <div class="w-24 h-24 rounded-full bg-white shadow-xl flex items-center justify-center mx-auto mb-6">
            <el-icon :size="60" class="text-red-500"><CircleClose /></el-icon>
          </div>
          <h2 class="text-2xl font-black text-gray-900 mb-2">支付失败</h2>
          <p class="text-gray-500 mb-2">{{ reason || '支付未完成' }}</p>
          <p class="text-sm text-gray-400 mb-6">订单号：{{ orderId }}</p>
          
          <div class="space-y-3">
            <el-button type="primary" class="w-full !h-12 !rounded-full !font-bold" @click="retryPay">重新支付</el-button>
            <el-button class="w-full !h-12 !rounded-full" @click="goToOrders">查看订单</el-button>
            <el-button text class="w-full !h-12 !text-gray-500" @click="goHome">返回首页</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端视图 -->
    <div v-else class="pay-fail-desktop min-h-screen bg-gradient-to-br from-red-50 to-orange-100 flex items-center justify-center">
      <div class="bg-white rounded-3xl shadow-2xl p-12 text-center max-w-lg">
        <div class="w-28 h-28 rounded-full bg-red-100 flex items-center justify-center mx-auto mb-6">
          <el-icon :size="80" class="text-red-500"><CircleClose /></el-icon>
        </div>
        <h2 class="text-3xl font-black text-gray-900 mb-2">支付失败</h2>
        <p class="text-gray-500 mb-2">{{ reason || '支付未完成' }}</p>
        <p class="text-sm text-gray-400 mb-8">订单号：{{ orderId }}</p>

        <div class="flex gap-4">
          <el-button type="primary" class="flex-1 !h-14 !rounded-full !text-lg !font-bold" @click="retryPay">重新支付</el-button>
          <el-button class="flex-1 !h-14 !rounded-full !text-lg" @click="goToOrders">查看订单</el-button>
        </div>
        <el-button text class="mt-4 !text-gray-500" @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleClose } from '@element-plus/icons-vue'
import { useDeviceType } from '@/utils/device'

const route = useRoute()
const router = useRouter()
const { isMobileScreen } = useDeviceType()

const orderId = computed(() => route.query.orderId || '')
const reason = computed(() => route.query.reason || '')

const retryPay = () => {
  router.replace({
    path: '/neo/pay-center',
    query: { orderId: orderId.value }
  })
}

const goToOrders = () => {
  router.push('/user/orders')
}

const goHome = () => {
  router.push('/neo/home')
}
</script>
