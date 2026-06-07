<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { showToast } from 'vant'
import { formatAvatarUrl } from '@/utils/url'
import request from '@/api/request'
import {
  UserCircleIcon,
  MapPinIcon,
  ArrowRightOnRectangleIcon,
  ShoppingBagIcon,
  ChevronRightIcon,
  CurrencyYenIcon,
  ClipboardDocumentListIcon,
  MegaphoneIcon,
  ShieldCheckIcon,
  PencilSquareIcon,
  AcademicCapIcon
} from '@heroicons/vue/24/outline'

const userStore = useUserStore()
const router = useRouter()
const { t } = useI18n()

// 数据定义
const userInfo = ref({})
const statistics = ref({
  publishedCount: 0,
  soldCount: 0,
  favoriteCount: 0,
  creditScore: 100
})

const navTo = (path) => {
  router.push(path).catch(() => { })
}

// 加载数据
const loadData = async () => {
  try {
    const userRes = await request.get('/user/info')
    if (userRes.code === '200' || userRes.success) {
      userInfo.value = userRes.data || {}
      userStore.setUser(userRes.data)
    }
    const statsRes = await request.get('/user/statistics/overview')
    if (statsRes.code === '200' || statsRes.success) {
      statistics.value = statsRes.data || {}
    }
  } catch (error) {
    console.error('加载失败:', error)
  }
}

const afterRead = async (file) => {
  const formData = new FormData()
  formData.append('file', file.file)
  try {
    const res = await request.post('/common/upload', formData)
    if (res.code === '200') {
      const newAvatar = res.data.url
      await request.put('/user/update', { avatar: newAvatar })
      userInfo.value.avatar = newAvatar
      showToast(t('profilePage.toast.avatarUpdated'))
    }
  } catch (error) {
    showToast(t('profilePage.toast.uploadFail'))
  }
}

onMounted(() => loadData())

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="profile-mobile-page min-h-screen bg-gray-50 pb-24 w-full overflow-x-hidden font-sans">
    <div class="relative overflow-hidden pt-10 pb-24 px-8 rounded-b-[3rem] shadow-lg"
      style="background: linear-gradient(135deg, #14b8a6 0%, #06b6d4 100%);">
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>

      <div class="flex items-center space-x-5 relative z-10 text-white">
        <van-uploader :after-read="afterRead" class="block">
          <div class="relative w-20 h-20">
            <div class="w-20 h-20 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner overflow-hidden">
              <div class="w-full h-full rounded-full bg-white overflow-hidden border border-white/20">
                <img v-if="userInfo.avatar || userInfo.headImg"
                  :src="formatAvatarUrl(userInfo.avatar || userInfo.headImg)" class="w-full h-full object-cover" />
                <div v-else
                  class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400 text-2xl font-bold">
                  {{ (userInfo.username || 'U').charAt(0).toUpperCase() }}
                </div>
              </div>
            </div>
            <div
              class="absolute bottom-0 right-0 w-7 h-7 bg-white rounded-full shadow-md flex items-center justify-center border border-gray-100 ring-2 ring-white/10 active:scale-90 transition-transform"
              @click="router.push('/user/profileEdit')">
              <PencilSquareIcon class="w-4 h-4 text-blue-500" />

            </div>
          </div>
        </van-uploader>

        <div class="flex-1">
          <h2 class="text-2xl font-bold tracking-tight">
            {{ userInfo.nickname || userInfo.username || t('profilePage.newUser') }}
          </h2>

          <div class="flex flex-wrap items-center gap-2 mt-2 text-sm text-blue-100 opacity-90">
            <div class="flex items-center bg-white/15 px-3 py-1 rounded-full backdrop-blur-md border border-white/10">
              <AcademicCapIcon class="w-3 h-3 mr-1" />
              <span>{{ userInfo.schoolName || t('profilePage.defaultSchool') }}</span>
            </div>
            <div class="flex items-center bg-white/15 px-3 py-1 rounded-full backdrop-blur-md border border-white/10">
              <span>{{ t('profilePage.creditPrefix') }}: {{ statistics.creditScore || 100 }}</span>
            </div>
            <div class="flex items-center bg-white/15 px-3 py-1 rounded-full backdrop-blur-md border border-white/10">
              <ShieldCheckIcon class="w-3 h-3 mr-1 text-blue-200" />
              <span>{{ t('profilePage.uid') }}: {{ userInfo.uid || userInfo.username }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="mx-5 -mt-16 bg-white rounded-2xl shadow-xl p-5 flex justify-around items-center relative z-10 mb-6 border border-white">
      <div class="flex flex-col items-center flex-1 cursor-pointer" @click="navTo('/user/my-products')">
        <span class="font-bold text-xl text-gray-800">{{ statistics.productCount || 0 }}</span>
        <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ t('profilePage.myPublished') }}</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center flex-1 cursor-pointer" @click="navTo('/user/orders?tab=buyer')">
        <span class="font-bold text-xl text-gray-800">{{ statistics.buyCount || 0 }}</span>
        <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ t('profilePage.myBought') }}</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center flex-1 cursor-pointer" @click="navTo('/user/orders?tab=seller')">
        <span class="font-bold text-xl text-gray-800">{{ statistics.soldCount || 0 }}</span>
        <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ t('profilePage.mySold') }}</span>
      </div>
      <div class="w-px h-8 bg-gray-100"></div>
      <div class="flex flex-col items-center flex-1 cursor-pointer" @click="navTo('/user/favorites')">
        <span class="font-bold text-xl text-gray-800">{{ statistics.favoriteCount || 0 }}</span>
        <span class="text-[11px] text-gray-400 mt-1 font-medium">{{ t('profilePage.menu.favorites') }}</span>
      </div>
    </div>

    <div class="mx-5 space-y-3">
      <div class="bg-white rounded-[2rem] shadow-sm overflow-hidden border border-gray-100">
        <div class="menu-item" @click="router.push('/user/profileEdit')">
          <div class="menu-icon-wrapper bg-blue-50 text-blue-500">
            <UserCircleIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.menu.profile') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>

        <div class="menu-item" @click="navTo('/user/address')">
          <div class="menu-icon-wrapper bg-orange-50 text-orange-500">
            <MapPinIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.menu.address') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>

        <div class="menu-item" @click="navTo('/user/recently-viewed')">
          <div class="menu-icon-wrapper bg-emerald-50 text-emerald-500">
            <ClipboardDocumentListIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.menu.history') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>

        <div class="menu-item" @click="navTo('/user/my-products')">
          <div class="menu-icon-wrapper bg-purple-50 text-purple-500">
            <ShoppingBagIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.myIdle') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>

        <div class="menu-item" @click="navTo('/user/orders?tab=buyer')">
          <div class="menu-icon-wrapper bg-teal-50 text-teal-600">
            <ShoppingBagIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.myBought') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>

        <div class="menu-item" @click="navTo('/user/orders?tab=seller')">
          <div class="menu-icon-wrapper bg-indigo-50 text-indigo-600">
            <CurrencyYenIcon class="w-5 h-5" />
          </div>
          <span class="menu-text">{{ t('profilePage.mySold') }}</span>
          <ChevronRightIcon class="menu-arrow" />
        </div>
      </div>

      <div
        class="bg-red-600 rounded-2xl shadow-sm p-4 mt-6 flex items-center justify-center cursor-pointer active:scale-95 transition-colors"
        @click="handleLogout">
        <ArrowRightOnRectangleIcon class="w-5 h-5 text-white mr-2" />
        <span class="text-white font-bold text-sm">{{ t('profilePage.logout') }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-mobile-page {
  /* 确保页面内容不被固定的 MobileHeader 遮挡 */
  padding-top: calc(65px + env(safe-area-inset-top));
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.1rem 1.25rem;
  border-bottom: 1px solid #f9fafb;
  cursor: pointer;
}

.menu-item:hover {
  background-color: #f9fafb;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon-wrapper {
  width: 2.4rem;
  height: 2.4rem;
  border-radius: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.8rem;
}

.menu-text {
  flex: 1;
  color: #374151;
  font-weight: 500;
  font-size: 0.9rem;
}

.menu-arrow {
  width: 1rem;
  height: 1rem;
  color: #d1d5db;
}
</style>