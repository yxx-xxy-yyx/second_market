<template>
  <div class="min-h-[100dvh] bg-[var(--bg-color)]">
    <header class="sticky top-0 z-[80] bg-white/80 backdrop-blur border-b border-gray-100">
      <div class="mx-auto max-w-[1400px] px-3 md:px-4 h-[56px] flex items-center justify-between">
        <button class="min-w-0 flex items-center gap-2" @click="router.push('/neo/home')">
          <BrandLogo :title="$t('neo.brand')" class="text-[color:var(--primary-color)]" />
        </button>

        <div class="hidden md:flex flex-1 justify-center px-4">
          <div class="neo-pillbar">
            <button class="neo-pill" :class="{ active: isActive('/neo/home') }" @click="router.push('/neo/home')">
              <el-icon :size="18"><House /></el-icon>
              <span class="neo-pill-label">{{ $t('neo.tabs.home') }}</span>
            </button>
            <button class="neo-pill" :class="{ active: isActive('/neo/errands') }" @click="router.push('/neo/errands')">
              <el-icon :size="18"><Promotion /></el-icon>
              <span class="neo-pill-label">{{ $t('neo.tabs.errands') }}</span>
            </button>
            <button class="neo-pill" :class="{ active: isActive('/neo/forum') }" @click="router.push('/neo/forum')">
              <el-icon :size="18"><ChatLineRound /></el-icon>
              <span class="neo-pill-label">{{ $t('neo.tabs.forum') }}</span>
            </button>
            <button class="neo-pill" :class="{ active: isActive('/user/favorites') }" @click="router.push('/user/favorites')">
              <el-icon :size="18"><Star /></el-icon>
              <span class="neo-pill-label">{{ $t('neo.tabs.favorites') }}</span>
            </button>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <el-select
            v-model="selectedSchool"
            :placeholder="$t('nav.selectSchool')"
            class="w-[110px] hidden md:block"
            filterable
            @change="handleSchoolChange"
          >
            <el-option
              v-for="item in schoolStore.schoolList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <LangSwitcher class="hidden md:block" />
          <el-button text class="min-w-[44px] min-h-[44px]" @click="router.push('/user/dashboard')">
            <el-icon :size="20"><House /></el-icon>
          </el-button>
        </div>
      </div>
    </header>

    <main
      class="mx-auto max-w-[1400px] px-3 md:px-4 py-4"
      :style="isMobileScreen ? { paddingBottom: 'calc(72px + env(safe-area-inset-bottom))' } : {}"
    >
      <router-view v-slot="{ Component, route }">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </main>

    <nav
      v-if="isMobileScreen"
      class="fixed bottom-0 left-0 right-0 z-[90] bg-white/90 backdrop-blur border-t border-gray-100"
      :style="{ paddingBottom: 'env(safe-area-inset-bottom)' }"
    >
      <div class="h-[64px] flex items-center justify-around">
        <button class="neo-tab" :class="{ active: isActive('/neo/home') }" @click="router.push('/neo/home')">
          <el-icon :size="22"><House /></el-icon>
          <span class="neo-tab-label">{{ $t('neo.tabs.home') }}</span>
        </button>
        <button
          class="neo-tab"
          :class="{ active: isActive('/neo/errands') }"
          @click="router.push('/neo/errands')"
        >
          <el-icon :size="22"><Promotion /></el-icon>
          <span class="neo-tab-label">{{ $t('neo.tabs.errands') }}</span>
        </button>
        <button
          class="neo-tab"
          :class="{ active: isActive('/neo/forum') }"
          @click="router.push('/neo/forum')"
        >
          <el-icon :size="22"><ChatLineRound /></el-icon>
          <span class="neo-tab-label">{{ $t('neo.tabs.forum') }}</span>
        </button>
        <button class="neo-tab" :class="{ active: isActive('/user/favorites') }" @click="router.push('/user/favorites')">
          <el-icon :size="22"><Star /></el-icon>
          <span class="neo-tab-label">{{ $t('neo.tabs.favorites') }}</span>
        </button>
      </div>
    </nav>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useSchoolStore } from '@/stores/school'
import BrandLogo from '@/components/BrandLogo.vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { ChatLineRound, House, Promotion, Star } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { isMobileScreen } = useDeviceType()

const schoolStore = useSchoolStore()

const selectedSchool = computed({
  get: () => schoolStore.selectedSchool,
  set: (value) => schoolStore.setSchool(value)
})

const handleSchoolChange = (value) => {
  schoolStore.setSchool(value)
}

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

onMounted(() => {
  if (!schoolStore.schoolList.length) schoolStore.getSchoolList()
})
</script>

<style scoped>
.neo-pillbar {
  display: flex;
  align-items: center;
  padding: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.06);
  backdrop-filter: blur(14px);
}

.neo-pill {
  height: 38px;
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border-radius: 999px;
  color: #64748b;
  transition: background-color 0.18s ease, color 0.18s ease, transform 0.06s ease;
}

.neo-pill:active {
  transform: scale(0.99);
}

.neo-pill.active {
  background: rgba(var(--primary-rgb), 0.12);
  color: var(--primary-color);
}

.neo-pill-label {
  font-size: 13px;
  line-height: 1;
  font-weight: 800;
}

.neo-tab {
  width: 25%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #64748b;
  transition: color 0.18s ease, transform 0.06s ease;
}

.neo-tab:active {
  transform: scale(0.99);
}

.neo-tab.active {
  color: var(--primary-color);
}

.neo-tab-label {
  margin-top: 4px;
  font-size: 11px;
  line-height: 1;
  font-weight: 600;
}
</style>
