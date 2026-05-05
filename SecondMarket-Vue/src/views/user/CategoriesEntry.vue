<template>
  <CategoriesMobile v-if="isMobileScreen || isTabletScreen" />
  <CategoriesDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import CategoriesDesktop from '@/views/user/Categories.vue'
import CategoriesMobile from '@/views/mobile/user/Categories.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
