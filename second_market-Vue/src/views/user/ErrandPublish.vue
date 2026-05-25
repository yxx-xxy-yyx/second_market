<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('errand.publishTitle') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900 mb-3">{{ $t('errand.type') }}</div>
        <div class="flex gap-2">
          <button class="type-btn" :class="subTypeCode === 'buy' ? 'active' : ''" @click="subTypeCode = 'buy'">{{ $t('errand.entries.buy') }}</button>
          <button class="type-btn" :class="subTypeCode === 'pick' ? 'active' : ''" @click="subTypeCode = 'pick'">{{ $t('errand.entries.pick') }}</button>
          <button class="type-btn" :class="subTypeCode === 'deliver' ? 'active' : ''" @click="subTypeCode = 'deliver'">{{ $t('errand.entries.deliver') }}</button>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 space-y-4">
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.title') }}</div>
          <el-input v-model="form.title" :placeholder="$t('errand.publish.titlePlaceholder')" />
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.itemType') }}</div>
            <el-input v-model="form.itemType" :placeholder="$t('errand.publish.itemTypePlaceholder')" />
          </div>
          <div>
            <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.weight') }}</div>
            <el-input v-model="form.weight" :placeholder="$t('errand.publish.optional')" />
          </div>
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.pickup') }}</div>
          <el-input v-model="form.pickup" :placeholder="$t('errand.publish.pickupPlaceholder')" />
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.deliver') }}</div>
          <el-input v-model="form.deliver" :placeholder="$t('errand.publish.deliverPlaceholder')" />
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.deadline') }}</div>
          <el-date-picker v-model="form.deadline" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" :placeholder="$t('errand.publish.deadlinePlaceholder')" class="w-full" />
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.reward') }}</div>
          <el-input v-model="form.reward" :placeholder="$t('errand.publish.rewardPlaceholder')" />
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('errand.publish.remark') }}</div>
          <el-input v-model="form.remark" type="textarea" :rows="4" :placeholder="$t('errand.publish.remarkPlaceholder')" />
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div>
            <div class="text-sm font-bold text-gray-900">{{ $t('errand.publish.visibilityTitle') }}</div>
            <div class="text-xs text-gray-500 mt-1">{{ $t('errand.publish.visibilityHint') }}</div>
          </div>
          <div class="text-sm font-bold text-primary">{{ schoolName }}</div>
        </div>
      </div>

      <el-button type="primary" class="w-full !rounded-2xl !h-12 !text-base !font-black" :loading="submitting" @click="submit">
        {{ $t('errand.publish.submit') }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { campusServiceApi } from '@/api/campus-service'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { t } = useI18n()

const resolveSubTypeCode = (v) => {
  const s = String(v || '')
  if (s === 'buy' || s === t('errand.entries.buy')) return 'buy'
  if (s === 'pick' || s === t('errand.entries.pick')) return 'pick'
  if (s === 'deliver' || s === t('errand.entries.deliver')) return 'deliver'
  if (s.includes('帮买')) return 'buy'
  if (s.includes('帮取')) return 'pick'
  if (s.includes('送')) return 'deliver'
  return 'buy'
}

const subTypeCode = ref(resolveSubTypeCode(route.query.subType))

const form = ref({
  title: '',
  itemType: '',
  weight: '',
  pickup: '',
  deliver: '',
  deadline: '',
  reward: '',
  remark: ''
})

const submitting = ref(false)

const schoolName = computed(() => {
  if (schoolStore.selectedSchool) return schoolStore.getSchoolName(String(schoolStore.selectedSchool)) || t('errand.publish.schoolInCampus')
  if (userStore.user?.schoolId) return schoolStore.getSchoolName(String(userStore.user.schoolId)) || t('errand.publish.schoolInCampus')
  return t('errand.publish.schoolNotSelected')
})

onMounted(() => {
  if (!schoolStore.selectedSchool && userStore.user?.schoolId) {
    schoolStore.setSchool(String(userStore.user.schoolId))
  }
})

const buildContent = () => {
  const parts = []
  if (form.value.itemType) parts.push(`${t('errand.publish.itemType')}：${form.value.itemType}`)
  if (form.value.weight) parts.push(`${t('errand.publish.weight')}：${form.value.weight}`)
  if (form.value.pickup) parts.push(`${t('errand.publish.pickup')}：${form.value.pickup}`)
  if (form.value.deliver) parts.push(`${t('errand.publish.deliver')}：${form.value.deliver}`)
  if (form.value.deadline) parts.push(`${t('errand.publish.deadline')}：${form.value.deadline}`)
  if (form.value.remark) parts.push(`${t('errand.publish.remark')}：${form.value.remark}`)
  return parts.join('\n')
}

const submit = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (!schoolStore.selectedSchool) {
    ElMessage.warning(t('errand.publish.needSchool'))
    return
  }
  if (!form.value.title.trim()) {
    ElMessage.warning(t('errand.publish.needTitle'))
    return
  }
  if (!form.value.pickup.trim() || !form.value.deliver.trim()) {
    ElMessage.warning(t('errand.publish.needAddress'))
    return
  }

  submitting.value = true
  try {
    const data = {
      type: 1,
      schoolId: Number(schoolStore.selectedSchool),
      title: `[${t(`errand.entries.${subTypeCode.value}`)}] ${form.value.title.trim()}`,
      content: buildContent(),
      location: `${form.value.pickup.trim()} → ${form.value.deliver.trim()}`,
      reward: Number(form.value.reward) || 0,
      deadline: form.value.deadline || null
    }
    const res = await campusServiceApi.publish(data)
    if (res.code === '200') {
      ElMessage.success(t('errand.publish.success'))
      router.replace('/user/errand/orders?tab=publish')
    } else {
      ElMessage.error(res.message || t('errand.publish.fail'))
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.type-btn {
  flex: 1;
  height: 38px;
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid rgba(241, 245, 249, 0.9);
  font-size: 12px;
  font-weight: 800;
  color: #64748b;
}

.type-btn.active {
  background: rgba(54, 179, 194, 0.12);
  border-color: rgba(54, 179, 194, 0.24);
  color: var(--primary-color);
}
</style>

