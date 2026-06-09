<template>
  <!-- 个人中心页面容器 -->
  <div class="profile-container">
    <!-- 移动端视图 -->
    <template v-if="isMobileScreen">
      <div class="profile-mobile-page min-h-screen bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 pb-32 w-full overflow-x-hidden font-sans">
        <div class="bg-gradient-to-br from-primary via-indigo-500 to-purple-600 pt-12 pb-32 px-6 rounded-b-[3.5rem] shadow-2xl relative overflow-hidden">
          <!-- 装饰元素 -->
          <div class="absolute top-0 right-0 w-80 h-80 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
          <div class="absolute bottom-0 left-0 w-64 h-64 bg-white/5 rounded-full -ml-16 -mb-16 blur-2xl"></div>
          <div class="absolute top-1/2 right-1/4 w-24 h-24 bg-gradient-to-br from-yellow-400/20 to-orange-400/10 rounded-full blur-xl"></div>
          
          <div class="flex items-center gap-5 relative z-10 text-white">
            <van-uploader :after-read="afterRead" class="block">
              <div class="relative w-24 h-24">
                <div class="w-24 h-24 rounded-full bg-white/25 backdrop-blur-xl p-1.5 shadow-2xl overflow-hidden ring-4 ring-white/20">
                  <div class="w-full h-full rounded-full bg-gradient-to-br from-gray-50 to-white overflow-hidden border-2 border-white/30">
                    <img v-if="userInfo.avatar || userInfo.headImg" :src="formatAvatarUrl(userInfo.avatar || userInfo.headImg)" class="w-full h-full object-cover" />
                    <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200 text-gray-600 text-3xl font-black">
                      {{ (userInfo.username || 'U').charAt(0).toUpperCase() }}
                    </div>
                  </div>
                </div>
                <div class="absolute bottom-1 right-1 w-10 h-10 bg-gradient-to-br from-white to-gray-50 rounded-full shadow-xl flex items-center justify-center border-3 border-white/40 ring-2 ring-white/20 hover:scale-110 active:scale-95 transition-all duration-300" @click.stop="router.push('/user/profileEdit')">
                  <el-icon class="text-primary" :size="18"><Edit /></el-icon>
                </div>
              </div>
            </van-uploader>
            <div class="flex-1">
              <h2 class="text-3xl font-black tracking-tight">{{ userInfo.nickname || userInfo.username || $t('profilePage.newUser') }}</h2>
              <div class="flex flex-wrap items-center gap-2.5 mt-3">
                <div class="flex items-center bg-white/20 px-4 py-1.5 rounded-full backdrop-blur-md border border-white/25 text-xs font-medium shadow-lg">
                  <span>{{ userInfo.schoolName || $t('profilePage.inCampus') }}</span>
                </div>
                <div class="flex items-center bg-white/20 px-4 py-1.5 rounded-full backdrop-blur-md border border-white/25 text-xs font-medium shadow-lg">
                  <span>{{ $t('profilePage.creditPrefix') }}: {{ statistics.creditScore || 100 }}</span>
                </div>
              </div>
              <p class="text-white/80 text-xs mt-3 font-medium">{{ $t('profilePage.uid') }}: {{ userInfo.uid || userInfo.username }}</p>
            </div>
          </div>
        </div>

        <div class="mx-5 -mt-20 bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl p-7 flex justify-around items-center relative z-10 mb-8 border border-white/60">
          <div class="flex flex-col items-center flex-1 cursor-pointer hover:scale-105 transition-transform duration-300" @click="router.push('/user/my-products')">
            <span class="font-black text-3xl text-gray-900">{{ statistics.productCount || 0 }}</span>
            <span class="text-xs text-gray-500 mt-2 font-bold">{{ $t('profilePage.myPublished') }}</span>
          </div>
          <div class="w-0.5 h-12 bg-gradient-to-b from-gray-200 via-gray-100 to-gray-200"></div>
          <div class="flex flex-col items-center flex-1 cursor-pointer hover:scale-105 transition-transform duration-300" @click="router.push('/user/orders')">
            <span class="font-black text-3xl text-gray-900">{{ statistics.buyCount || 0 }}</span>
            <span class="text-xs text-gray-500 mt-2 font-bold">{{ $t('profilePage.myBought') }}</span>
          </div>
          <div class="w-0.5 h-12 bg-gradient-to-b from-gray-200 via-gray-100 to-gray-200"></div>
          <div class="flex flex-col items-center flex-1 cursor-pointer hover:scale-105 transition-transform duration-300" @click="router.push('/user/orders')">
            <span class="font-black text-3xl text-gray-900">{{ statistics.soldCount || 0 }}</span>
            <span class="text-xs text-gray-500 mt-2 font-bold">{{ $t('profilePage.mySold') }}</span>
          </div>
        </div>

        <div class="mx-5 space-y-4">
          <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl overflow-hidden border border-white/70">
            <div v-for="item in menuItems" :key="item.path" 
                 class="flex items-center px-6 py-5 hover:bg-gradient-to-r from-gray-50 to-gray-100 active:scale-[0.98] border-b border-gray-100 last:border-0 transition-all duration-300 cursor-pointer group" 
                 @click="router.push(item.path)">
              <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center mr-5 shadow-lg transition-all duration-300 group-hover:scale-110 group-hover:shadow-xl', item.bgClass, item.textClass]">
                <el-icon :size="22"><component :is="item.icon" /></el-icon>
              </div>
              <span class="flex-1 text-base font-bold text-gray-800">{{ item.label }}</span>
              <div class="flex items-center gap-1 text-gray-400 group-hover:text-primary group-hover:translate-x-1 transition-all duration-300">
                <el-icon :size="20"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
          <button class="w-full py-5 bg-gradient-to-r from-white to-gray-50 rounded-3xl text-red-500 font-black shadow-lg hover:shadow-xl active:scale-[0.98] transition-all duration-300 border border-red-100" @click="handleLogout">
            {{ $t('profilePage.logout') }}
          </button>
        </div>
      </div>
    </template>

    <!-- 桌面端视图 -->
    <template v-else>
      <div class="profile-page bg-gradient-to-br from-slate-50 via-gray-50 to-slate-100 min-h-screen pt-8 pb-24">
        <div class="px-8 max-w-7xl mx-auto pb-8">
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- 左侧用户信息卡片 -->
            <div class="lg:col-span-1">
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-100 overflow-hidden">
                <!-- 顶部渐变装饰 -->
                <div class="bg-gradient-to-br from-primary via-indigo-500 to-purple-600 p-8 relative overflow-hidden">
                  <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-20 -mt-20 blur-3xl"></div>
                  <div class="relative z-10 flex flex-col items-center">
                    <div class="relative mb-6">
                      <el-avatar :size="140" :src="formatAvatarUrl(userInfo.avatar || userInfo.headImg)" class="ring-8 ring-white/30 shadow-2xl">
                        <div class="w-full h-full bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center">
                          <el-icon :size="60" class="text-gray-500"><User /></el-icon>
                        </div>
                      </el-avatar>
                      <div class="absolute -bottom-2 -right-2 w-12 h-12 bg-gradient-to-br from-green-400 to-emerald-500 rounded-full flex items-center justify-center ring-4 ring-white shadow-xl">
                        <el-icon :size="24" class="text-white"><CircleCheck /></el-icon>
                      </div>
                    </div>
                    <h2 class="text-3xl font-black text-white mb-2">{{ userInfo.nickname || userInfo.username }}</h2>
                    <div class="flex items-center gap-2 bg-white/20 px-4 py-1.5 rounded-full backdrop-blur-md border border-white/25">
                      <span class="text-white/90 text-sm font-medium">{{ userInfo.schoolName || $t('profilePage.inCampus') }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="p-8">
                  <div class="space-y-5">
                    <div class="flex justify-between items-center p-4 bg-gray-50 rounded-2xl">
                      <span class="text-gray-500 font-medium">{{ $t('profilePage.uid') }}</span>
                      <span class="text-gray-800 font-bold">{{ userInfo.uid || userInfo.username }}</span>
                    </div>
                    <div class="flex justify-between items-center p-4 bg-gray-50 rounded-2xl">
                      <span class="text-gray-500 font-medium">{{ $t('profilePage.desktop.school') }}</span>
                      <span class="text-gray-800 font-bold">{{ userInfo.schoolName || $t('profilePage.inCampus') }}</span>
                    </div>
                    <div class="flex justify-between items-center p-4 bg-gray-50 rounded-2xl">
                      <span class="text-gray-500 font-medium">{{ $t('profilePage.desktop.registerTime') }}</span>
                      <span class="text-gray-800 font-bold">{{ userInfo.createTime ? new Date(userInfo.createTime).toLocaleDateString() : '-' }}</span>
                    </div>
                    <div class="flex justify-between items-center p-4 bg-gray-50 rounded-2xl">
                      <span class="text-gray-500 font-medium">{{ $t('profilePage.creditPrefix') }}</span>
                      <span class="text-primary font-black text-xl">{{ statistics.creditScore || 100 }}</span>
                    </div>
                  </div>
                  
                  <div class="mt-8 space-y-3">
                    <el-button type="primary" class="w-full !h-14 !text-lg !font-bold !rounded-2xl !shadow-lg hover:!shadow-xl transition-all" @click="router.push('/user/profileEdit')">
                      {{ $t('profilePage.editProfile') }}
                    </el-button>
                    <el-button class="w-full !h-14 !text-lg !font-bold !rounded-2xl !shadow-md hover:!shadow-lg transition-all" @click="handleLogout">
                      {{ $t('profilePage.logout') }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧统计和菜单 -->
            <div class="lg:col-span-2 space-y-8">
              <!-- 统计卡片网格 -->
              <div class="grid grid-cols-2 lg:grid-cols-4 gap-6">
                <div v-for="stat in desktopStats" :key="stat.label" 
                     class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100 p-7 text-center cursor-pointer hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 group" 
                     @click="router.push(stat.path)">
                  <div class="text-5xl font-black bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent mb-2">{{ stat.value }}</div>
                  <div class="text-sm font-bold text-gray-700 group-hover:text-primary transition-colors">{{ stat.label }}</div>
                </div>
              </div>
              
              <!-- 菜单区域 -->
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-gray-100 overflow-hidden">
                <div class="p-7 border-b border-gray-100">
                  <h3 class="text-2xl font-black text-gray-900 flex items-center gap-3">
                    <div class="h-10 w-10 bg-gradient-to-br from-primary to-purple-600 rounded-2xl flex items-center justify-center">
                      <el-icon :size="22" class="text-white"><List /></el-icon>
                    </div>
                    {{ $t('profilePage.desktop.menuTitle') || '快速导航' }}
                  </h3>
                </div>
                <div class="p-6">
                  <div class="grid grid-cols-2 lg:grid-cols-3 gap-4">
                    <div v-for="item in menuItems" :key="item.path" 
                         class="bg-gradient-to-br from-gray-50 to-gray-100 rounded-2xl p-5 border border-gray-100 cursor-pointer hover:from-white hover:to-gray-50 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group" 
                         @click="router.push(item.path)">
                      <div :class="['w-14 h-14 rounded-2xl flex items-center justify-center mb-4 shadow-lg group-hover:shadow-xl group-hover:scale-110 transition-all duration-300', item.bgClass]">
                        <el-icon :size="28"><component :is="item.icon" /></el-icon>
                      </div>
                      <div class="text-base font-bold text-gray-800">{{ item.label }}</div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 交易趋势占位 -->
              <div class="bg-white/90 backdrop-blur-xl rounded-3xl shadow-xl border border-gray-100">
                <div class="p-7 border-b border-gray-100 flex items-center gap-3">
                  <div class="h-10 w-10 bg-gradient-to-br from-cyan-100 to-blue-100 rounded-2xl flex items-center justify-center">
                    <el-icon :size="20" class="text-blue-600"><TrendCharts /></el-icon>
                  </div>
                  <h3 class="text-xl font-black text-gray-900">{{ $t('profilePage.desktop.tradeTrend') }}</h3>
                </div>
                <div class="p-8">
                  <div class="h-64 flex flex-col items-center justify-center text-gray-400 bg-gradient-to-br from-gray-50 to-gray-100 rounded-3xl border border-gray-100">
                    <el-icon :size="64" class="text-gray-300 mb-4"><TrendCharts /></el-icon>
                    <div class="text-lg font-medium">{{ $t('profilePage.desktop.noTrendData') }}</div>
                  </div>
                </div>
              </div>
            </div>
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
  Star, MapLocation, List, Bell, Setting, Clock, Stamp, Odometer, CircleCheck, TrendCharts
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
  { label: t('profilePage.menu.profile'), icon: User, path: '/user/profileEdit', bgClass: 'bg-gradient-to-br from-green-100 to-emerald-100', textClass: 'text-green-600' },
  { label: t('profilePage.menu.myPublish'), icon: Box, path: '/user/my-publish', bgClass: 'bg-gradient-to-br from-blue-100 to-indigo-100', textClass: 'text-blue-600' },
  { label: t('profilePage.menu.favorites'), icon: Star, path: '/user/favorites', bgClass: 'bg-gradient-to-br from-rose-100 to-pink-100', textClass: 'text-rose-600' },
  { label: t('profilePage.menu.history'), icon: Clock, path: '/user/recently-viewed', bgClass: 'bg-gradient-to-br from-amber-100 to-orange-100', textClass: 'text-amber-600' },
  { label: t('profilePage.menu.cart'), icon: ShoppingBag, path: '/user/cart', bgClass: 'bg-gradient-to-br from-cyan-100 to-teal-100', textClass: 'text-cyan-600' },
  { label: t('profilePage.menu.orders'), icon: List, path: '/user/orders', bgClass: 'bg-gradient-to-br from-violet-100 to-purple-100', textClass: 'text-violet-600' },
  { label: t('profilePage.menu.errandOrders'), icon: List, path: '/user/errand/orders', bgClass: 'bg-gradient-to-br from-fuchsia-100 to-pink-100', textClass: 'text-fuchsia-600' },
  { label: t('profilePage.menu.address'), icon: MapLocation, path: '/user/address', bgClass: 'bg-gradient-to-br from-lime-100 to-green-100', textClass: 'text-lime-600' },
  { label: t('profilePage.menu.verify'), icon: Stamp, path: '/user/verify', bgClass: 'bg-gradient-to-br from-orange-100 to-red-100', textClass: 'text-orange-600' },
  { label: t('profilePage.menu.credit'), icon: Odometer, path: '/user/credit', bgClass: 'bg-gradient-to-br from-sky-100 to-blue-100', textClass: 'text-sky-600' },
  { label: t('profilePage.menu.settings'), icon: Setting, path: '/user/settings', bgClass: 'bg-gradient-to-br from-slate-100 to-gray-200', textClass: 'text-slate-600' },
  { label: t('profilePage.menu.notices'), icon: Bell, path: '/user/notices', bgClass: 'bg-gradient-to-br from-yellow-100 to-amber-100', textClass: 'text-yellow-600' }
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
