<template>
  <div class="pay-select-container">
    <!-- 移动端视图 -->
    <div v-if="isMobileScreen" class="pay-select-mobile min-h-screen bg-gray-50 pb-28">
      <!-- 顶部导航 -->
      <div class="sticky top-0 z-50 bg-white px-4 py-3 flex items-center justify-between border-b border-gray-100 shadow-sm">
        <div class="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center cursor-pointer hover:bg-gray-200 transition" @click="router.back()">
          <el-icon :size="20"><ArrowLeft /></el-icon>
        </div>
        <span class="text-base font-bold text-gray-900">选择支付方式</span>
        <div class="w-10"></div>
      </div>

      <!-- 订单金额 -->
      <div class="bg-white p-6 text-center">
        <p class="text-sm text-gray-500 mb-2">支付金额</p>
        <p class="text-4xl font-black text-primary">¥{{ amount }}</p>
        <p class="text-sm text-gray-400 mt-2">订单号：{{ orderId }}</p>
      </div>

      <!-- 支付方式 -->
      <div class="mt-4 p-4 space-y-3">
        <div class="bg-white rounded-2xl p-4 shadow-sm">
          <h3 class="font-bold text-gray-900 mb-4">选择支付方式</h3>
          
          <div class="space-y-3">
            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'alipay' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'alipay'">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-blue-500 flex items-center justify-center">
                  <span class="text-white font-bold text-lg">支</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">支付宝</p>
                  <p class="text-xs text-gray-500">推荐</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="alipay">
                <span v-if="selectedMethod === 'alipay'"></span>
              </el-radio>
            </div>

            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'wechat' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'wechat'">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-green-500 flex items-center justify-center">
                  <span class="text-white font-bold text-lg">微</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">微信支付</p>
                  <p class="text-xs text-gray-500">无需开通</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="wechat">
                <span v-if="selectedMethod === 'wechat'"></span>
              </el-radio>
            </div>

            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'balance' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'balance'">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-orange-500 flex items-center justify-center">
                  <span class="text-white font-bold text-lg">余额</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">余额支付</p>
                  <p class="text-xs text-gray-500">可用余额：¥{{ userBalance }}</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="balance">
                <span v-if="selectedMethod === 'balance'"></span>
              </el-radio>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部提交 -->
      <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 px-4 py-4">
        <el-button type="primary" class="w-full !h-12 !rounded-full !font-bold" :loading="paying" @click="handlePay">
          确认支付 ¥{{ amount }}
        </el-button>
      </div>
    </div>

    <!-- 桌面端视图 -->
    <div v-else class="pay-select-desktop bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen">
      <div class="max-w-lg mx-auto p-8">
        <div class="bg-white rounded-3xl shadow-xl p-8">
          <h2 class="text-2xl font-bold text-gray-900 text-center mb-6">选择支付方式</h2>
          
          <!-- 金额展示 -->
          <div class="text-center mb-8 p-6 bg-gray-50 rounded-2xl">
            <p class="text-sm text-gray-500 mb-2">支付金额</p>
            <p class="text-4xl font-black text-primary">¥{{ amount }}</p>
            <p class="text-sm text-gray-400 mt-2">订单号：{{ orderId }}</p>
          </div>

          <!-- 支付方式 -->
          <div class="space-y-4">
            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'alipay' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'alipay'">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-xl bg-blue-500 flex items-center justify-center">
                  <span class="text-white font-bold text-xl">支</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">支付宝</p>
                  <p class="text-sm text-gray-500">推荐</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="alipay" />
            </div>

            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'wechat' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'wechat'">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-xl bg-green-500 flex items-center justify-center">
                  <span class="text-white font-bold text-xl">微</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">微信支付</p>
                  <p class="text-sm text-gray-500">无需开通</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="wechat" />
            </div>

            <div class="flex items-center justify-between p-4 border-2 rounded-xl cursor-pointer transition-all"
                 :class="selectedMethod === 'balance' ? 'border-primary bg-primary/5' : 'border-gray-100 hover:border-gray-200'"
                 @click="selectedMethod = 'balance'">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-xl bg-orange-500 flex items-center justify-center">
                  <span class="text-white font-bold text-xl">余额</span>
                </div>
                <div>
                  <p class="font-bold text-gray-900">余额支付</p>
                  <p class="text-sm text-gray-500">可用余额：¥{{ userBalance }}</p>
                </div>
              </div>
              <el-radio v-model="selectedMethod" value="balance" />
            </div>
          </div>

          <!-- 提交按钮 -->
          <el-button type="primary" class="w-full !h-14 !rounded-full !text-lg !font-bold mt-8" :loading="paying" @click="handlePay">
            确认支付 ¥{{ amount }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api/order'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useDeviceType } from '@/utils/device'

const route = useRoute()
const router = useRouter()
const { isMobileScreen } = useDeviceType()
const userStore = useUserStore()

const selectedMethod = ref('alipay')
const paying = ref(false)

const orderId = computed(() => route.query.orderId || '')
const amount = computed(() => route.query.amount || '0')
const userBalance = computed(() => userStore.user?.balance || 0)

const handlePay = async () => {
  paying.value = true
  try {
    const res = await orderApi.payOrder(orderId.value)
    if (res.code === '200') {
      router.replace({
        path: '/neo/pay-center/success',
        query: { orderId: orderId.value, amount: amount.value }
      })
    } else {
      router.replace({
        path: '/neo/pay-center/fail',
        query: { orderId: orderId.value, reason: res.message }
      })
    }
  } catch (e) {
    router.replace({
      path: '/neo/pay-center/fail',
      query: { orderId: orderId.value, reason: '支付失败，请稍后重试' }
    })
  } finally {
    paying.value = false
  }
}
</script>
