<template>
  <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
    <div class="px-5 py-5 bg-gray-50">
      <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ $t('neo.pay.successTitle') }}</div>
      <div class="mt-1 text-[12px] text-gray-500">{{ $t('neo.pay.successDesc') }}</div>
    </div>

    <div class="px-6 py-10">
      <div class="mx-auto max-w-[560px] text-center">
        <div class="neo-result-badge">{{ $t('neo.pay.successBadge') }}</div>
        <div class="mt-4 text-[13px] text-gray-600">
          {{ $t('neo.pay.orderCount') }}: <span class="font-semibold text-gray-900">{{ orderIds.length }}</span>
        </div>
        <div class="mt-7 grid grid-cols-1 md:grid-cols-2 gap-3">
          <el-button type="primary" size="large" @click="router.push('/neo/orders/pending-receive')">
            {{ $t('neo.pay.toOrders') }}
          </el-button>
          <el-button size="large" @click="router.push('/neo/home')">
            {{ $t('neo.pay.backHome') }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()
const route = useRoute()

const orderIds = computed(() => {
  const raw = String(route.query?.orderIds || '').trim()
  return raw ? raw.split(',').map((x) => Number(x)).filter(Boolean) : []
})
</script>

<style scoped>
.neo-result-badge {
  display: inline-flex;
  padding: 10px 18px;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 900;
  color: white;
  background: linear-gradient(135deg, rgba(var(--primary-rgb), 0.95), rgba(var(--secondary-rgb), 0.9));
}
</style>

