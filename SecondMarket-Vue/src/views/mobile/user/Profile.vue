<template>
  <div class="px-3 py-4 space-y-5">
    <div class="rounded-2xl overflow-hidden border border-gray-100 shadow-sm bg-white">
      <div class="px-4 pt-6 pb-5 bg-gradient-to-br text-white" :style="{ background: 'linear-gradient(135deg, rgba(6,182,212,1) 0%, rgba(139,92,246,0.92) 100%)' }">
        <div class="flex flex-col items-center">
          <button class="relative" @click="showAvatarDialog = true">
            <el-avatar :size="84" :src="avatarUrl" class="ring-4 ring-white/40">
              <el-icon :size="40"><User /></el-icon>
            </el-avatar>
            <div class="absolute -bottom-2 -right-2 h-9 w-9 rounded-full bg-white sm-text-primary flex items-center justify-center shadow">
              <el-icon :size="18"><Camera /></el-icon>
            </div>
          </button>

          <div class="mt-4 text-lg font-semibold">
            {{ displayName }}
          </div>
          <div class="mt-1 text-xs text-white/85">
            UID: {{ displayUid }}
          </div>

          <div class="mt-4 w-full grid grid-cols-3 gap-3">
            <div class="rounded-2xl bg-white/20 px-3 py-3 text-center">
              <div class="text-xl font-bold">{{ statistics.creditScore || 0 }}</div>
              <div class="text-xs text-white/85 mt-1">{{ $t('profile.myCredit') }}</div>
            </div>
            <div class="rounded-2xl bg-white/20 px-3 py-3 text-center">
              <div class="text-xl font-bold">{{ statistics.buyCount || 0 }}</div>
              <div class="text-xs text-white/85 mt-1">{{ $t('nav.myOrders') }}</div>
            </div>
            <div class="rounded-2xl bg-white/20 px-3 py-3 text-center">
              <div class="text-xl font-bold">{{ statistics.favoriteCount || 0 }}</div>
              <div class="text-xs text-white/85 mt-1">{{ $t('common.myFavorites') }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="px-4 py-4 grid grid-cols-3 gap-3">
        <button class="rounded-2xl bg-gray-50 border border-gray-100 px-3 py-4 text-center active:scale-[0.99] transition" @click="router.push('/user/my-products')">
          <div class="text-lg font-bold text-gray-900">{{ statistics.productCount || 0 }}</div>
          <div class="text-xs text-gray-500 mt-1">{{ $t('nav.myProducts') }}</div>
        </button>
        <button class="rounded-2xl bg-gray-50 border border-gray-100 px-3 py-4 text-center active:scale-[0.99] transition" @click="router.push('/user/orders')">
          <div class="text-lg font-bold text-gray-900">{{ statistics.buyCount || 0 }}</div>
          <div class="text-xs text-gray-500 mt-1">{{ $t('nav.myOrders') }}</div>
        </button>
        <button class="rounded-2xl bg-gray-50 border border-gray-100 px-3 py-4 text-center active:scale-[0.99] transition" @click="router.push('/user/favorites')">
          <div class="text-lg font-bold text-gray-900">{{ statistics.favoriteCount || 0 }}</div>
          <div class="text-xs text-gray-500 mt-1">{{ $t('common.myFavorites') }}</div>
        </button>
      </div>
    </div>

    <div class="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
      <div class="px-4 py-3 text-sm font-semibold text-gray-900 bg-gray-50">
        {{ $t('common.userCenter') }}
      </div>

      <button class="w-full px-4 py-4 flex items-center justify-between active:bg-gray-50 transition" @click="router.push('/user/orders')">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center">
            <el-icon :size="20"><ShoppingCart /></el-icon>
          </div>
          <div class="text-sm font-semibold text-gray-900">{{ $t('nav.myOrders') }}</div>
        </div>
        <el-icon class="text-gray-300" :size="18"><ArrowRight /></el-icon>
      </button>

      <button class="w-full px-4 py-4 flex items-center justify-between active:bg-gray-50 transition" @click="router.push('/user/favorites')">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center">
            <el-icon :size="20"><Star /></el-icon>
          </div>
          <div class="text-sm font-semibold text-gray-900">{{ $t('common.myFavorites') }}</div>
        </div>
        <el-icon class="text-gray-300" :size="18"><ArrowRight /></el-icon>
      </button>

      <button class="w-full px-4 py-4 flex items-center justify-between active:bg-gray-50 transition" @click="router.push('/user/messages')">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center">
            <el-icon :size="20"><Bell /></el-icon>
          </div>
          <div class="text-sm font-semibold text-gray-900">{{ $t('nav.messages') }}</div>
        </div>
        <el-icon class="text-gray-300" :size="18"><ArrowRight /></el-icon>
      </button>

      <button class="w-full px-4 py-4 flex items-center justify-between active:bg-gray-50 transition" @click="router.push('/user/notices')">
        <div class="flex items-center gap-3">
          <div class="h-10 w-10 rounded-2xl sm-bg-primary-soft sm-text-primary flex items-center justify-center">
            <el-icon :size="20"><Notification /></el-icon>
          </div>
          <div class="text-sm font-semibold text-gray-900">{{ $t('nav.notices') }}</div>
        </div>
        <el-icon class="text-gray-300" :size="18"><ArrowRight /></el-icon>
      </button>

    </div>

    <el-button type="danger" class="w-full" @click="handleLogout">
      {{ $t('common.logout') }}
    </el-button>
  </div>

  <el-dialog v-model="showAvatarDialog" :title="$t('profile.changeAvatar')" width="92%" :close-on-click-modal="false">
    <div class="py-4 flex justify-center">
      <AvatarUpload :model-value="avatarUrl" :size="180" @success="handleAvatarSuccess" />
    </div>
  </el-dialog>

</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { userStatisticsApi } from '@/api/statistics'
import { formatAvatarUrl } from '@/utils/url'
import AvatarUpload from '@/components/AvatarUpload.vue'
import {
  User,
  Camera,
  ShoppingCart,
  Star,
  Bell,
  Notification,
  ArrowRight,
} from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()

const showAvatarDialog = ref(false)

const userInfo = ref({})
const statistics = ref({})

const avatarUrl = computed(() => formatAvatarUrl(userInfo.value.avatar))
const displayName = computed(() => userInfo.value.nickname || userInfo.value.username || userInfo.value.uid || '-')
const displayUid = computed(() => userInfo.value.uid || userInfo.value.username || '-')


const loadUserInfo = async () => {
  const ok = await userStore.getUserInfo()
  if (ok.success) {
    userInfo.value = userStore.user || {}
    return
  }
  const res = await userApi.getCurrentUser()
  if (res.code === '200' || res.success) {
    userInfo.value = res.data || {}
    userStore.setUser(res.data)
  }
}

const loadStatistics = async () => {
  const res = await userStatisticsApi.getOverview()
  if (res.code === '200') {
    statistics.value = res.data || {}
  }
}

const handleAvatarSuccess = async () => {
  showAvatarDialog.value = false
  await loadUserInfo()
}

const handleLogout = async () => {
  userStore.logout()
  await router.replace('/login')
}

onMounted(() => {
  loadUserInfo()
  loadStatistics()
})
</script>
