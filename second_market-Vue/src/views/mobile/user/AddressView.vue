<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast } from 'vant'
import {
  ChevronLeftIcon,
  MapPinIcon,
  HomeIcon,
  MapIcon
} from '@heroicons/vue/24/outline'
import { userApi } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const address = ref('')
const maxLength = 100

onMounted(() => {
  address.value = userStore.user?.address || ''
})

const handleSave = async () => {
  if (!address.value.trim()) {
    showToast(t('profilePage.addressView.addressRequired'))
    return
  }

  try {
    const res = await userApi.updateUserInfo({ address: address.value })
    
    if (res.code === '200' || res.success) {
      showSuccessToast(t('profilePage.addressView.saveSuccess'))
      // 更新本地 store
      userStore.updateUser({ address: address.value })
      setTimeout(() => router.back(), 1500)
    }
  } catch (error) {
    showToast(t('profilePage.addressView.saveFail'))
    console.error(error)
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-32 font-sans">
    <div class="bg-gradient-to-br from-primary to-primaryDark pt-8 pb-16 px-8 rounded-b-[3rem] shadow-lg relative overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
      
      <div class="relative z-10 flex items-center h-10 mb-4">
        <button @click="router.back()" class="absolute left-0 p-1 rounded-full hover:bg-white/10 active:scale-90 transition-all text-white">
          <ChevronLeftIcon class="w-7 h-7" />
        </button>
        <h1 class="w-full text-center text-lg font-bold text-white tracking-wide">{{ t('profilePage.addressView.title') }}</h1>
      </div>

      <div class="flex justify-center relative z-10">
        <div class="w-20 h-20 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner flex items-center justify-center">
          <div class="w-full h-full rounded-full bg-white flex items-center justify-center border border-white/20">
            <MapIcon class="w-10 h-10 text-primary" />
          </div>
        </div>
      </div>
    </div>

    <div class="px-5 -mt-8 relative z-20">
      <div class="bg-white rounded-[2.5rem] shadow-xl shadow-gray-200/50 overflow-hidden border border-white p-6">
        <div class="flex items-start space-x-4">
          <div class="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
            <MapPinIcon class="w-5 h-5 text-primary" />
          </div>
          
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-2 font-medium tracking-wide">{{ t('profilePage.addressView.detailAddress') }}</div>
            <div class="relative">
              <textarea 
                v-model="address"
                :maxlength="maxLength"
                :placeholder="t('profilePage.addressView.addressPlaceholder')"
                class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-gray-50/50 rounded-2xl p-4 min-h-[120px] resize-none focus:bg-gray-50 transition-colors border border-transparent focus:border-primary/30"
              ></textarea>
              
              <div class="absolute bottom-3 right-4 text-[10px] text-gray-300 font-mono">
                {{ address.length }}/{{ maxLength }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-8 px-2">
        <button @click="handleSave"
          class="w-full bg-gradient-to-r from-primary to-primaryDark text-white font-bold py-4 rounded-2xl shadow-lg shadow-cyan-soft active:scale-95 transition-transform flex items-center justify-center space-x-2">
          <span>{{ t('profilePage.addressView.saveBtn') }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
textarea::-webkit-scrollbar {
  width: 4px;
}
textarea::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 10px;
}

textarea:focus {
  box-shadow: none;
}
</style>
