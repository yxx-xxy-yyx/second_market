<template>
  <el-drawer v-model="open" direction="rtl" size="340px">
    <template #header>
      <div class="font-semibold text-gray-900">{{ $t('common.more') }}</div>
    </template>

    <div class="space-y-5">
      <div class="flex items-center gap-3">
        <el-avatar :size="44" :src="avatarUrl">
          <el-icon><User /></el-icon>
        </el-avatar>
        <div class="min-w-0">
          <div class="text-base font-semibold text-gray-900 truncate">
            {{ userStore.user?.nickname || userStore.user?.username || '-' }}
          </div>
          <div class="text-xs text-gray-500 truncate">
            {{ userStore.user?.email || '' }}
          </div>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-3">
        <el-button class="w-full" @click="go('/user/profile')">{{ $t('nav.myAccount') }}</el-button>
        <el-button class="w-full" @click="go('/user/orders')">{{ $t('nav.myOrders') }}</el-button>
        <el-button class="w-full" @click="go('/user/favorites')">{{ $t('common.myFavorites') }}</el-button>
        <el-button class="w-full" @click="go('/user/messages')">{{ $t('nav.messages') }}</el-button>
        <el-button class="w-full" @click="openEdit">{{ $t('profile.editInfo') }}</el-button>
      </div>

      <div class="rounded-2xl border border-gray-100 bg-white p-4 space-y-4">
        <div class="space-y-2">
          <div class="text-xs font-semibold text-gray-600">{{ $t('common.school') }}</div>
          <el-select
            v-model="selectedSchool"
            :placeholder="$t('nav.selectSchool')"
            class="w-full"
            filterable
          >
            <el-option
              v-for="item in schoolStore.schoolList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>

        <div class="space-y-2">
          <div class="text-xs font-semibold text-gray-600">{{ $t('common.language') }}</div>
          <LangSwitcher />
        </div>
      </div>

      <el-button type="danger" class="w-full" @click="handleLogout">
        {{ $t('common.logout') }}
      </el-button>
    </div>
  </el-drawer>

  <el-dialog v-model="showEditDialog" :title="$t('profile.editInfo')" width="92%" :close-on-click-modal="false">
    <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
      <el-form-item :label="$t('profile.nickname')" prop="nickname">
        <el-input v-model="editForm.nickname" :placeholder="$t('profile.nickname')" />
      </el-form-item>
      <el-form-item :label="$t('profile.phone')" prop="phone">
        <el-input v-model="editForm.phone" :placeholder="$t('profile.phone')" />
      </el-form-item>
      <el-form-item :label="$t('profile.address')" prop="address">
        <el-input v-model="editForm.address" type="textarea" :rows="3" :placeholder="$t('profile.address')" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showEditDialog = false">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" :loading="saving" @click="handleSaveEdit">{{ $t('common.save') }}</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { formatAvatarUrl } from '@/utils/url'
import { User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'
import { useI18n } from 'vue-i18n'

const open = defineModel({ type: Boolean, default: false })

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { t } = useI18n()

const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

const showEditDialog = ref(false)
const saving = ref(false)
const editFormRef = ref()

const editForm = reactive({
  nickname: '',
  phone: '',
  address: ''
})

const editRules = {
  nickname: [{ required: true, message: t('common.pleaseInputNickname'), trigger: 'blur' }]
}

const selectedSchool = computed({
  get: () => schoolStore.selectedSchool,
  set: (value) => schoolStore.setSchool(value)
})

onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
})

const go = async (path) => {
  open.value = false
  await router.push(path)
}

const openEdit = () => {
  open.value = false
  editForm.nickname = userStore.user?.nickname || ''
  editForm.phone = userStore.user?.phone || ''
  editForm.address = userStore.user?.address || ''
  showEditDialog.value = true
}

const handleSaveEdit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate()
  saving.value = true
  try {
    const res = await userApi.updateUserInfo({
      nickname: editForm.nickname,
      phone: editForm.phone,
      address: editForm.address
    })
    if (res.code === '200' || res.success) {
      ElMessage.success(t('profile.saveSuccess'))
      showEditDialog.value = false
      await userStore.getUserInfo()
    } else {
      ElMessage.error(res.message || t('profile.saveFail'))
    }
  } finally {
    saving.value = false
  }
}

const handleLogout = async () => {
  open.value = false
  userStore.logout()
  await router.replace('/login')
}
</script>
