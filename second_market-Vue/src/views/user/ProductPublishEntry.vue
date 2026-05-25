<template>
  <ProductPublishMobile v-if="isMobileScreen" />
  <ProductPublishTablet v-else-if="isTabletScreen" />
  <ProductPublishDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import ProductPublishDesktop from '@/views/user/ProductPublish.vue'
import ProductPublishTablet from '@/views/tablet/user/ProductPublish.vue'
import ProductPublishMobile from '@/views/mobile/user/ProductPublish.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
