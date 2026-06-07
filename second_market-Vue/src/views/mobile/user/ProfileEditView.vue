<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showToast, showSuccessToast } from 'vant'
import {
  UserIcon,
  IdentificationIcon,
  PhoneIcon,
  AcademicCapIcon,
  BuildingOfficeIcon,
  CameraIcon,
  ChevronLeftIcon
} from '@heroicons/vue/24/outline'
import { userApi } from '@/api/user'
import { uploadAvatarApi } from '@/api/file'
import { formatAvatarUrl } from '@/utils/url'
import LangSwitcher from '@/components/LangSwitcher.vue'

const router = useRouter()
const userStore = useUserStore()

const isSaving = ref(false)
const isUploadingAvatar = ref(false)
const avatarInputRef = ref(null)

const form = ref({
  nickname: '',
  name: '',
  schoolName: '',
  dormitory: '',
  address: '',
  phone: '',
  avatar: ''
})

const isOk = (res) => res && (res.code === '200' || res.success)

const fillForm = (user = {}) => {
  form.value = {
    nickname: user.nickname || user.name || '',
    name: user.name || '',
    schoolName: user.schoolName || '',
    dormitory: user.dormitory || '',
    address: user.address || '',
    phone: user.phone || user.mobile || '',
    avatar: user.avatar || ''
  }
}

const avatarUrl = computed(() => formatAvatarUrl(form.value.avatar))

const loadUserInfo = async () => {
  const cachedUser = userStore.user || null
  if (cachedUser) {
    fillForm(cachedUser)
  }

  try {
    const ok = await userStore.getUserInfo()
    if (ok.success) {
      fillForm(userStore.user || {})
      return
    }

    const res = await userApi.getCurrentUser()
    if (isOk(res)) {
      userStore.setUser(res.data)
      fillForm(res.data || {})
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    showToast('获取用户信息失败')
  }
}

onMounted(() => {
  loadUserInfo()
})

const uploadAvatar = async (selectedFile) => {
  if (!selectedFile || isUploadingAvatar.value) return
  if (!selectedFile.type?.startsWith('image/')) {
    showToast('请选择图片文件')
    return
  }
  if (selectedFile.size / 1024 / 1024 > 5) {
    showToast('图片不能超过5MB')
    return
  }

  showToast('头像上传中...')
  isUploadingAvatar.value = true
  try {
    const res = await uploadAvatarApi(selectedFile)
    if (!isOk(res)) {
      throw new Error(res?.message || '上传失败')
    }

    const uploaded = res.data || {}
    const avatar =
      uploaded.fileUrl ||
      uploaded.filePath ||
      uploaded.url ||
      uploaded.path ||
      (typeof uploaded === 'string' ? uploaded : '')

    if (!avatar) {
      throw new Error('未获取到头像地址')
    }

    const updateRes = await userApi.updateAvatar(avatar)
    if (!isOk(updateRes)) {
      throw new Error(updateRes?.message || '头像更新失败')
    }

    form.value.avatar = avatar
    userStore.updateUser({ avatar })
    showSuccessToast('头像更新成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    showToast(error.message || '上传失败')
  } finally {
    isUploadingAvatar.value = false
  }
}

const handleAvatarChange = async (event) => {
  const selectedFile = event.target.files?.[0]
  await uploadAvatar(selectedFile)
  event.target.value = ''
}

const triggerAvatarPicker = () => {
  if (isUploadingAvatar.value) return
  avatarInputRef.value?.click()
}

const handleSave = async () => {
  if (isSaving.value) return
  if (!form.value.nickname.trim()) {
    showToast('请输入昵称')
    return
  }

  isSaving.value = true
  try {
    const payload = {
      ...form.value,
      nickname: form.value.nickname.trim(),
      phone: form.value.phone.trim(),
      mobile: form.value.phone.trim()
    }

    const res = await userApi.updateUserInfo(payload)
    if (!isOk(res)) {
      throw new Error(res?.message || '保存失败')
    }

    showSuccessToast('保存成功')
    await loadUserInfo()
    setTimeout(() => router.back(), 800)
  } catch (error) {
    console.error('保存用户信息失败:', error)
    showToast(error.message || '保存失败')
  } finally {
    isSaving.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 pb-32">
    <div class="bg-gradient-to-br from-primary to-primaryDark pt-8 pb-16 px-8 rounded-b-[3rem] shadow-lg relative overflow-hidden">
      <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -mr-16 -mt-16 blur-3xl"></div>
      
      <div class="relative z-50 flex items-center h-10 mb-4">
        <button 
          @click.stop="router.back()" 
          type="button"
          class="absolute left-0 p-2 -ml-2 rounded-full hover:bg-white/10 active:scale-95 transition-all text-white cursor-pointer"
        >
          <ChevronLeftIcon class="w-7 h-7" />
        </button>
        
        <h1 class="w-full text-center text-lg font-bold text-white tracking-wide">编辑资料</h1>
        <div class="absolute right-0 top-1/2 -translate-y-1/2">
          <LangSwitcher glass />
        </div>
      </div>

      <div class="flex justify-center relative z-10">
        <button
          type="button"
          class="relative block active:scale-95 transition-transform"
          :disabled="isUploadingAvatar"
          @click="triggerAvatarPicker"
        >
          <div class="w-24 h-24 rounded-full bg-white/20 backdrop-blur-md p-1 shadow-inner">
            <div class="w-full h-full rounded-full bg-white overflow-hidden border border-white/20">
              <img v-if="form.avatar" :src="avatarUrl" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex items-center justify-center bg-gray-100 text-gray-400">
                <UserIcon class="w-12 h-12" />
              </div>
            </div>
          </div>
          <div class="absolute bottom-0 right-0 w-8 h-8 bg-white rounded-full shadow-lg flex items-center justify-center border border-gray-100 ring-2 ring-white/10">
            <CameraIcon class="w-4 h-4 text-primary" />
          </div>
          <div v-if="isUploadingAvatar" class="absolute inset-0 rounded-full bg-black/35 flex items-center justify-center text-white text-xs font-bold">
            上传中
          </div>
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/*"
            class="avatar-file-input"
            @click.stop
            @change="handleAvatarChange"
          />
        </button>
      </div>
    </div>

    <div class="px-5 -mt-8 relative z-20">
      <div class="bg-white rounded-[2.5rem] shadow-xl shadow-gray-200/50 overflow-hidden border border-white">
        <div class="p-5 flex items-center border-b border-gray-50">
          <div class="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center mr-4">
            <IdentificationIcon class="w-5 h-5 text-blue-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">昵称</div>
            <input v-model="form.nickname" type="text" placeholder="请输入昵称"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 flex items-center border-b border-gray-50">
          <div class="w-10 h-10 rounded-full bg-indigo-50 flex items-center justify-center mr-4">
            <PhoneIcon class="w-5 h-5 text-indigo-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">手机号</div>
            <input v-model="form.phone" type="tel" placeholder="请输入联系电话"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 flex items-center border-b border-gray-50">
          <div class="w-10 h-10 rounded-full bg-purple-50 flex items-center justify-center mr-4">
            <AcademicCapIcon class="w-5 h-5 text-purple-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">学校</div>
            <input v-model="form.schoolName" type="text" placeholder="请输入学校名称"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>

        <div class="p-5 flex items-center">
          <div class="w-10 h-10 rounded-full bg-orange-50 flex items-center justify-center mr-4">
            <BuildingOfficeIcon class="w-5 h-5 text-orange-600" />
          </div>
          <div class="flex-1">
            <div class="text-xs text-gray-400 mb-1 font-medium">宿舍楼</div>
            <input v-model="form.dormitory" type="text" placeholder="例如：西区5号楼"
              class="w-full text-gray-900 placeholder-gray-300 outline-none text-base font-medium bg-transparent" />
          </div>
        </div>
      </div>

      <div class="mt-8 px-2">
        <button @click="handleSave" :disabled="isSaving"
          class="w-full bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-bold py-4 rounded-2xl shadow-lg shadow-blue-200 active:scale-95 transition-transform">
          {{ isSaving ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.avatar-file-input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}
</style>
