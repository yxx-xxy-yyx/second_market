<template>
  <RegisterMobile v-if="isMobileScreen" />
  <RegisterDesktop v-else />
</template>

<script setup>
import { useDeviceType } from '@/utils/device'
import RegisterDesktop from '@/views/auth/Register.vue'
import RegisterMobile from '@/views/mobile/auth/Register.vue'

const { isMobileScreen } = useDeviceType()
</script>
