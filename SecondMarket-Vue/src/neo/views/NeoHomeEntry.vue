<template>
  <NeoHomeMobile v-if="isMobileScreen" />
  <NeoHomeTablet v-else-if="isTabletScreen" />
  <NeoHomeDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import NeoHomeDesktop from '@/neo/views/desktop/NeoHomeDesktop.vue'
import NeoHomeTablet from '@/neo/views/tablet/NeoHomeTablet.vue'
import NeoHomeMobile from '@/neo/views/mobile/NeoHomeMobile.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>

