<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('feedbackPage.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('feedbackPage.sectionTitle') }}</div>
        <el-input v-model="content" type="textarea" :rows="5" :placeholder="$t('feedbackPage.placeholder')" />
        <div class="mt-4 flex gap-2">
          <el-button class="flex-1" @click="clear">{{ $t('feedbackPage.clear') }}</el-button>
          <el-button class="flex-1" type="primary" :disabled="!content.trim()" @click="submit">{{ $t('feedbackPage.submit') }}</el-button>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('feedbackPage.historyTitle') }}</div>
        <div v-if="records.length" class="space-y-2">
          <div v-for="r in records" :key="r.id" class="rounded-xl border border-gray-100 p-3">
            <div class="text-xs text-gray-400">{{ new Date(r.time).toLocaleString() }}</div>
            <div class="mt-1 text-sm text-gray-700 whitespace-pre-wrap">{{ r.content }}</div>
          </div>
        </div>
        <el-empty v-else :description="$t('feedbackPage.empty')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const STORAGE_KEY = 'sm_feedback_v1'
const content = ref('')
const records = ref([])

const load = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    records.value = raw ? JSON.parse(raw) : []
  } catch {
    records.value = []
  }
}

const persist = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(records.value))
}

const submit = async () => {
  try {
    await ElMessageBox.confirm(t('feedbackPage.confirm'), t('feedbackPage.confirmTitle'), { type: 'warning' })
  } catch {
    return
  }

  records.value.unshift({ id: Date.now(), time: Date.now(), content: content.value.trim() })
  records.value = records.value.slice(0, 50)
  persist()
  content.value = ''
  ElMessage.success(t('feedbackPage.success'))
}

const clear = () => {
  content.value = ''
}

onMounted(load)
</script>

