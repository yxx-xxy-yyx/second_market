<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('creditPage.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-gray-900">{{ $t('creditPage.current') }}</div>
          <div class="text-xl font-black text-primary">{{ creditScore }}</div>
        </div>
        <div class="mt-3">
          <div class="h-3 rounded-full bg-gray-100 overflow-hidden">
            <div class="h-full bg-primary" :style="{ width: progress + '%' }"></div>
          </div>
          <div class="mt-2 text-xs text-gray-500">{{ $t('creditPage.hint') }}</div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('creditPage.portrait') }}</div>
        <div v-loading="loading" class="grid grid-cols-2 gap-3">
          <div class="rounded-2xl border border-gray-100 p-3">
            <div class="text-xs text-gray-500">{{ $t('creditPage.avgRating') }}</div>
            <div class="mt-1 text-lg font-black text-primary">{{ rating.averageRating || 0 }}</div>
          </div>
          <div class="rounded-2xl border border-gray-100 p-3">
            <div class="text-xs text-gray-500">{{ $t('creditPage.reviewCount') }}</div>
            <div class="mt-1 text-lg font-black text-gray-900">{{ rating.totalReviews || 0 }}</div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 space-y-2 text-sm text-gray-700 leading-relaxed">
        <div class="font-bold text-gray-900">{{ $t('creditPage.tipsTitle') }}</div>
        <div>{{ $t('creditPage.tips.t1') }}</div>
        <div>{{ $t('creditPage.tips.t2') }}</div>
        <div>{{ $t('creditPage.tips.t3') }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { userStatisticsApi } from '@/api/statistics'
import request from '@/api/request'

const router = useRouter()

const loading = ref(false)
const creditScore = ref(100)
const rating = ref({})

const progress = computed(() => {
  const v = Number(creditScore.value || 0)
  const p = Math.max(0, Math.min(100, v))
  return p
})

const load = async () => {
  loading.value = true
  try {
    const [overviewRes, ratingRes] = await Promise.all([
      request.get('/user/statistics/overview').catch(() => null),
      userStatisticsApi.getRating().catch(() => null)
    ])
    if (overviewRes?.code === '200' && overviewRes.data?.creditScore != null) {
      creditScore.value = overviewRes.data.creditScore
    }
    if (ratingRes?.code === '200') {
      rating.value = ratingRes.data || {}
    }
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

