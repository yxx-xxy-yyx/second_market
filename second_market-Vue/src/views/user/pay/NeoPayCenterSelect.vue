<template>
  <div class="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-5">
    <section class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
      <div class="px-5 py-5 bg-gray-50 flex items-start justify-between gap-4">
        <div>
          <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.pay.title') }}</div>
          <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.pay.subtitle') }}</div>
        </div>
        <el-button text class="!text-gray-500" @click="router.push('/neo/orders/pending-pay')">
          {{ $t('neo.pay.viewOrders') }}
        </el-button>
      </div>

      <div class="px-5 py-5 space-y-3">
        <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.methods') }}</div>
        <el-radio-group v-model="method" class="w-full">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
            <label class="neo-pay-item">
              <el-radio label="CARD" class="!mr-0">
                <div class="flex items-center gap-3">
                  <div class="neo-pay-icon">C</div>
                  <div>
                    <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.card') }}</div>
                    <div class="text-[12px] text-gray-500">{{ $t('neo.pay.cardDesc') }}</div>
                  </div>
                </div>
              </el-radio>
            </label>
            <label class="neo-pay-item">
              <el-radio label="KAKAO" class="!mr-0">
                <div class="flex items-center gap-3">
                  <div class="neo-pay-icon">K</div>
                  <div>
                    <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.kakao') }}</div>
                    <div class="text-[12px] text-gray-500">{{ $t('neo.pay.kakaoDesc') }}</div>
                  </div>
                </div>
              </el-radio>
            </label>
            <label class="neo-pay-item">
              <el-radio label="NAVER" class="!mr-0">
                <div class="flex items-center gap-3">
                  <div class="neo-pay-icon">N</div>
                  <div>
                    <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.naver') }}</div>
                    <div class="text-[12px] text-gray-500">{{ $t('neo.pay.naverDesc') }}</div>
                  </div>
                </div>
              </el-radio>
            </label>
            <label class="neo-pay-item">
              <el-radio label="WECHAT" class="!mr-0">
                <div class="flex items-center gap-3">
                  <div class="neo-pay-icon">W</div>
                  <div>
                    <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.wechat') }}</div>
                    <div class="text-[12px] text-gray-500">{{ $t('neo.pay.wechatDesc') }}</div>
                  </div>
                </div>
              </el-radio>
            </label>
          </div>
        </el-radio-group>
      </div>
    </section>

    <aside class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden h-fit">
      <div class="px-5 py-5 bg-gray-50">
        <div class="text-[13px] font-semibold text-gray-900">{{ $t('neo.pay.summary') }}</div>
      </div>
      <div class="px-5 py-5 space-y-3">
        <div class="flex items-center justify-between text-[13px] text-gray-600">
          <span>{{ $t('neo.pay.orderCount') }}</span>
          <span class="font-semibold text-gray-900">{{ orderIds.length }}</span>
        </div>
        <div class="flex items-center justify-between text-[13px] text-gray-600">
          <span>{{ $t('neo.pay.amount') }}</span>
          <span class="font-extrabold text-[16px] text-[color:var(--primary-color)]">{{ currency }}{{ amount.toFixed(2) }}</span>
        </div>
        <el-button type="primary" class="w-full" :disabled="!method || orderIds.length === 0" @click="goCountdown">
          {{ $t('neo.pay.next') }}
        </el-button>
        <div class="text-[12px] text-gray-400 leading-relaxed">{{ $t('neo.pay.mockHint') }}</div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'
import { useNeoCheckoutStore } from '@/neo/stores/neoCheckout'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const checkout = useNeoCheckoutStore()

const currency = t('common.currency')
const method = ref(checkout.draft.paymentMethod || 'KAKAO')

const orderIds = computed(() => {
  const fromQuery = String(route.query?.orderIds || '').trim()
  const ids = fromQuery ? fromQuery.split(',').map((x) => Number(x)).filter(Boolean) : []
  return ids
})

const amount = ref(0)

const loadAmount = async () => {
  try {
    const list = await Promise.all(orderIds.value.map((id) => orderApi.getOrderDetail(id)))
    const ok = list.filter((x) => x && (x.code === '200' || x.success)).map((x) => x.data)
    amount.value = ok.reduce((acc, o) => acc + Number(o?.amount || 0), 0)
  } catch {
    amount.value = checkout.amount
  }
}

onMounted(() => {
  if (!orderIds.value.length) {
    ElMessage.warning(t('neo.pay.missingOrder'))
    router.replace('/neo/cart')
    return
  }
  loadAmount()
})

const goCountdown = () => {
  checkout.setPaymentMethod(method.value)
  router.push({ path: '/neo/pay-center/countdown', query: { orderIds: orderIds.value.join(','), method: method.value } })
}
</script>

<style scoped>
.neo-pay-item {
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(248, 250, 252, 0.8);
  padding: 14px 14px;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.06s ease;
}

.neo-pay-item:hover {
  border-color: rgba(var(--primary-rgb), 0.35);
  background: rgba(var(--primary-rgb), 0.06);
}

.neo-pay-icon {
  height: 40px;
  width: 40px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  color: white;
  background: linear-gradient(135deg, rgba(var(--primary-rgb), 0.95), rgba(var(--secondary-rgb), 0.9));
}
</style>

