<template>
  <div class="min-h-[100dvh] bg-gray-50">
    <UserHeader v-if="isDesktopScreen" />

    <!-- 🔴 移除了移动端的导航栏 -->
    <div :class="[
      'mx-auto w-full transition-all duration-300',
      isDesktopScreen ? 'max-w-[1400px] px-4 py-5 grid grid-cols-[260px_1fr] gap-5' : 'px-3 py-3'
    ]" :style="(!isDesktopScreen && !isProductDetailPage && !route.meta.hideNav) ? { paddingBottom: 'calc(80px + env(safe-area-inset-bottom))' } : {}">
      <UserSideNav v-if="isDesktopScreen" />

      <main class="min-w-0">
        <router-view v-slot="{ Component, route }">
          <component :is="Component" :key="route.fullPath" />
        </router-view>
      </main>
    </div>

    <UserFooter v-if="isDesktopScreen" />
    <UserBottomNav v-if="!isDesktopScreen && !isProductDetailPage && !route.meta.hideNav" />

    <AiAssistant />
    <UserQuickPanelDrawer v-model="showQuickPanel" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import UserHeader from '@/components/UserHeader.vue'
import UserFooter from '@/components/UserFooter.vue'
import UserBottomNav from '@/components/UserBottomNav.vue'
import AiAssistant from '@/components/AiAssistant.vue'
import UserSideNav from '@/components/user/UserSideNav.vue'
import UserQuickPanelDrawer from '@/components/user/UserQuickPanelDrawer.vue'
import { useDeviceType } from '@/utils/device'

const router = useRouter()
const route = useRoute()
const { isMobileScreen, isTabletScreen, isDesktopScreen } = useDeviceType()

const isProductDetailPage = computed(() => route.path.includes('/user/product/'))
const showQuickPanel = ref(false)
</script>