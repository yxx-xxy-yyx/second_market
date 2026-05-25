<template>
  <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
    <div class="px-5 py-5 bg-gray-50">
      <div class="text-[16px] md:text-[18px] font-semibold text-gray-900">{{ title }}</div>
      <div class="mt-2 text-[12px] md:text-[13px] text-gray-500">{{ $t('neo.placeholder.desc') }}</div>
    </div>

    <div v-if="isMobileScreen" class="px-5 py-5 space-y-4">
      <div class="grid grid-cols-2 gap-3">
        <button class="neo-entry" @click="router.push('/neo/home')">{{ $t('neo.tabs.home') }}</button>
        <button class="neo-entry" @click="router.push('/neo/cart')">{{ $t('neo.tabs.cart') }}</button>
        <button class="neo-entry" @click="router.push('/neo/orders/pending-pay')">{{ $t('neo.home.orders') }}</button>
        <button class="neo-entry" @click="router.push('/user/dashboard')">{{ $t('neo.placeholder.back') }}</button>
      </div>
      <div class="rounded-2xl border border-dashed border-gray-200 bg-white px-4 py-5 text-center text-[12px] text-gray-500">
        {{ $t('neo.placeholder.hint') }}
      </div>
    </div>

    <div v-else-if="isTabletScreen" class="px-6 py-6 grid grid-cols-[1fr_280px] gap-5">
      <div class="rounded-2xl border border-dashed border-gray-200 bg-white px-5 py-6 text-[13px] text-gray-600 leading-relaxed">
        {{ $t('neo.placeholder.hint') }}
      </div>
      <div class="space-y-3">
        <button class="neo-entry w-full" @click="router.push('/neo/home')">{{ $t('neo.tabs.home') }}</button>
        <button class="neo-entry w-full" @click="router.push('/neo/cart')">{{ $t('neo.tabs.cart') }}</button>
        <button class="neo-entry w-full" @click="router.push('/neo/orders/pending-pay')">{{ $t('neo.home.orders') }}</button>
        <button class="neo-entry w-full" @click="router.push('/user/dashboard')">{{ $t('neo.placeholder.back') }}</button>
      </div>
    </div>

    <div v-else class="px-6 py-6 grid grid-cols-[1fr_320px] gap-6">
      <div class="rounded-2xl border border-dashed border-gray-200 bg-white px-6 py-7 text-[13px] text-gray-600 leading-relaxed">
        {{ $t('neo.placeholder.hint') }}
      </div>
      <div class="space-y-3">
        <button class="neo-entry w-full" @click="router.push('/neo/home')">{{ $t('neo.tabs.home') }}</button>
        <button class="neo-entry w-full" @click="router.push('/neo/cart')">{{ $t('neo.tabs.cart') }}</button>
        <button class="neo-entry w-full" @click="router.push('/neo/orders/pending-pay')">{{ $t('neo.home.orders') }}</button>
        <button class="neo-entry w-full" @click="router.push('/user/dashboard')">{{ $t('neo.placeholder.back') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'

const router = useRouter()
const route = useRoute()
const { isMobileScreen, isTabletScreen } = useDeviceType()

const title = computed(() => route.meta?.title || 'Neo')
</script>

<style scoped>
.neo-entry {
  height: 44px;
  border-radius: 14px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(248, 250, 252, 0.8);
  font-size: 13px;
  font-weight: 700;
  color: #0f172a;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.06s ease;
}

.neo-entry:active {
  transform: scale(0.99);
}

.neo-entry:hover {
  border-color: rgba(var(--primary-rgb), 0.35);
  background: rgba(var(--primary-rgb), 0.06);
}
</style>

