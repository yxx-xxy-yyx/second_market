<template>
  <ProfileMobile v-if="isMobileScreen" />
  <ProfileTablet v-else-if="isTabletScreen" />
  <ProfileDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import ProfileDesktop from '@/views/user/Profile.vue'
import ProfileTablet from '@/views/tablet/user/Profile.vue'
import ProfileMobile from '@/views/mobile/user/Profile.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
