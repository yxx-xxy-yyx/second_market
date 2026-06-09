<template>
  <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
    <div class="px-5 py-5 bg-gray-50 flex items-start justify-between gap-4">
      <div>
        <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.pay.countdownTitle') }}</div>
        <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.pay.countdownDesc') }}</div>
      </div>
      <el-button text class="!text-gray-500" @click="router.push({ path: '/neo/pay-center', query: { orderIds: orderIds.join(',') } })">
        {{ $t('neo.pay.back') }}
      </el-button>
    </div>

    <div class="px-6 py-8">
      <div class="mx-auto max-w-[520px] text-center">
        <div class="neo-timer">{{ timeText }}</div>
        <div class="mt-2 text-[12px] text-gray-500">
          {{ $t('neo.pay.methodChosen') }}: <span class="font-semibold text-gray-900">{{ methodLabel }}</span>
        </div>

        <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-3">
          <el-button type="primary" size="large" :loading="processing" @click="paySuccess">
            {{ $t('neo.pay.finishSuccess') }}
          </el-button>
          <el-button size="large" :disabled="processing" @click="payFail">
            {{ $t('neo.pay.simulateFail') }}
          </el-button>
        </div>

        <div class="mt-6 rounded-2xl border border-dashed border-gray-200 bg-white px-4 py-5 text-[12px] text-gray-500 leading-relaxed">
          {{ $t('neo.pay.mockTip') }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { orderApi } from '@/api/order'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const processing = ref(false)

const orderIds = computed(() => {
  const raw = String(route.query?.orderIds || '').trim()
  return raw ? raw.split(',').map((x) => Number(x)).filter(Boolean) : []
})

const method = computed(() => String(route.query?.method || '').trim())

const methodLabel = computed(() => {
  switch (method.value) {
    case 'CARD':
      return t('neo.pay.card')
    case 'KAKAO':
      return t('neo.pay.kakao')
    case 'NAVER':
      return t('neo.pay.naver')
    case 'WECHAT':
      return t('neo.pay.wechat')
    default:
      return method.value || '-'
  }
})

const remain = ref(5 * 60)
let timer = null

const timeText = computed(() => {
  const m = String(Math.floor(remain.value / 60)).padStart(2, '0')
  const s = String(remain.value % 60).padStart(2, '0')
  return `${m}:${s}`
})

const tick = () => {
  if (remain.value <= 0) return
  remain.value -= 1
  if (remain.value <= 0) payFail()
}

const paySuccess = async () => {
  if (!orderIds.value.length) return
  processing.value = true
  try {
    for (const id of orderIds.value) {
      const res = await orderApi.payOrder(id)
      if (res.code !== '200') throw new Error(res.message || t('neo.pay.payFail'))
    }
    router.replace({ path: '/neo/pay-center/success', query: { orderIds: orderIds.value.join(',') } })
  } catch (e) {
    ElMessage.error(e?.message || t('neo.pay.payFail'))
    router.replace({ path: '/neo/pay-center/fail', query: { orderIds: orderIds.value.join(',') } })
  } finally {
    processing.value = false
  }
}

const payFail = () => {
  if (processing.value) return
  router.replace({ path: '/neo/pay-center/fail', query: { orderIds: orderIds.value.join(',') } })
}

onMounted(() => {
  if (!orderIds.value.length) {
    ElMessage.warning(t('neo.pay.missingOrder'))
    router.replace('/neo/cart')
    return
  }
  timer = setInterval(tick, 1000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.neo-timer {
  font-size: 54px;
  font-weight: 900;
  letter-spacing: 1px;
  color: var(--primary-color);
}
</style>

