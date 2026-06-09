<template>
  <div class="pay-success-container">
    <!-- 移动端视图 -->
    <div v-if="isMobileScreen" class="pay-success-mobile min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 flex flex-col">
      <div class="flex-1 flex items-center justify-center p-6">
        <div class="text-center">
          <div class="w-24 h-24 rounded-full bg-white shadow-xl flex items-center justify-center mx-auto mb-6 animate-bounce">
            <el-icon :size="60" class="text-green-500"><CircleCheck /></el-icon>
          </div>
          <h2 class="text-2xl font-black text-gray-900 mb-2">支付成功</h2>
          <p class="text-gray-500 mb-6">感谢您的购买</p>
          
          <div class="bg-white rounded-2xl p-4 shadow-lg mb-6">
            <div class="flex justify-between mb-2">
              <span class="text-gray-500">订单号</span>
              <span class="font-bold text-gray-900">{{ orderId }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-500">支付金额</span>
              <span class="font-bold text-green-500">¥{{ amount }}</span>
            </div>
          </div>

          <div class="space-y-3">
            <el-button type="primary" class="w-full !h-12 !rounded-full !font-bold" @click="goToOrders">查看订单</el-button>
            <el-button class="w-full !h-12 !rounded-full" @click="goHome">返回首页</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端视图 -->
    <div v-else class="pay-success-desktop min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 flex items-center justify-center">
      <div class="bg-white rounded-3xl shadow-2xl p-12 text-center max-w-lg">
        <div class="w-28 h-28 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-6 animate-bounce">
          <el-icon :size="80" class="text-green-500"><CircleCheck /></el-icon>
        </div>
        <h2 class="text-3xl font-black text-gray-900 mb-2">支付成功</h2>
        <p class="text-gray-500 mb-8">感谢您的购买</p>
        
        <div class="bg-gray-50 rounded-2xl p-6 mb-8">
          <div class="flex justify-between mb-3">
            <span class="text-gray-500">订单号</span>
            <span class="font-bold text-gray-900">{{ orderId }}</span>
          </div>
          <div class="flex justify-between pt-3 border-t border-gray-200">
            <span class="text-gray-500">支付金额</span>
            <span class="text-2xl font-black text-green-500">¥{{ amount }}</span>
          </div>
        </div>

        <div class="flex gap-4">
          <el-button type="primary" class="flex-1 !h-14 !rounded-full !text-lg !font-bold" @click="goToOrders">查看订单</el-button>
          <el-button class="flex-1 !h-14 !rounded-full !text-lg" @click="goHome">返回首页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleCheck } from '@element-plus/icons-vue'
import { useDeviceType } from '@/utils/device'

const route = useRoute()
const router = useRouter()
const { isMobileScreen } = useDeviceType()

const orderId = computed(() => route.query.orderId || '')
const amount = computed(() => route.query.amount || '0')

const goToOrders = () => {
  router.push('/user/orders')
}

const goHome = () => {
  router.push('/neo/home')
}
</script>

<style scoped>
.animate-bounce {
  animation: bounce 1s ease-in-out;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}
</style>
