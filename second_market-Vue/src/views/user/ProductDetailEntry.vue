<template>
    <ProductDetailMobile v-if="isMobileScreen" />
    <ProductDetailDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import ProductDetailDesktop from '@/views/user/ProductDetail.vue'
import ProductDetailMobile from '@/views/mobile/user/ProductDetail.vue'

const { isMobileScreen } = useDeviceType()
</script>