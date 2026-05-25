<template>
  <div class="settings-container min-h-screen bg-gray-50 pb-24">
    <div class="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 px-4 py-3 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <el-icon :size="20" class="text-gray-700" @click="router.back()"><ArrowLeft /></el-icon>
        <div class="text-base font-bold text-gray-900">{{ $t('settings.title') }}</div>
      </div>
      <LangSwitcher />
    </div>

    <div class="px-3 py-4 space-y-3">
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="cell" @click="router.push('/user/profileEdit')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><User /></el-icon></div>
            <div class="cell-title">{{ $t('settings.accountProfile') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/address')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><MapLocation /></el-icon></div>
            <div class="cell-title">{{ $t('settings.address') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/verify')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Stamp /></el-icon></div>
            <div class="cell-title">{{ $t('settings.verify') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/credit')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Odometer /></el-icon></div>
            <div class="cell-title">{{ $t('settings.credit') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <div class="cell" @click="router.push('/user/privacy')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Document /></el-icon></div>
            <div class="cell-title">{{ $t('settings.privacy') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/terms')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><Tickets /></el-icon></div>
            <div class="cell-title">{{ $t('settings.terms') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
        <div class="cell" @click="router.push('/user/feedback')">
          <div class="cell-left">
            <div class="icon bg-primary/10 text-primary"><el-icon><ChatLineRound /></el-icon></div>
            <div class="cell-title">{{ $t('settings.feedback') }}</div>
          </div>
          <el-icon class="text-gray-300"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <button class="w-full h-12 text-red-500 font-bold" @click="logout">{{ $t('settings.logout') }}</button>
      </div>

      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
        <button class="w-full h-12 text-gray-500 font-bold" @click="deactivate">{{ $t('settings.deactivate') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ArrowLeft, ArrowRight, User, MapLocation, Stamp, Odometer, Document, Tickets, ChatLineRound } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

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
  border-bottom: 1px solid rgba(241, 245, 249, 0.9);
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
  color: #0f172a;
}
</style>

