<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('errand.ordersTitle') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-4 pt-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-1 flex">
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'publish' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'publish'">{{ $t('errand.tabs.myPublish') }}</button>
        <button class="flex-1 h-10 rounded-xl text-sm font-bold transition" :class="tab === 'accept' ? 'bg-primary/10 text-primary' : 'text-gray-500'" @click="tab = 'accept'">{{ $t('errand.tabs.myAccept') }}</button>
      </div>
    </div>

    <div class="px-4 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-gray-900">{{ $t('errand.incomeStats') }}</div>
          <div class="text-lg font-black text-primary">¥{{ income.toFixed(2) }}</div>
        </div>
        <div class="mt-2 text-xs text-gray-500">{{ $t('errand.incomeHint') }}</div>
      </div>

      <div v-loading="loading" class="space-y-3">
        <div
          v-for="s in filtered"
          :key="s.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
          @click="router.push(`/user/errand/detail/${s.id}`)"
        >
          <div class="flex items-start justify-between gap-3">
            <div class="min-w-0">
              <div class="text-sm font-bold text-gray-900 truncate">{{ s.title }}</div>
              <div class="mt-1 text-[11px] text-gray-500 line-clamp-2">{{ s.content }}</div>
            </div>
            <div class="text-[10px] px-2 py-1 rounded-full bg-primary/10 text-primary font-bold">{{ statusText(s.status) }}</div>
          </div>
          <div class="mt-2 flex items-center justify-between text-[11px] text-gray-500">
            <div class="truncate">{{ s.location || '校内' }}</div>
            <div v-if="Number(s.reward) > 0" class="font-black text-primary">¥{{ s.reward }}</div>
          </div>

          <div class="mt-3 flex justify-end gap-2" @click.stop>
            <el-button v-if="tab === 'publish' && s.status === 1" size="small" plain @click="cancel(s.id)">{{ $t('errand.actions.cancel') }}</el-button>
            <el-button v-if="tab === 'publish' && s.status === 2" size="small" type="success" @click="complete(s.id)">{{ $t('errand.actions.confirmComplete') }}</el-button>
            <el-button v-if="tab === 'publish' && s.status === 3 && s.accepterId" size="small" type="primary" plain @click="toReview(s.id)">{{ $t('errand.actions.toReview') }}</el-button>
            <el-button v-if="tab === 'accept' && s.status === 2" size="small" type="success" @click="complete(s.id)">{{ $t('errand.actions.delivered') }}</el-button>
          </div>
        </div>

        <el-empty v-if="!loading && filtered.length === 0" :description="$t('common.noData')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { campusServiceApi } from '@/api/campus-service'
import { useUserStore } from '@/stores/user'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { t } = useI18n()

const tab = ref(route.query.tab ? String(route.query.tab) : 'publish')
const loading = ref(false)
const list = ref([])

const statusText = (s) => {
  if (s === 1) return t('errand.status.waiting')
  if (s === 2) return t('errand.status.running')
  if (s === 3) return t('errand.status.done')
  if (s === 4) return t('errand.status.canceled')
  return t('errand.status.unknown')
}

const filtered = computed(() => {
  const me = userStore.user?.id
  if (!me) return []
  if (tab.value === 'accept') return list.value.filter((s) => String(s.accepterId) === String(me))
  return list.value.filter((s) => String(s.userId) === String(me))
})

const income = computed(() => {
  const me = userStore.user?.id
  if (!me) return 0
  return list.value
    .filter((s) => String(s.accepterId) === String(me) && s.status === 3)
    .reduce((sum, s) => sum + Number(s.reward || 0), 0)
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await campusServiceApi.getList({ pageNum: 1, pageSize: 200, type: 1 })
    if (res.code === '200') {
      list.value = res.data?.records || []
    }
  } finally {
    loading.value = false
  }
}

const cancel = async (id) => {
  try {
    await ElMessageBox.confirm(t('errand.confirm.cancelOne'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.cancel(id)
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.canceled'))
      fetchList()
    }
  } catch {}
}

const complete = async (id) => {
  try {
    await ElMessageBox.confirm(t('errand.confirm.completeOne'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.complete(id)
    if (res.code === '200') {
      ElMessage.success(t('errand.toast.completed'))
      fetchList()
    }
  } catch {}
}

const toReview = (id) => {
  router.push({ path: `/user/errand/detail/${id}`, query: { review: '1' } })
}

onMounted(fetchList)
</script>

