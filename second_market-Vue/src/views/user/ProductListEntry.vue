<template>
  <ProductListMobile v-if="isMobileScreen || isTabletScreen" />
  <ProductListDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import ProductListDesktop from '@/views/user/ProductList.vue'
import ProductListMobile from '@/views/mobile/user/ProductList.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
