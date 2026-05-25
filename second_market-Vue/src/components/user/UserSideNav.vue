<template>
  <aside class="rounded-2xl bg-white/90 backdrop-blur border border-gray-100 shadow-sm">
    <div class="px-4 py-4 flex items-center gap-3">
      <el-avatar :size="36" :src="avatarUrl">
        <el-icon><User /></el-icon>
      </el-avatar>
      <div class="min-w-0">
        <div class="text-sm font-semibold text-gray-900 truncate">
          {{ userStore.user?.nickname || userStore.user?.username || '-' }}
        </div>
        <div class="text-xs text-gray-500 truncate">
          {{ $t('common.userCenter') }}
        </div>
      </div>
    </div>

    <div class="px-2 pb-3">
      <button
        v-for="item in navItems"
        :key="item.path"
        class="w-full flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm transition"
        :class="isActive(item.path) ? 'sm-bg-primary-soft text-[color:var(--primary-color)]' : 'text-gray-700 hover:bg-gray-50'"
        @click="router.push(item.path)"
      >
        <el-icon :size="18"><component :is="item.icon" /></el-icon>
        <span class="truncate">{{ item.label }}</span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { User } from '@element-plus/icons-vue'
import IconHome from '@/components/icons/IconHome.vue'
import IconCategory from '@/components/icons/IconCategory.vue'
import IconHeart from '@/components/icons/IconHeart.vue'
import IconBag from '@/components/icons/IconBag.vue'
import IconChat from '@/components/icons/IconChat.vue'
import IconBell from '@/components/icons/IconBell.vue'
import IconUser from '@/components/icons/IconUser.vue'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const userStore = useUserStore()

const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

const navItems = computed(() => [
  { path: '/user/dashboard', label: t('nav.home'), icon: IconHome },
  { path: '/user/categories', label: t('nav.categories'), icon: IconCategory },
  { path: '/user/products', label: t('nav.products'), icon: IconCategory },
  { path: '/user/favorites', label: t('common.myFavorites'), icon: IconHeart },
  { path: '/user/orders', label: t('nav.myOrders'), icon: IconBag },
  { path: '/user/messages', label: t('nav.messages'), icon: IconChat },
  { path: '/user/notices', label: t('nav.notices'), icon: IconBell },
  { path: '/user/profile', label: t('nav.myAccount'), icon: IconUser }
])

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>
