<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('agreement.user.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 py-4">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 text-sm text-gray-700 leading-relaxed space-y-3">
        <div v-html="contentHtml"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const escapeHtml = (s) => {
  return String(s || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

const contentHtml = computed(() => {
  return escapeHtml(t('agreement.user.content')).replace(/\n/g, '<br/>')
})
</script>

