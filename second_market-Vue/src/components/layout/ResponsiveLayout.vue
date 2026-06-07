<template>
  <div class="responsive-layout">
    <!-- 桌面端布局 -->
    <div v-if="isDesktop" class="desktop-layout">
      <slot name="desktop" :props="layoutProps" />
    </div>

    <!-- 平板端布局 -->
    <div v-else-if="isTablet" class="tablet-layout">
      <slot name="tablet" :props="layoutProps" />
    </div>

    <!-- 移动端布局 (默认) -->
    <div v-else class="mobile-layout">
      <slot name="mobile" :props="layoutProps" />
    </div>

    <!-- 默认内容区域 (如果没有指定设备插槽) -->
    <div v-if="$slots.default" class="default-content">
      <slot :props="layoutProps" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDeviceType } from '@/composables/useDevice'

const { isMobile, isTablet, isDesktop, screenWidth, screenHeight } = useDeviceType()

const layoutProps = computed(() => ({
  isMobile,
  isTablet,
  isDesktop,
  screenWidth,
  screenHeight
}))
</script>

<style scoped>
.responsive-layout {
  width: 100%;
  min-height: 100vh;
}

.desktop-layout,
.tablet-layout,
.mobile-layout,
.default-content {
  width: 100%;
  height: 100%;
}
</style>
