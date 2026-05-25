<template>
  <!-- 个人中心页面容器 -->
  <div class="profile-container">
    <!-- 移动端视图 -->
    <template v-if="isMobileScreen">
      <div class="profile-mobile-page min-h-screen bg-gray-50 pb-24 w-full overflow-x-hidden font-sans">
        <div class="bg-gradient-to-br from-primary to-primaryDark pt-10 pb-24 px-8 rounded-b-[3rem] shadow-lg relative">
          <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
          <div class="flex items-center space-x-5 relative z-10 text-white">
            <van-uploader :after-read="afterRead" class="block">
              <div class="relative w-20 h-20">
                <div class="w-20 h-20 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner overflow-hidden">
                  <div class="w-full h-full rounded-full bg-white overflow-hidden border border-white/20">
                    <img v-if="userInfo.avatar || userInfo.headImg" :src="formatAvatarUrl(userInfo.avatar || userInfo.headImg)" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400 text-2xl font-bold">
                      {{ (userInfo.username || 'U').charAt(0).toUpperCase() }}
                    </div>
                  </div>
                </div>
                <div class="absolute bottom-0 right-0 w-7 h-7 bg-white rounded-full shadow-md flex items-center justify-center border border-gray-100 ring-2 ring-white/10 active:scale-90 transition-transform" @click.stop="router.push('/user/profileEdit')">
                  <el-icon class="text-primary" :size="14"><Edit /></el-icon>
                </div>
              </div>
            </van-uploader>
            <div class="flex-1">
              <h2 class="text-2xl font-bold tracking-tight">{{ userInfo.nickname || userInfo.username || $t('profilePage.newUser') }}</h2>
              <div class="flex flex-wrap items-center gap-2 mt-2">
                <div class="flex items-center bg-white/15 px-3 py-0.5 rounded-full backdrop-blur-md border border-white/10 text-[10px]">
                  <span>{{ userInfo.schoolName || $t('profilePage.inCampus') }}</span>
                </div>
                <div class="flex items-center bg-white/15 px-3 py-0.5 rounded-full backdrop-blur-md border border-white/10 text-[10px]">
                  <span>{{ $t('profilePage.creditPrefix') }}: {{ statistics.creditScore || 100 }}</span>
                </div>
              </div>
              <p class="text-white/70 text-[10px] mt-2">{{ $t('profilePage.uid') }}: {{ userInfo.uid || userInfo.username }}</p>
            </div>
          </div>
        </div>

        <div class="mx-5 -mt-16 bg-white rounded-2xl shadow-xl p-5 flex justify-around items-center relative z-10 mb-6 border border-white">
          <div class="flex flex-col items-center flex-1 cursor-pointer" @click="router.push('/user/my-products')">
            <span class="font-bold text-xl text-gray-800">{{ statistics.productCount || 0 }}</span>
            <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ $t('profilePage.myPublished') }}</span>
          </div>
          <div class="w-px h-8 bg-gray-100"></div>
          <div class="flex flex-col items-center flex-1 cursor-pointer" @click="router.push('/user/orders')">
            <span class="font-bold text-xl text-gray-800">{{ statistics.buyCount || 0 }}</span>
            <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ $t('profilePage.myBought') }}</span>
          </div>
          <div class="w-px h-8 bg-gray-100"></div>
          <div class="flex flex-col items-center flex-1 cursor-pointer" @click="router.push('/user/orders')">
            <span class="font-bold text-xl text-gray-800">{{ statistics.soldCount || 0 }}</span>
            <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ $t('profilePage.mySold') }}</span>
          </div>
        </div>

        <div class="mx-5 space-y-3">
          <div class="bg-white rounded-2xl shadow-sm overflow-hidden border border-gray-100">
            <div v-for="item in menuItems" :key="item.path" class="flex items-center p-4 active:bg-gray-50 border-b border-gray-50 last:border-0" @click="router.push(item.path)">
              <div :class="['w-10 h-10 rounded-xl flex items-center justify-center mr-4', item.bgClass, item.textClass]">
                <el-icon :size="20"><component :is="item.icon" /></el-icon>
              </div>
              <span class="flex-1 text-sm font-medium text-gray-700">{{ item.label }}</span>
              <el-icon class="text-gray-300"><ArrowRight /></el-icon>
            </div>
          </div>
          <button class="w-full py-4 bg-white rounded-2xl text-red-500 font-bold shadow-sm active:scale-[0.98] transition-all" @click="handleLogout">{{ $t('profilePage.logout') }}</button>
        </div>
      </div>
    </template>

    <!-- 桌面端视图 -->
    <template v-else>
      <div class="profile-page p-6 max-w-7xl mx-auto pb-24">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <el-card class="info-card col-span-1">
            <div class="flex flex-col items-center p-6">
              <el-avatar :size="120" :src="formatAvatarUrl(userInfo.avatar || userInfo.headImg)">
                <el-icon :size="60"><User /></el-icon>
              </el-avatar>
              <h2 class="mt-4 text-xl font-bold">{{ userInfo.nickname || userInfo.username }}</h2>
              <div class="mt-6 w-full space-y-4">
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">{{ $t('profilePage.uid') }}</span>
                  <span>{{ userInfo.uid || userInfo.username }}</span>
                </div>
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">{{ $t('profilePage.desktop.school') }}</span>
                  <span>{{ userInfo.schoolName || $t('profilePage.inCampus') }}</span>
                </div>
                <div class="flex justify-between text-sm">
                  <span class="text-gray-500">{{ $t('profilePage.desktop.registerTime') }}</span>
                  <span>{{ new Date(userInfo.createTime).toLocaleDateString() }}</span>
                </div>
              </div>
              <el-button type="primary" class="w-full mt-8" @click="router.push('/user/profileEdit')">{{ $t('profilePage.editProfile') }}</el-button>
              <el-button class="w-full mt-2" @click="handleLogout">{{ $t('profilePage.logout') }}</el-button>
            </div>
          </el-card>

          <div class="md:col-span-2 space-y-6">
            <div class="grid grid-cols-4 gap-6">
              <el-card v-for="stat in desktopStats" :key="stat.label" shadow="hover" class="text-center cursor-pointer" @click="router.push(stat.path)">
                <div class="text-2xl font-bold text-primary">{{ stat.value }}</div>
                <div class="text-xs text-gray-500 mt-1">{{ stat.label }}</div>
              </el-card>
            </div>
            <el-card :header="$t('profilePage.desktop.tradeTrend')">
              <div class="h-64 flex items-center justify-center text-gray-400">
                {{ $t('profilePage.desktop.noTrendData') }}
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useDeviceType } from '@/utils/device'
import { formatAvatarUrl } from '@/utils/url'
import request from '@/api/request'
import { uploadAvatarApi } from '@/api/file'
import { userApi } from '@/api/user'
import { 
  User, Edit, ArrowRight, Box, ShoppingBag, 
  Star, MapLocation, List, Bell, Setting, Clock, Stamp, Odometer
} from '@element-plus/icons-vue'
import { showToast } from 'vant'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const userStore = useUserStore()
const { isMobileScreen } = useDeviceType()
const { t } = useI18n()

const userInfo = ref({})
const statistics = ref({
  productCount: 0,
  buyCount: 0,
  soldCount: 0,
  favoriteCount: 0,
  creditScore: 100
})

const menuItems = computed(() => [
  { label: t('profilePage.menu.profile'), icon: User, path: '/user/profileEdit', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.myPublish'), icon: Box, path: '/user/my-publish', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.favorites'), icon: Star, path: '/user/favorites', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.history'), icon: Clock, path: '/user/recently-viewed', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.cart'), icon: ShoppingBag, path: '/user/cart', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.orders'), icon: List, path: '/user/orders', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.errandOrders'), icon: List, path: '/user/errand/orders', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.address'), icon: MapLocation, path: '/user/address', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.verify'), icon: Stamp, path: '/user/verify', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.credit'), icon: Odometer, path: '/user/credit', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.settings'), icon: Setting, path: '/user/settings', bgClass: 'bg-primary/10', textClass: 'text-primary' },
  { label: t('profilePage.menu.notices'), icon: Bell, path: '/user/notices', bgClass: 'bg-primary/10', textClass: 'text-primary' }
])

const desktopStats = computed(() => [
  { label: t('profilePage.myPublished'), value: statistics.value.productCount || 0, path: '/user/my-products' },
  { label: t('profilePage.myBought'), value: statistics.value.buyCount || 0, path: '/user/orders' },
  { label: t('profilePage.mySold'), value: statistics.value.soldCount || 0, path: '/user/orders' },
  { label: t('profilePage.menu.favorites'), value: statistics.value.favoriteCount || 0, path: '/user/favorites' }
])

const loadData = async () => {
  try {
    const [userRes, statsRes] = await Promise.all([
      request.get('/user/info'),
      request.get('/user/statistics/overview')
    ])
    if (userRes.code === '200') userInfo.value = userRes.data
    if (statsRes.code === '200') statistics.value = statsRes.data
  } catch (error) {
    console.error('加载失败:', error)
  }
}

const afterRead = async (file) => {
  try {
    const res = await uploadAvatarApi(file.file)
    if (res.code !== '200') return
    const uploaded = res.data || {}
    const avatar =
      uploaded.fileUrl ||
      uploaded.filePath ||
      uploaded.url ||
      uploaded.path ||
      (typeof uploaded === 'string' ? uploaded : '')
    if (!avatar) return
    const updateRes = await userApi.updateAvatar(avatar)
    if (updateRes.code === '200') {
      userInfo.value.avatar = avatar
      showToast(t('profilePage.toast.avatarUpdated'))
    }
  } catch (error) {
    showToast(t('profilePage.toast.uploadFail'))
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => loadData())
</script>

<style scoped>
.profile-mobile-page {
  -webkit-font-smoothing: antialiased;
}
</style>
