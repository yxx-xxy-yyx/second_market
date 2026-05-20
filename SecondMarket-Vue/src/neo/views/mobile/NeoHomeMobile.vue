<template>
  <div class="space-y-5">
    <div class="rounded-2xl overflow-hidden border border-gray-100 bg-white shadow-sm">
      <div class="px-4 pt-6 pb-5 bg-gradient-to-br from-[rgba(var(--primary-rgb),0.96)] to-[rgba(var(--secondary-rgb),0.92)] text-white">
        <div class="text-[18px] font-semibold tracking-wide">{{ $t('neo.home.heroTitle') }}</div>
        <div class="mt-2 text-[12px] text-white/85 leading-relaxed">{{ $t('neo.home.heroDesc') }}</div>
        <div class="mt-4 flex items-center gap-2">
          <el-input v-model="keyword" :placeholder="$t('neo.home.searchPlaceholder')" class="flex-1" @keyup.enter="goSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" class="!border-white/20 !bg-white/15 hover:!bg-white/20" @click="goSearch">
            {{ $t('neo.home.search') }}
          </el-button>
        </div>
      </div>
      <div class="px-4 py-4 flex items-center justify-between">
        <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.aiPick') }}</div>
        <el-button text class="!text-[color:var(--primary-color)]" @click="refresh">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
      <div class="px-4 pb-5">
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div class="flex gap-3 overflow-hidden">
              <div v-for="n in 3" :key="n" class="h-[148px] w-[180px] shrink-0 rounded-2xl bg-gray-100" />
            </div>
          </template>
          <template #default>
            <div class="flex gap-3 overflow-x-auto pb-1">
              <button
                v-for="p in aiRecommended.slice(0, 10)"
                :key="p.id"
                class="w-[180px] shrink-0 rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden text-left active:scale-[0.99] transition"
                @click="goDetail(p.id)"
              >
                <img :src="getProductImage(p.images)" class="w-full h-[92px] object-cover bg-gray-50" @error="handleImgError" />
                <div class="p-3">
                  <div class="text-[13px] font-semibold text-gray-900 line-clamp-2 min-h-[34px]">{{ p.title }}</div>
                  <div class="mt-2 text-[14px] font-bold text-[color:var(--primary-color)]">{{ currency }}{{ p.price }}</div>
                </div>
              </button>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
      <div class="px-4 py-4 flex items-center justify-between">
        <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.hot') }}</div>
        <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/user/products')">
          {{ $t('neo.home.more') }}
        </el-button>
      </div>
      <div class="px-4 pb-5 space-y-3">
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div v-for="n in 5" :key="n" class="h-[56px] rounded-xl bg-gray-100" />
          </template>
          <template #default>
            <button
              v-for="p in hotProducts.slice(0, 6)"
              :key="p.id"
              class="w-full flex items-center gap-3 rounded-xl border border-gray-100 active:bg-gray-50 transition px-3 py-2 text-left"
              @click="goDetail(p.id)"
            >
              <img :src="getProductImage(p.images)" class="h-12 w-12 rounded-xl object-cover bg-gray-50" @error="handleImgError" />
              <div class="min-w-0 flex-1">
                <div class="text-[13px] font-semibold text-gray-900 truncate">{{ p.title }}</div>
                <div class="mt-1 text-[12px] text-gray-500">
                  {{ currency }}{{ p.price }} · {{ $t('neo.home.views') }} {{ p.viewCount || 0 }}
                </div>
              </div>
              <el-icon class="text-gray-300"><ArrowRight /></el-icon>
            </button>
          </template>
        </el-skeleton>
      </div>
    </div>

    <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
      <div class="px-4 py-4 flex items-center justify-between">
        <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.campusPick') }}</div>
        <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/user/products')">
          {{ $t('neo.home.more') }}
        </el-button>
      </div>
      <div class="px-4 pb-5 grid grid-cols-2 gap-3">
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div v-for="n in 4" :key="n" class="h-[148px] rounded-2xl bg-gray-100" />
          </template>
          <template #default>
            <button
              v-for="p in campusProducts.slice(0, 4)"
              :key="p.id"
              class="rounded-2xl border border-gray-100 overflow-hidden active:scale-[0.99] transition text-left"
              @click="goDetail(p.id)"
            >
              <img :src="getProductImage(p.images)" class="w-full h-[92px] object-cover bg-gray-50" @error="handleImgError" />
              <div class="p-3">
                <div class="text-[13px] font-semibold text-gray-900 line-clamp-1">{{ p.title }}</div>
                <div class="mt-1 text-[12px] text-gray-500 truncate">{{ currency }}{{ p.price }}</div>
              </div>
            </button>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useNeoHomeFeed } from '@/neo/neo.home.feed'
import { ArrowRight, Refresh, Search } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()

const { loading, aiRecommended, hotProducts, campusProducts, refresh } = useNeoHomeFeed()

const keyword = ref('')
const currency = t('common.currency')

const goSearch = () => {
  const q = keyword.value?.trim()
  router.push({ path: '/user/products', query: q ? { keyword: q } : {} })
}

const goDetail = (id) => {
  router.push(`/user/product/${id}`)
}

const DEFAULT_IMAGE = 'https://via.placeholder.com/300x300?text=No+Image'
const getProductImage = (images) => {
  if (!images) return DEFAULT_IMAGE
  try {
    const arr = typeof images === 'string' ? JSON.parse(images) : images
    return arr?.[0] || DEFAULT_IMAGE
  } catch {
    return DEFAULT_IMAGE
  }
}

const handleImgError = (e) => {
  if (e?.target) e.target.src = DEFAULT_IMAGE
}
</script>

