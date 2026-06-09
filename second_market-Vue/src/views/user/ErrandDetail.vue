<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('errand.detailTitle') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div v-loading="loading" class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <div class="text-sm font-black text-gray-900">{{ detail.title }}</div>
            <div class="mt-2 text-[11px] text-gray-500 whitespace-pre-wrap">{{ detail.content }}</div>
          </div>
          <div class="text-[10px] px-2 py-1 rounded-full bg-primary/10 text-primary font-bold">{{ statusText(detail.status) }}</div>
        </div>
        <div class="mt-3 flex items-center justify-between text-[11px] text-gray-500">
          <div class="truncate">{{ detail.location || '校内' }}</div>
          <div v-if="Number(detail.reward) > 0" class="font-black text-primary">¥{{ detail.reward }}</div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900">{{ $t('errand.tracking') }}</div>
        <div class="mt-3 space-y-3">
          <div class="step">
            <div class="dot bg-primary"></div>
            <div class="flex-1">
              <div class="text-sm font-bold text-gray-900">{{ $t('errand.steps.published') }}</div>
              <div class="text-[11px] text-gray-500">{{ detail.createTime || '-' }}</div>
            </div>
          </div>
          <div class="step">
            <div class="dot" :class="detail.status >= 2 ? 'bg-primary' : 'bg-gray-200'"></div>
            <div class="flex-1">
              <div class="text-sm font-bold" :class="detail.status >= 2 ? 'text-gray-900' : 'text-gray-400'">{{ $t('errand.steps.accepted') }}</div>
              <div class="text-[11px]" :class="detail.status >= 2 ? 'text-gray-500' : 'text-gray-300'">{{ detail.accepterNickname || (detail.accepterId ? `骑手ID ${detail.accepterId}` : '暂无') }}</div>
            </div>
          </div>
          <div class="step">
            <div class="dot" :class="detail.status === 3 ? 'bg-primary' : (detail.status === 4 ? 'bg-gray-200' : 'bg-gray-200')"></div>
            <div class="flex-1">
              <div class="text-sm font-bold" :class="detail.status === 3 ? 'text-gray-900' : 'text-gray-400'">{{ $t('errand.steps.completed') }}</div>
              <div class="text-[11px]" :class="detail.status === 3 ? 'text-gray-500' : 'text-gray-300'">{{ detail.status === 3 ? (detail.updateTime || '-') : '-' }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('errand.publisher') }}</div>
        <div class="flex items-center gap-3">
          <el-avatar :size="40" :src="detail.userAvatar" />
          <div class="flex-1 min-w-0">
            <div class="text-sm font-bold text-gray-900 truncate">{{ detail.userNickname || `用户 ${detail.userId}` }}</div>
            <div class="text-[11px] text-gray-500 truncate">学校ID {{ detail.schoolId }}</div>
          </div>
          <el-button v-if="detail.userId && String(detail.userId) !== String(me)" size="small" plain @click="chatTo(detail.userId, detail.userNickname, detail.userAvatar)">{{ $t('errand.actions.chat') }}</el-button>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4" v-if="detail.accepterId">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('errand.riderInfo') }}</div>
        <div class="flex items-center gap-3">
          <el-avatar :size="40" :src="formatAvatar(detail.accepterAvatar)" />
          <div class="flex-1 min-w-0">
            <div class="text-sm font-bold text-gray-900 truncate">{{ detail.accepterNickname || `用户 ${detail.accepterId}` }}</div>
            <div class="text-[11px] text-gray-500 truncate flex items-center gap-2">
              <span>接单ID {{ detail.accepterId }}</span>
              <span v-if="ratingInfo.reviewCount" class="text-primary font-bold">{{ Number(ratingInfo.avgRating || 0).toFixed(1) }}分</span>
              <span v-if="ratingInfo.reviewCount" class="text-gray-400">({{ ratingInfo.reviewCount }}条)</span>
            </div>
          </div>
          <el-button v-if="String(detail.accepterId) !== String(me)" size="small" plain @click="chatTo(detail.accepterId, detail.accepterNickname, '')">{{ $t('errand.actions.chat') }}</el-button>
        </div>
        <div class="mt-3" v-if="ratingInfo.reviewCount">
          <el-rate :model-value="Number(ratingInfo.avgRating || 0)" disabled allow-half />
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4" v-if="reviews.length">
        <div class="text-sm font-bold text-gray-900 mb-2">{{ $t('errand.review.title') }}</div>
        <div class="space-y-3">
          <div v-for="r in reviews" :key="r.id" class="flex items-start gap-3">
            <el-avatar :size="34" :src="formatAvatar(r.reviewerAvatar)" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between">
                <div class="text-sm font-bold text-gray-900 truncate">{{ r.reviewerNickname || `用户 ${r.reviewerId}` }}</div>
                <div class="text-[11px] text-gray-400">{{ r.createTime || '' }}</div>
              </div>
              <el-rate :model-value="Number(r.rating || 0)" disabled />
              <div v-if="r.content" class="mt-1 text-[12px] text-gray-600 whitespace-pre-wrap">{{ r.content }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex justify-end gap-2">
          <el-button v-if="isPublisher && detail.status === 1" plain @click="cancel">{{ $t('errand.actions.cancel') }}</el-button>
          <el-button v-if="canAccept" type="primary" @click="accept">{{ $t('errand.actions.accept') }}</el-button>
          <el-button v-if="detail.status === 2 && (isPublisher || isAccepter)" type="success" @click="complete">{{ $t('errand.actions.complete') }}</el-button>
          <el-button v-if="canReview" type="primary" @click="reviewDialog = true">{{ $t('errand.actions.reviewRider') }}</el-button>
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="reviewDialog" :title="$t('errand.review.dialogTitle')" width="92%" :close-on-click-modal="false">
    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <div class="text-sm font-bold text-gray-900">{{ $t('errand.review.rating') }}</div>
        <el-rate v-model="reviewForm.rating" />
      </div>
      <el-input v-model="reviewForm.content" type="textarea" :rows="4" :placeholder="$t('errand.review.contentPlaceholder')" />
    </div>
    <template #footer>
      <div class="flex justify-end gap-2">
        <el-button @click="reviewDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">{{ $t('errand.actions.submitReview') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { campusServiceApi } from '@/api/campus-service'
import { reviewApi } from '@/api/review'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const id = route.params.id
const loading = ref(false)
const detail = ref({})

const me = computed(() => userStore.user?.id)
const isPublisher = computed(() => detail.value?.userId && String(detail.value.userId) === String(me.value))
const isAccepter = computed(() => detail.value?.accepterId && String(detail.value.accepterId) === String(me.value))
const canAccept = computed(() => detail.value?.status === 1 && !isPublisher.value && userStore.isLoggedIn)
const canReview = computed(() => isPublisher.value && detail.value?.status === 3 && detail.value?.accepterId && reviewed.value === false)

const statusText = (s) => {
  if (s === 1) return t('errand.status.waiting')
  if (s === 2) return t('errand.status.running')
  if (s === 3) return t('errand.status.done')
  if (s === 4) return t('errand.status.canceled')
  return t('errand.status.unknown')
}

const formatAvatar = (v) => formatAvatarUrl(v || '')

const ratingInfo = ref({ avgRating: 0, reviewCount: 0 })
const reviews = ref([])
const reviewed = ref(false)
const reviewDialog = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = reactive({
  rating: 5,
  content: ''
})

const chatTo = (userId, nickname, avatar) => {
  router.push({
    path: `/user/chat/${userId}`,
    query: {
      nickname: nickname || '',
      avatar: avatar || ''
    }
  })
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await campusServiceApi.getById(id)
    if (res.code === '200') {
      detail.value = res.data || {}
      await refreshReviewState()
    }
  } finally {
    loading.value = false
  }
}

const refreshReviewState = async () => {
  if (detail.value?.accepterId) {
    try {
      const r = await reviewApi.getUserRating(detail.value.accepterId)
      if (r.code === '200') ratingInfo.value = r.data || { avgRating: 0, reviewCount: 0 }
    } catch {}
  }
  try {
    const listRes = await reviewApi.getErrandReviews(id, 1, 5)
    if (listRes.code === '200') reviews.value = listRes.data?.records || []
  } catch {}
  if (isPublisher.value && detail.value?.status === 3) {
    try {
      const checkRes = await reviewApi.checkErrandReviewed(id)
      if (checkRes.code === '200') reviewed.value = !!checkRes.data
    } catch {}
  } else {
    reviewed.value = false
  }
  if (route.query.review && canReview.value) {
    reviewDialog.value = true
  }
}

const submitReview = async () => {
  if (!detail.value?.accepterId) return
  if (!reviewForm.rating) return ElMessage.warning(t('errand.review.selectRating'))
  reviewSubmitting.value = true
  try {
    const res = await reviewApi.createErrandReview({
      campusServiceId: Number(id),
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.reviewed'))
      reviewDialog.value = false
      reviewed.value = true
      await refreshReviewState()
    } else {
      ElMessage.error(res.message || t('errand.toast.reviewFail'))
    }
  } finally {
    reviewSubmitting.value = false
  }
}

const accept = async () => {
  try {
    await ElMessageBox.confirm(t('errand.confirm.accept'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.accept(id)
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.accepted'))
      fetchDetail()
    } else {
      ElMessage.error(res.message || t('errand.toast.acceptFail'))
    }
  } catch {}
}

const complete = async () => {
  try {
    await ElMessageBox.confirm(t('errand.confirm.complete'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.complete(id)
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.completed'))
      fetchDetail()
    }
  } catch {}
}

const cancel = async () => {
  try {
    await ElMessageBox.confirm(t('errand.confirm.cancel'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.cancel(id)
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.canceled'))
      fetchDetail()
    }
  } catch {}
}

onMounted(fetchDetail)
</script>

<style scoped>
.step {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  margin-top: 6px;
  flex-shrink: 0;
}
</style>

