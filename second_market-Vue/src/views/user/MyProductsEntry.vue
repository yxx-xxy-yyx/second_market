<template>
  <MyProductsMobile v-if="isMobileScreen" />
  <MyProductsTablet v-else-if="isTabletScreen" />
  <MyProductsDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import MyProductsDesktop from '@/views/user/MyProducts.vue'
import MyProductsTablet from '@/views/tablet/user/MyProducts.vue'
import MyProductsMobile from '@/views/mobile/user/MyProducts.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
