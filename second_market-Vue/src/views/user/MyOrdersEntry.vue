<template>
  <MyOrdersMobile v-if="isMobileScreen" />
  <MyOrdersTablet v-else-if="isTabletScreen" />
  <MyOrdersDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import MyOrdersDesktop from '@/views/user/MyOrders.vue'
import MyOrdersTablet from '@/views/tablet/user/MyOrders.vue'
import MyOrdersMobile from '@/views/mobile/user/MyOrders.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
