<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('verifyPage.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-gray-900">{{ $t('verifyPage.statusTitle') }}</div>
          <div class="text-xs px-2 py-1 rounded-full font-bold" :class="verified ? 'bg-primary/10 text-primary' : 'bg-gray-100 text-gray-500'">
            {{ verified ? $t('verifyPage.verified') : $t('verifyPage.unverified') }}
          </div>
        </div>
        <div class="mt-3 text-xs text-gray-500 leading-relaxed">
          {{ $t('verifyPage.tip') }}
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 space-y-4">
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('verifyPage.name') }}</div>
          <el-input v-model="form.name" :placeholder="$t('verifyPage.namePlaceholder')" />
        </div>
        <div>
          <div class="text-xs text-gray-500 mb-2">{{ $t('verifyPage.idNo') }}</div>
          <el-input v-model="form.idNo" :placeholder="$t('verifyPage.idNoPlaceholder')" />
        </div>
        <div class="flex gap-2">
          <el-button class="flex-1" @click="reset">{{ $t('verifyPage.reset') }}</el-button>
          <el-button class="flex-1" type="primary" :disabled="!canSubmit" @click="submit">{{ verified ? $t('verifyPage.update') : $t('verifyPage.submit') }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const STORAGE_KEY = 'sm_verify_v1'
const verified = ref(false)
const form = ref({ name: '', idNo: '' })

const load = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    const data = raw ? JSON.parse(raw) : null
    if (data?.name) form.value.name = data.name
    if (data?.idNo) form.value.idNo = data.idNo
    verified.value = !!data?.verified
  } catch {}
}

const persist = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ ...form.value, verified: true, time: Date.now() }))
}

const canSubmit = computed(() => form.value.name.trim().length >= 2 && form.value.idNo.trim().length >= 6)

const submit = async () => {
  try {
    await ElMessageBox.confirm(t('verifyPage.confirm'), t('common.tip'), { type: 'warning' })
    persist()
    verified.value = true
    ElMessage.success(t('verifyPage.saved'))
  } catch {}
}

const reset = () => {
  form.value = { name: '', idNo: '' }
  verified.value = false
  localStorage.removeItem(STORAGE_KEY)
}

onMounted(load)
</script>

