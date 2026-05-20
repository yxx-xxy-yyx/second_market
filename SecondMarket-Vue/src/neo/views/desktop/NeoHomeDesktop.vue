<template>
  <div class="grid grid-cols-[1fr_360px] gap-5">
    <section class="space-y-5 min-w-0">
      <div class="rounded-2xl overflow-hidden border border-gray-100 bg-white shadow-sm">
        <div class="px-6 py-6 bg-gradient-to-br from-[rgba(var(--primary-rgb),0.96)] to-[rgba(var(--secondary-rgb),0.92)] text-white">
          <div class="flex items-start justify-between gap-6">
            <div class="min-w-0">
              <div class="text-[22px] font-semibold tracking-wide">{{ $t('neo.home.heroTitle') }}</div>
              <div class="mt-2 text-sm text-white/85 leading-relaxed">{{ $t('neo.home.heroDesc') }}</div>
              <div class="mt-5 flex items-center gap-3">
                <el-input
                  v-model="keyword"
                  :placeholder="$t('neo.home.searchPlaceholder')"
                  class="w-[520px]"
                  @keyup.enter="goSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" class="!border-white/20 !bg-white/15 hover:!bg-white/20" @click="goSearch">
                  {{ $t('neo.home.search') }}
                </el-button>
              </div>
            </div>

            <div class="shrink-0 flex items-center gap-2">
              <el-button text class="!text-white/90 hover:!text-white" @click="refresh">
                <el-icon><Refresh /></el-icon>
                {{ $t('neo.home.refresh') }}
              </el-button>
            </div>
          </div>
        </div>

        <div class="px-6 py-5">
          <div class="flex items-center justify-between">
            <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.aiPick') }}</div>
            <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/neo/home')">
              {{ $t('neo.home.viewAll') }}
            </el-button>
          </div>

          <div class="mt-4">
            <el-skeleton :loading="loading" animated>
              <template #template>
                <div class="grid grid-cols-5 gap-3">
                  <div v-for="n in 5" :key="n" class="h-[160px] rounded-2xl bg-gray-100" />
                </div>
              </template>
              <template #default>
                <div class="grid grid-cols-5 gap-3">
                  <button
                    v-for="p in aiRecommended.slice(0, 5)"
                    :key="p.id"
                    class="group rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden text-left hover:shadow-md transition"
                    @click="goDetail(p.id)"
                  >
                    <div class="relative">
                      <img
                        :src="getProductImage(p.images)"
                        class="w-full h-[96px] object-cover bg-gray-50"
                        @error="handleImgError"
                      />
                      <div
                        v-if="p.aiAnalyzed === 1 || p.aiAnalyzed === true"
                        class="absolute top-2 left-2 px-2 py-1 rounded-full text-[11px] font-semibold bg-white/90 text-[color:var(--primary-color)]"
                      >
                        AI
                      </div>
                    </div>
                    <div class="p-3">
                      <div class="text-[13px] font-semibold text-gray-900 line-clamp-2 min-h-[34px]">
                        {{ p.title }}
                      </div>
                      <div class="mt-2 flex items-center justify-between">
                        <div class="text-[14px] font-bold text-[color:var(--primary-color)]">
                          {{ currency }}{{ p.price }}
                        </div>
                        <div class="text-[11px] text-gray-400">
                          {{ $t('neo.home.views') }} {{ p.viewCount || 0 }}
                        </div>
                      </div>
                    </div>
                  </button>
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-5">
        <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
          <div class="px-5 py-4 flex items-center justify-between">
            <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.hot') }}</div>
            <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/user/products')">
              {{ $t('neo.home.more') }}
            </el-button>
          </div>
          <div class="px-5 pb-5">
            <el-skeleton :loading="loading" animated>
              <template #template>
                <div class="space-y-3">
                  <div v-for="n in 5" :key="n" class="h-[58px] rounded-xl bg-gray-100" />
                </div>
              </template>
              <template #default>
                <div class="space-y-3">
                  <button
                    v-for="p in hotProducts.slice(0, 6)"
                    :key="p.id"
                    class="w-full flex items-center gap-3 rounded-xl border border-gray-100 hover:border-[rgba(var(--primary-rgb),0.35)] hover:bg-[rgba(var(--primary-rgb),0.05)] transition px-3 py-2 text-left"
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
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>

        <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
          <div class="px-5 py-4 flex items-center justify-between">
            <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.campusPick') }}</div>
            <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/user/products')">
              {{ $t('neo.home.more') }}
            </el-button>
          </div>
          <div class="px-5 pb-5 grid grid-cols-2 gap-3">
            <el-skeleton :loading="loading" animated>
              <template #template>
                <div v-for="n in 4" :key="n" class="h-[148px] rounded-2xl bg-gray-100" />
              </template>
              <template #default>
                <button
                  v-for="p in campusProducts.slice(0, 4)"
                  :key="p.id"
                  class="rounded-2xl border border-gray-100 overflow-hidden hover:shadow-md transition text-left"
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
    </section>

    <aside class="space-y-5">
      <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
        <div class="px-5 py-4 text-sm font-semibold text-gray-900">{{ $t('neo.home.quick') }}</div>
        <div class="px-5 pb-5 grid grid-cols-2 gap-3">
          <button class="neo-quick" @click="router.push('/neo/cart')">
            <el-icon :size="18"><ShoppingCart /></el-icon>
            <span>{{ $t('neo.tabs.cart') }}</span>
          </button>
          <button class="neo-quick" @click="router.push('/neo/orders/pending-pay')">
            <el-icon :size="18"><Document /></el-icon>
            <span>{{ $t('neo.home.orders') }}</span>
          </button>
          <button class="neo-quick" @click="router.push('/neo/chat')">
            <el-icon :size="18"><ChatDotRound /></el-icon>
            <span>{{ $t('neo.home.chat') }}</span>
          </button>
          <button class="neo-quick" @click="router.push('/neo/seller-center')">
            <el-icon :size="18"><DataAnalysis /></el-icon>
            <span>{{ $t('neo.home.seller') }}</span>
          </button>
        </div>
      </div>

      <div class="rounded-2xl border border-gray-100 bg-white shadow-sm overflow-hidden">
        <div class="px-5 py-4 flex items-center justify-between">
          <div class="text-sm font-semibold text-gray-900">{{ $t('neo.home.cityPick') }}</div>
          <el-button text class="!text-[color:var(--primary-color)]" @click="router.push('/user/products')">
            {{ $t('neo.home.more') }}
          </el-button>
        </div>
        <div class="px-5 pb-5 space-y-3">
          <el-skeleton :loading="loading" animated>
            <template #template>
              <div v-for="n in 6" :key="n" class="h-[56px] rounded-xl bg-gray-100" />
            </template>
            <template #default>
              <button
                v-for="p in cityProducts.slice(0, 6)"
                :key="p.id"
                class="w-full flex items-center gap-3 rounded-xl border border-gray-100 hover:border-[rgba(var(--primary-rgb),0.35)] hover:bg-[rgba(var(--primary-rgb),0.05)] transition px-3 py-2 text-left"
                @click="goDetail(p.id)"
              >
                <img :src="getProductImage(p.images)" class="h-12 w-12 rounded-xl object-cover bg-gray-50" @error="handleImgError" />
                <div class="min-w-0 flex-1">
                  <div class="text-[13px] font-semibold text-gray-900 truncate">{{ p.title }}</div>
                  <div class="mt-1 text-[12px] text-gray-500 truncate">{{ currency }}{{ p.price }}</div>
                </div>
                <el-icon class="text-gray-300"><ArrowRight /></el-icon>
              </button>
            </template>
          </el-skeleton>
        </div>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useNeoHomeFeed } from '@/neo/neo.home.feed'
import {
  ArrowRight,
  ChatDotRound,
  DataAnalysis,
  Document,
  Refresh,
  Search,
  ShoppingCart
} from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()

const { loading, aiRecommended, hotProducts, campusProducts, cityProducts, refresh } = useNeoHomeFeed()

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

<style scoped>
.neo-quick {
  height: 52px;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.9);
  background: rgba(248, 250, 252, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 13px;
  font-weight: 700;
  color: #0f172a;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.06s ease;
}

.neo-quick:active {
  transform: scale(0.99);
}

.neo-quick:hover {
  border-color: rgba(var(--primary-rgb), 0.35);
  background: rgba(var(--primary-rgb), 0.06);
}
</style>

