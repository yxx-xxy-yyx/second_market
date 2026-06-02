<template>
  <div class="settings-container min-h-screen pb-24" :style="{ backgroundColor: 'var(--bg-color)'">
    <div class="sticky top-0 z-50 backdrop-blur border-b px-4 py-3 flex items-center justify-between"
         :style="{ backgroundColor: 'rgba(255,255,255,0.9)', borderColor: 'var(--border-color)' }">
      <div class="flex items-center gap-3">
        <el-icon :size="20" @click="router.back()" :style="{ color: 'var(--text-primary)'"><ArrowLeft /></el-icon>
        <div class="text-base font-bold" :style="{ color: 'var(--text-primary)'">{{ $t('settings.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-3 py-4 space-y-3">
      <!-- 外观设置 -->
      <div class="rounded-2xl border shadow-sm overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell" @click="router.push('/user/profileEdit')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><User /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.accountProfile') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/address')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><MapLocation /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.address') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/verify')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Stamp /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.verify') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/credit')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Odometer /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.credit') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 外观设置 -->
      <div class="rounded-2xl border shadow-sm overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Moon /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">深色模式</div>
          </div>
          <el-switch v-model="isDark" @change="toggleTheme" />
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="rounded-2xl border shadow-sm overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell" @click="router.push('/user/privacy')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Document /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.privacy') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/terms')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Tickets /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.terms') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/feedback')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><ChatLineRound /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.feedback') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="rounded-2xl border shadow-sm overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <button class="w-full h-12 text-red-500 font-bold" @click="logout">{{ $t('settings.logout') }}</button>
      </div>

      <div class="rounded-2xl border shadow-sm overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <button class="w-full h-12 font-bold" :style="{ color: 'var(--text-secondary)'}" @click="deactivate">{{ $t('settings.deactivate') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { computed } from 'vue'
import { ArrowLeft, ArrowRight, User, MapLocation, Stamp, Odometer, Document, Tickets, ChatLineRound, Moon } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const { t } = useI18n()

const isDark = computed({
  get: () => themeStore.isDark,
  set: (val) => {
    if (val !== themeStore.isDark) {
      themeStore.toggleTheme()
    }
  }
})

const toggleTheme = () => {
  themeStore.toggleTheme()
}

const logout = async () => {
  try {
    await ElMessageBox.confirm(t('settings.confirmLogout'), t('common.tip'), { type: 'warning' })
    userStore.logout()
    router.replace('/login')
  } catch {}
}

const deactivate = async () => {
  try {
    await ElMessageBox.confirm(t('settings.deactivateTip'), t('settings.deactivateTitle'), { type: 'warning' })
    userStore.logout()
    localStorage.removeItem('sm_forum_fav_v1')
    localStorage.removeItem('sm_content_history_v1')
    localStorage.removeItem('sm_cart_v1')
    ElMessage.success(t('settings.clearedLocal'))
    router.replace('/login')
  } catch {}
}
</script>

<style scoped>
.cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid var(--border-light);
}

.cell:last-child {
  border-bottom: none;
}

.cell-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.icon {
  width: 38px;
  height: 38px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.cell-title {
  font-size: 14px;
  font-weight: 700;
}
</style>

