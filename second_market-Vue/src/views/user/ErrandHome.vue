<template>
  <div class="min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-primary text-white px-4 py-3 flex items-center justify-between shadow-sm">
      <div class="flex items-center gap-3">
        <el-icon class="text-xl cursor-pointer active:scale-90 transition-transform" @click="router.back()"><ArrowLeft /></el-icon>
        <h1 class="text-lg font-bold">{{ $t('errand.homeTitle') }}</h1>
      </div>
      <LangSwitcher glass />
    </div>

    <div class="px-4 py-4 space-y-4">
      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-4">
        <div class="text-sm font-bold text-gray-900">{{ $t('errand.entriesTitle') }}</div>
        <div class="mt-3 grid grid-cols-3 gap-3">
          <button class="entry" @click="goPublish('buy')">
            <div class="entry-icon bg-primary/10 text-primary">买</div>
            <div class="entry-title">{{ $t('errand.entries.buy') }}</div>
          </button>
          <button class="entry" @click="goPublish('pick')">
            <div class="entry-icon bg-primary/10 text-primary">取</div>
            <div class="entry-title">{{ $t('errand.entries.pick') }}</div>
          </button>
          <button class="entry" @click="goPublish('deliver')">
            <div class="entry-icon bg-primary/10 text-primary">送</div>
            <div class="entry-title">{{ $t('errand.entries.deliver') }}</div>
          </button>
        </div>
      </div>

      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-gray-900">{{ $t('errand.myErrand') }}</div>
          <button class="text-sm text-primary font-bold" @click="router.push('/user/errand/orders')">{{ $t('common.view') }}</button>
        </div>
        <div class="mt-3 grid grid-cols-2 gap-3">
          <button class="quick" @click="router.push('/user/errand/orders?tab=publish')">
            <div class="text-xs text-gray-500">{{ $t('errand.stats.published') }}</div>
            <div class="mt-1 text-base font-black text-gray-900">{{ publishCount }}</div>
          </button>
          <button class="quick" @click="router.push('/user/errand/orders?tab=accept')">
            <div class="text-xs text-gray-500">{{ $t('errand.stats.accepted') }}</div>
            <div class="mt-1 text-base font-black text-gray-900">{{ acceptCount }}</div>
          </button>
        </div>
      </div>

      <div class="bg-white rounded-3xl border border-gray-100 shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div class="text-sm font-bold text-gray-900">{{ $t('errand.schoolErrandList') }}</div>
          <button class="text-sm text-primary font-bold" @click="router.push('/user/campus-nearby')">{{ $t('common.more') }}</button>
        </div>
        <div v-loading="loading" class="mt-3 space-y-3">
          <div v-for="s in list" :key="s.id" class="rounded-2xl border border-gray-100 p-4" @click="router.push(`/user/errand/detail/${s.id}`)">
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
          </div>
          <el-empty v-if="!loading && list.length === 0" :description="$t('errand.empty')" />
        </div>
      </div>
    </div>

    <div class="fixed bottom-24 right-6 w-14 h-14 bg-primary rounded-full shadow-lg shadow-cyan-soft flex items-center justify-center text-white text-2xl active:scale-90 transition-all z-50 cursor-pointer border-4 border-white"
      @click="goPublish('buy')">
      <el-icon><Plus /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { campusServiceApi } from '@/api/campus-service'
import { useUserStore } from '@/stores/user'
import { useSchoolStore } from '@/stores/school'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const userStore = useUserStore()
const schoolStore = useSchoolStore()
const { t } = useI18n()

const loading = ref(false)
const list = ref([])
const publishCount = ref(0)
const acceptCount = ref(0)

const statusText = (s) => {
  if (s === 1) return t('errand.status.waiting')
  if (s === 2) return t('errand.status.running')
  if (s === 3) return t('errand.status.done')
  if (s === 4) return t('errand.status.canceled')
  return t('errand.status.unknown')
}

const goPublish = (subType) => {
  router.push({ path: '/user/errand/publish', query: { subType: String(subType || 'buy') } })
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 20, type: 1 }
    if (schoolStore.selectedSchool) params.schoolId = Number(schoolStore.selectedSchool)
    const res = await campusServiceApi.getList(params)
    if (res.code === '200') {
      const records = res.data?.records || []
      list.value = records.filter((s) => s.status === 1 || s.status === 2)
      const me = userStore.user?.id
      publishCount.value = records.filter((s) => String(s.userId) === String(me)).length
      acceptCount.value = records.filter((s) => String(s.accepterId) === String(me)).length
    }
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 92px;
  border-radius: 18px;
  border: 1px solid rgba(241, 245, 249, 0.9);
  background: #fff;
}

.entry:active {
  transform: scale(0.98);
}

.entry-icon {
  width: 40px;
  height: 40px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
}

.entry-title {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 800;
  color: #0f172a;
}

.quick {
  border-radius: 18px;
  border: 1px solid rgba(241, 245, 249, 0.9);
  background: #fff;
  padding: 14px 14px;
  text-align: left;
}
</style>

