<template>
  <FavoritesMobile v-if="isMobileScreen" />
  <FavoritesTablet v-else-if="isTabletScreen" />
  <FavoritesDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import FavoritesDesktop from '@/views/user/Favorites.vue'
import FavoritesTablet from '@/views/tablet/user/Favorites.vue'
import FavoritesMobile from '@/views/mobile/user/Favorites.vue'

const { isMobileScreen, isTabletScreen } = useDeviceType()
</script>
