<template>
  <div class="pay-countdown-container">
    <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div class="bg-white rounded-3xl shadow-2xl p-8 text-center max-w-md w-full">
        <div class="w-20 h-20 rounded-full bg-orange-100 flex items-center justify-center mx-auto mb-6">
          <el-icon :size="40" class="text-orange-500"><Clock /></el-icon>
        </div>
        <h2 class="text-2xl font-bold text-gray-900 mb-4">订单支付</h2>
        <p class="text-gray-500 mb-6">请在规定时间内完成支付</p>
        
        <div class="bg-gray-50 rounded-2xl p-6 mb-6">
          <p class="text-sm text-gray-500 mb-2">剩余时间</p>
          <div class="flex items-center justify-center gap-4">
            <div class="text-center">
              <div class="text-4xl font-black text-primary">{{ minutes }}</div>
              <div class="text-xs text-gray-500 mt-1">分钟</div>
            </div>
            <span class="text-3xl font-bold text-gray-300">:</span>
            <div class="text-center">
              <div class="text-4xl font-black text-primary">{{ seconds }}</div>
              <div class="text-xs text-gray-500 mt-1">秒</div>
            </div>
          </div>
        </div>

        <p class="text-sm text-gray-400 mb-6">支付完成后，订单将自动发货</p>

        <el-button type="primary" class="w-full !h-12 !rounded-full !font-bold" @click="router.push('/neo/pay-center')">
          继续支付
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Clock } from '@element-plus/icons-vue'

const router = useRouter()

const initialMinutes = 30
const initialSeconds = 0
const remainingSeconds = ref(initialMinutes * 60 + initialSeconds)
let timer = null

const minutes = computed(() => Math.floor(remainingSeconds.value / 60))
const seconds = computed(() => remainingSeconds.value % 60)

onMounted(() => {
  timer = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--
    } else {
      clearInterval(timer)
      router.replace('/neo/pay-center/fail?reason=支付超时')
    }
  }, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>
