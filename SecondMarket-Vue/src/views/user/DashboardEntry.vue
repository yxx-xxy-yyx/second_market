<template>
  <DashboardMobile v-if="isMobileScreen" />
  <DashboardTablet v-else-if="isTabletScreen" />
  <DashboardDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import DashboardDesktop from '@/views/user/Dashboard.vue'
import DashboardTablet from '@/views/tablet/user/Dashboard.vue'
import DashboardMobile from '@/views/mobile/user/DashboardV2.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
