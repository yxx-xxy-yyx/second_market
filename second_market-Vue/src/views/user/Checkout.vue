<template>
  <div class="checkout-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-gray-50 pb-24">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
            <div class="text-base font-bold text-gray-900">确认订单</div>
          </div>
        </div>

        <div class="px-3 py-4 space-y-3">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
            <div class="text-sm font-semibold text-gray-900 mb-3">商品清单</div>
            <div v-for="i in cart.selectedItems" :key="i.productId" class="flex gap-3 py-3 border-b border-gray-50 last:border-0">
              <div class="w-16 h-16 rounded-xl bg-gray-50 overflow-hidden flex-shrink-0 cursor-pointer" @click="router.push(`/user/product/${i.productId}`)">
                <img :src="i.image" class="w-full h-full object-cover" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ i.title }}</div>
                <div class="mt-2 flex items-center justify-between">
                  <div class="text-base font-black text-primary">¥{{ i.price }}</div>
                  <div v-if="i.conditionScore" class="text-[11px] text-gray-400">成色 {{ i.conditionScore }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
            <div class="text-sm font-semibold text-gray-900 mb-2">备注</div>
            <el-input v-model="remark" type="textarea" :rows="3" placeholder="给卖家留言（可选）" />
          </div>
        </div>

        <div class="fixed left-0 right-0 bottom-0 bg-white border-t border-gray-100 px-4 py-3 flex items-center gap-3 z-50">
          <div class="flex-1">
            <div class="text-xs text-gray-500">合计</div>
            <div class="text-lg font-black text-primary">¥{{ cart.selectedTotal.toFixed(2) }}</div>
          </div>
          <el-button type="primary" class="!rounded-full !h-10 !px-6" :loading="submitting" @click="submitOrder">
            支付（模拟）
          </el-button>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="p-6 max-w-4xl mx-auto pb-24 space-y-6">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold">确认订单</h1>
          <div class="text-sm text-gray-500">已选 {{ cart.selectedCount }} 件</div>
        </div>

        <el-card shadow="never">
          <template #header>
            <div class="font-bold">商品清单</div>
          </template>
          <div class="space-y-4">
            <div v-for="i in cart.selectedItems" :key="i.productId" class="flex items-center gap-4">
              <el-image :src="i.image" fit="cover" class="w-20 h-20 rounded-lg cursor-pointer" @click="router.push(`/user/product/${i.productId}`)" />
              <div class="flex-1 min-w-0">
                <div class="font-semibold truncate">{{ i.title }}</div>
                <div class="mt-2 text-primary font-bold">¥{{ i.price }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div class="font-bold">备注</div>
          </template>
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="给卖家留言（可选）" />
        </el-card>

        <div class="flex items-center justify-end gap-4">
          <div class="text-right">
            <div class="text-xs text-gray-500">合计</div>
            <div class="text-2xl font-black text-primary">¥{{ cart.selectedTotal.toFixed(2) }}</div>
          </div>
          <el-button type="primary" :loading="submitting" @click="submitOrder">支付（模拟）</el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { orderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const cart = useCartStore()
const userStore = useUserStore()

const remark = ref('')
const submitting = ref(false)

const submitOrder = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (cart.selectedCount === 0) return

  try {
    await ElMessageBox.confirm('确认使用模拟支付提交订单吗？', '确认支付', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
  } catch {
    return
  }

  submitting.value = true
  const createdOrderIds = []
  try {
    for (const i of cart.selectedItems) {
      const createRes = await orderApi.createOrder({ productId: i.productId, remark: remark.value })
      if (createRes.code !== '200' || !createRes.data?.id) {
        throw new Error(createRes.message || '创建订单失败')
      }
      createdOrderIds.push(createRes.data.id)
    }

    for (const id of createdOrderIds) {
      const payRes = await orderApi.payOrder(id)
      if (payRes.code !== '200') {
        throw new Error(payRes.message || '支付失败')
      }
    }

    for (const i of cart.selectedItems) {
      cart.removeItem(i.productId)
    }

    ElMessage.success('支付成功，订单已生成')
    router.replace('/user/orders')
  } catch (e) {
    ElMessage.error(e?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

