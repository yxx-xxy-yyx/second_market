<template>
  <LoginSwipe v-if="isMobileScreen" />
  <LoginSwipeTablet v-else-if="isTabletScreen" />
  <LoginDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import LoginDesktop from '@/views/auth/Login.vue'
import LoginSwipe from '@/views/mobile/auth/LoginSwipe.vue'
import LoginSwipeTablet from '@/views/tablet/auth/LoginSwipe.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
