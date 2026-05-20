<template>
  <div class="min-h-[100dvh] bg-gray-50">
    <UserHeader v-if="isDesktopScreen" />

    <div
      v-else
      class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100"
      :style="{ paddingTop: 'env(safe-area-inset-top)' }"
    >
      <div class="h-[52px] px-3 flex items-center justify-between">
        <button class="flex items-center gap-2 min-w-0" @click="router.push('/user/dashboard')">
          <BrandLogo :title="$t('common.projectName')" class="text-[color:var(--primary-color)]" />
        </button>

        <div class="flex items-center gap-1">
          <el-button text class="min-w-[44px] min-h-[44px]" @click="router.push('/user/products')">
            <el-icon :size="20"><Search /></el-icon>
          </el-button>
          <el-button text class="min-w-[44px] min-h-[44px]" @click="router.push('/user/messages')">
            <el-icon :size="20"><Bell /></el-icon>
          </el-button>
          <el-button text class="min-w-[44px] min-h-[44px]" @click="showQuickPanel = true">
            <el-icon :size="20"><Setting /></el-icon>
          </el-button>
        </div>
      </div>

      <div v-if="isTabletScreen" class="px-3 pb-3 flex items-center gap-2 overflow-x-auto">
        <el-button text class="rounded-xl px-3 min-h-[40px]" @click="router.push('/user/dashboard')">
          {{ $t('nav.home') }}
        </el-button>
        <el-button text class="rounded-xl px-3 min-h-[40px]" @click="router.push('/user/categories')">
          {{ $t('nav.categories') }}
        </el-button>
        <el-button text class="rounded-xl px-3 min-h-[40px]" @click="router.push('/user/publish')">
          {{ $t('common.publish') }}
        </el-button>
        <el-button text class="rounded-xl px-3 min-h-[40px]" @click="router.push('/user/profile')">
          {{ $t('nav.myAccount') }}
        </el-button>
      </div>
    </div>

    <div
      v-if="isDesktopScreen"
      class="mx-auto max-w-[1400px] px-4 py-5 grid grid-cols-[260px_1fr] gap-5"
    >
      <div class="sticky top-[80px] self-start max-h-[calc(100dvh-96px)] overflow-auto">
        <UserSideNav />
      </div>
      <main class="min-w-0">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </main>
    </div>

    <main
      v-else
      class="px-3 py-3"
      :style="isMobileScreen ? { paddingBottom: 'calc(72px + env(safe-area-inset-bottom))' } : {}"
    >
      <router-view v-slot="{ Component, route }">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>

    <UserFooter v-if="isDesktopScreen" />
    <UserBottomNav v-if="isMobileScreen" />
    <AiAssistant />
    <UserQuickPanelDrawer v-model="showQuickPanel" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import UserHeader from '@/components/UserHeader.vue'
import UserFooter from '@/components/UserFooter.vue'
import UserBottomNav from '@/components/UserBottomNav.vue'
import AiAssistant from '@/components/AiAssistant.vue'
import UserSideNav from '@/components/user/UserSideNav.vue'
import UserQuickPanelDrawer from '@/components/user/UserQuickPanelDrawer.vue'
import BrandLogo from '@/components/BrandLogo.vue'
import { useDeviceType } from '@/utils/device'
import { Bell, Search, Setting } from '@element-plus/icons-vue'

const router = useRouter()
const { isMobileScreen, isTabletScreen, isDesktopScreen } = useDeviceType()

const showQuickPanel = ref(false)
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
