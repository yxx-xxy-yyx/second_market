<template>
  <div class="my-publish-container">
    <template v-if="isMobileScreen">
      <div class="min-h-screen bg-gray-50 pb-24">
        <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
            <div class="text-base font-bold text-gray-900">{{ $t('myPublishPage.title') }}</div>
          </div>
          <LangSwitcher />
        </div>

        <div class="px-3 py-3">
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-1 flex">
            <button
              v-for="t in tabs"
              :key="t.value"
              class="flex-1 h-10 rounded-xl text-sm font-bold transition"
              :class="activeTab === t.value ? 'bg-primary/10 text-primary' : 'text-gray-500'"
              @click="activeTab = t.value"
            >
              {{ t.label }}
            </button>
          </div>
        </div>

        <div class="px-3 pb-24">
          <div v-if="activeTab === 'product'" class="space-y-3">
            <div class="flex items-center justify-between px-1">
              <div class="text-sm font-semibold text-gray-900">{{ $t('myPublishPage.tabs.product') }}</div>
              <button class="text-sm text-primary" @click="router.push('/user/publish')">{{ $t('myPublishPage.goPublish') }}</button>
            </div>
            <div v-loading="loading" class="space-y-3">
              <div
                v-for="p in myProducts"
                :key="p.id"
                class="bg-white rounded-2xl border border-gray-100 shadow-sm p-3 flex gap-3"
                @click="router.push(`/user/product/${p.id}`)"
              >
                <div class="w-20 h-20 rounded-xl bg-gray-50 overflow-hidden flex-shrink-0">
                  <img :src="getProductImage(p.images)" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-semibold text-gray-900 line-clamp-2">{{ p.title }}</div>
                  <div class="mt-2 flex items-center justify-between">
                    <div class="text-base font-black text-primary">¥{{ p.price }}</div>
                    <div class="text-[11px] text-gray-400">{{ $t('product.condition') }} {{ p.conditionScore || '-' }}</div>
                  </div>
                </div>
              </div>
              <el-empty v-if="!loading && myProducts.length === 0" :description="$t('myPublishPage.emptyProduct')" />
            </div>
          </div>

          <div v-else-if="activeTab === 'service'" class="space-y-3">
            <div class="flex items-center justify-between px-1">
              <div class="text-sm font-semibold text-gray-900">{{ $t('myPublishPage.serviceSection') }}</div>
              <button class="text-sm text-primary" @click="router.push('/user/campus-nearby')">{{ $t('myPublishPage.goPublish') }}</button>
            </div>
            <div v-loading="loading" class="space-y-3">
              <div
                v-for="s in myServices"
                :key="s.id"
                class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
              >
                <div class="flex items-start justify-between gap-3">
                  <div class="min-w-0">
                    <div class="text-sm font-bold text-gray-900 truncate">{{ s.title }}</div>
                    <div class="mt-1 text-[11px] text-gray-500 line-clamp-2">{{ s.content }}</div>
                  </div>
                  <div class="text-[10px] px-2 py-1 rounded-full bg-primary/10 text-primary font-bold flex-shrink-0">
                    {{ statusText(s.status) }}
                  </div>
                </div>
                <div class="mt-3 flex items-center justify-between text-[11px] text-gray-500">
                  <div class="truncate">{{ typeText(s.type) }} · {{ s.location || '校内' }}</div>
                  <div v-if="Number(s.reward) > 0" class="font-bold text-primary">¥{{ s.reward }}</div>
                </div>
                <div class="mt-3 flex justify-end gap-2">
                  <el-button v-if="s.status === 1" size="small" plain @click="handleCancelService(s.id)">{{ $t('myPublishPage.cancel') }}</el-button>
                  <el-button v-if="s.status === 2" size="small" type="success" @click="handleCompleteService(s.id)">{{ $t('myPublishPage.complete') }}</el-button>
                </div>
              </div>
              <el-empty v-if="!loading && myServices.length === 0" :description="$t('myPublishPage.emptyService')" />
            </div>
          </div>

          <div v-else class="space-y-3">
            <div class="flex items-center justify-between px-1">
              <div class="text-sm font-semibold text-gray-900">{{ $t('myPublishPage.tabs.forum') }}</div>
              <button class="text-sm text-primary" @click="router.push('/user/forum/publish')">{{ $t('myPublishPage.goPublish') }}</button>
            </div>
            <div v-loading="loading" class="space-y-3">
              <div
                v-for="p in myPosts"
                :key="p.id"
                class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4"
                @click="router.push(`/user/forum/detail/${p.id}`)"
              >
                <div class="flex items-start justify-between gap-3">
                  <div class="min-w-0">
                    <div class="text-sm font-bold text-gray-900 line-clamp-2">{{ p.title }}</div>
                      <div class="mt-1 text-[11px] text-gray-500 truncate">{{ p.category || $t('myPublishPage.tabs.forum') }}</div>
                  </div>
                    <button class="text-xs text-red-500 flex-shrink-0" @click.stop="handleDeletePost(p.id)">{{ $t('myPublishPage.delete') }}</button>
                </div>
                <div class="mt-3 flex items-center justify-between text-[11px] text-gray-500">
                    <div class="truncate">{{ p.schoolName || $t('myPublishPage.forumAll') }}</div>
                    <div>{{ p.commentCount || 0 }} {{ $t('myPublishPage.comment') }}</div>
                </div>
              </div>
              <el-empty v-if="!loading && myPosts.length === 0" :description="$t('myPublishPage.emptyPost')" />
            </div>
          </div>
        </div>
      </div>
    </template>

    <template v-else>
      <div class="p-6 max-w-6xl mx-auto pb-24 space-y-6">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold">{{ $t('myPublishPage.title') }}</h1>
          <el-radio-group v-model="activeTab" size="small">
            <el-radio-button label="product">{{ $t('myPublishPage.tabs.product') }}</el-radio-button>
            <el-radio-button label="service">{{ $t('myPublishPage.tabs.service') }}</el-radio-button>
            <el-radio-button label="forum">{{ $t('myPublishPage.tabs.forum') }}</el-radio-button>
          </el-radio-group>
        </div>

        <el-card shadow="never">
          <div class="flex items-center justify-between mb-4">
            <div class="font-bold">
              {{ activeTab === 'product' ? $t('myPublishPage.tabs.product') : (activeTab === 'service' ? $t('myPublishPage.tabs.service') : $t('myPublishPage.tabs.forum')) }}
            </div>
            <el-button
              type="primary"
              plain
              @click="router.push(activeTab === 'product' ? '/user/publish' : (activeTab === 'service' ? '/user/campus-nearby' : '/user/forum/publish'))"
            >
              {{ $t('myPublishPage.goPublish') }}
            </el-button>
          </div>

          <div v-if="activeTab === 'product'" v-loading="loading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            <div v-for="p in myProducts" :key="p.id" class="rounded-xl border border-gray-100 overflow-hidden cursor-pointer" @click="router.push(`/user/product/${p.id}`)">
              <div class="aspect-square bg-gray-50">
                <img :src="getProductImage(p.images)" class="w-full h-full object-cover" />
              </div>
              <div class="p-3">
                <div class="text-sm font-semibold line-clamp-2">{{ p.title }}</div>
                <div class="mt-2 flex items-center justify-between">
                  <div class="font-black text-primary">¥{{ p.price }}</div>
                  <div class="text-xs text-gray-400">{{ $t('product.condition') }} {{ p.conditionScore || '-' }}</div>
                </div>
              </div>
            </div>
            <el-empty v-if="!loading && myProducts.length === 0" :description="$t('myPublishPage.emptyProduct')" />
          </div>

          <div v-else-if="activeTab === 'service'" v-loading="loading" class="space-y-3">
            <div v-for="s in myServices" :key="s.id" class="rounded-xl border border-gray-100 p-4">
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <div class="font-bold truncate">{{ s.title }}</div>
                  <div class="mt-1 text-sm text-gray-600 line-clamp-2">{{ s.content }}</div>
                </div>
                <div class="text-xs px-2 py-1 rounded-full bg-primary/10 text-primary font-bold">{{ statusText(s.status) }}</div>
              </div>
              <div class="mt-2 text-sm text-gray-500">{{ typeText(s.type) }} · {{ s.location || $t('profilePage.inCampus') }}</div>
              <div class="mt-3 flex justify-end gap-2">
                <el-button v-if="s.status === 1" size="small" plain @click="handleCancelService(s.id)">{{ $t('myPublishPage.cancel') }}</el-button>
                <el-button v-if="s.status === 2" size="small" type="success" @click="handleCompleteService(s.id)">{{ $t('myPublishPage.complete') }}</el-button>
              </div>
            </div>
            <el-empty v-if="!loading && myServices.length === 0" :description="$t('myPublishPage.emptyService')" />
          </div>

          <div v-else v-loading="loading" class="space-y-3">
            <div v-for="p in myPosts" :key="p.id" class="rounded-xl border border-gray-100 p-4 cursor-pointer" @click="router.push(`/user/forum/detail/${p.id}`)">
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <div class="font-bold line-clamp-2">{{ p.title }}</div>
                  <div class="mt-1 text-sm text-gray-500">{{ p.category || $t('myPublishPage.tabs.forum') }} · {{ p.schoolName || $t('myPublishPage.forumAll') }}</div>
                </div>
                <el-button type="danger" plain size="small" @click.stop="handleDeletePost(p.id)">{{ $t('myPublishPage.delete') }}</el-button>
              </div>
              <div class="mt-2 text-sm text-gray-500">{{ p.commentCount || 0 }} {{ $t('myPublishPage.comment') }}</div>
            </div>
            <el-empty v-if="!loading && myPosts.length === 0" :description="$t('myPublishPage.emptyPost')" />
          </div>
        </el-card>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useUserStore } from '@/stores/user'
import { productApi } from '@/api/product'
import { campusServiceApi } from '@/api/campus-service'
import { forumApi } from '@/api/forum'
import { formatProductImageUrl } from '@/utils/url'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { isMobileScreen } = useDeviceType()
const userStore = useUserStore()
const { t } = useI18n()

const tabs = computed(() => [
  { label: t('myPublishPage.tabs.product'), value: 'product' },
  { label: t('myPublishPage.tabs.service'), value: 'service' },
  { label: t('myPublishPage.tabs.forum'), value: 'forum' }
])

const activeTab = ref('product')
const loading = ref(false)
const myProducts = ref([])
const myServices = ref([])
const myPosts = ref([])

const getProductImage = (images) => {
  if (!images) return ''
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return formatProductImageUrl(Array.isArray(arr) ? arr[0] : arr)
  } catch {
    return formatProductImageUrl(images)
  }
}

const typeText = (type) => {
  if (type === 1) return t('myPublishPage.type.t1')
  if (type === 2) return t('myPublishPage.type.t2')
  if (type === 3) return t('myPublishPage.type.t3')
  return t('myPublishPage.type.other')
}

const statusText = (s) => {
  if (s === 1) return t('errand.status.waiting')
  if (s === 2) return t('errand.status.running')
  if (s === 3) return t('errand.status.done')
  if (s === 4) return t('errand.status.canceled')
  return t('errand.status.unknown')
}

const fetchProducts = async () => {
  const res = await productApi.getMyProducts({ pageNum: 1, pageSize: 50 })
  if (res.code === '200') myProducts.value = res.data?.records || res.data || []
}

const fetchServices = async () => {
  const res = await campusServiceApi.getList({ pageNum: 1, pageSize: 100 })
  const records = res?.data?.records || res?.data || []
  const me = userStore.user?.id
  myServices.value = Array.isArray(records) ? records.filter((s) => String(s.userId) === String(me)) : []
}

const fetchPosts = async () => {
  const res = await forumApi.getList({ current: 1, size: 100 })
  if (res.code !== '200') {
    myPosts.value = []
    return
  }
  const records = res.data?.records || res.data || []
  const me = userStore.user?.id
  myPosts.value = Array.isArray(records) ? records.filter((p) => String(p.userId) === String(me)) : []
}

const reload = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'product') await fetchProducts()
    else if (activeTab.value === 'service') await fetchServices()
    else await fetchPosts()
  } catch (e) {
    ElMessage.error(t('myPublishPage.loadFail'))
  } finally {
    loading.value = false
  }
}

const handleCancelService = async (id) => {
  try {
    await ElMessageBox.confirm(t('myPublishPage.confirmCancel'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.cancel(id)
    if (res.success || res.code === '200') {
      ElMessage.success(t('myPublishPage.canceled'))
      reload()
    }
  } catch {}
}

const handleCompleteService = async (id) => {
  try {
    await ElMessageBox.confirm(t('myPublishPage.confirmComplete'), t('common.tip'), { type: 'warning' })
    const res = await campusServiceApi.complete(id)
    if (res.success || res.code === '200') {
      ElMessage.success(t('myPublishPage.completed'))
      reload()
    }
  } catch {}
}

const handleDeletePost = async (id) => {
  try {
    await ElMessageBox.confirm(t('myPublishPage.confirmDeletePost'), t('common.tip'), { type: 'warning' })
    const res = await forumApi.delete(id)
    if (res.code === '200') {
      ElMessage.success(t('myPublishPage.deleted'))
      reload()
    } else {
      ElMessage.error(res.message || t('myPublishPage.deleteFail'))
    }
  } catch {}
}

onMounted(() => reload())
watch(activeTab, () => reload())
</script>

