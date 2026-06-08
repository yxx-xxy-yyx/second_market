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

    <div class="px-3 py-4 space-y-4">
      <!-- 账户设置 -->
      <div class="rounded-2xl border shadow-md overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/profileEdit')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-green-400 to-emerald-500 text-white shadow-md"><el-icon><User /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.accountProfile') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/address')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-blue-400 to-cyan-500 text-white shadow-md"><el-icon><MapLocation /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.address') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="showPasswordModal = true">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-red-400 to-rose-500 text-white shadow-md"><el-icon><Lock /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">修改密码</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/verify')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-purple-400 to-violet-500 text-white shadow-md"><el-icon><Stamp /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.verify') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/credit')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-amber-400 to-orange-500 text-white shadow-md"><el-icon><Odometer /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.credit') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
      </div>

      <!-- 密码修改弹窗 -->
      <div class="password-modal-overlay" v-if="showPasswordModal" @click.self="showPasswordModal = false">
        <div class="password-modal">
          <div class="modal-header">
            <h3>修改密码</h3>
            <button class="close-btn" @click="showPasswordModal = false">
              <el-icon :size="18"><Close /></el-icon>
            </button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>当前密码</label>
              <input 
                type="password" 
                v-model="passwordForm.oldPassword" 
                placeholder="请输入当前密码"
              />
            </div>
            <div class="form-group">
              <label>新密码</label>
              <input 
                type="password" 
                v-model="passwordForm.newPassword" 
                placeholder="请输入新密码（至少6位）"
              />
            </div>
            <div class="form-group">
              <label>确认新密码</label>
              <input 
                type="password" 
                v-model="passwordForm.confirmPassword" 
                placeholder="请再次输入新密码"
              />
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="showPasswordModal = false">取消</button>
            <button class="confirm-btn" @click="handleChangePassword" :disabled="passwordLoading">
              {{ passwordLoading ? '修改中...' : '确认修改' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 外观设置 -->
      <div class="rounded-2xl border shadow-md overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-blue-400 to-purple-500 text-white shadow-md"><el-icon><Moon /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('ai.darkMode') }}</div>
          </div>
          <el-switch v-model="isDark" @change="toggleTheme" style="--el-switch-on-color: #3b82f6" />
        </div>
      </div>

      <!-- 其他设置 -->
      <div class="rounded-2xl border shadow-md overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/privacy')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-indigo-400 to-blue-500 text-white shadow-md"><el-icon><Document /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.privacy') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/terms')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-teal-400 to-green-500 text-white shadow-md"><el-icon><Tickets /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.terms') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
        <div class="cell hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors" @click="router.push('/user/feedback')">
          <div class="cell-left">
            <div class="icon bg-gradient-to-br from-rose-400 to-pink-500 text-white shadow-md"><el-icon><ChatLineRound /></el-icon></div>
            <div class="cell-title" :style="{ color: 'var(--text-primary)'">{{ $t('settings.feedback') }}</div>
          </div>
          <el-icon :style="{ color: 'var(--text-secondary)'"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="rounded-2xl border shadow-md overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <button class="w-full h-14 text-red-500 font-bold bg-gradient-to-r from-red-50 to-transparent hover:from-red-100 transition-all" @click="logout">{{ $t('settings.logout') }}</button>
      </div>

      <div class="rounded-2xl border shadow-md overflow-hidden" :style="{ backgroundColor: 'var(--bg-white)', borderColor: 'var(--border-color)' }">
        <button class="w-full h-14 font-bold bg-gradient-to-r from-gray-50 to-transparent hover:from-gray-100 transition-all" :style="{ color: 'var(--text-secondary)'}" @click="deactivate">{{ $t('settings.deactivate') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { computed, ref } from 'vue'
import { ArrowLeft, ArrowRight, User, MapLocation, Stamp, Odometer, Document, Tickets, ChatLineRound, Moon, Lock, Close } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'
import { changePasswordApi } from '@/api/user'

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

// 密码修改相关
const showPasswordModal = ref(false)
const passwordLoading = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const handleChangePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    ElMessage.warning('请输入当前密码')
    return
  }
  if (!passwordForm.value.newPassword || passwordForm.value.newPassword.length < 6) {
    ElMessage.warning('新密码至少需要6位')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  passwordLoading.value = true
  try {
    const res = await changePasswordApi(passwordForm.value.oldPassword, passwordForm.value.newPassword)
    if (res.code === '200') {
      ElMessage.success('密码修改成功，请重新登录')
      showPasswordModal.value = false
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
      // 退出登录
      userStore.logout()
      router.replace('/login')
    } else {
      ElMessage.error(res.message || '密码修改失败')
    }
  } catch (e) {
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
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

/* 密码修改弹窗样式 */
.password-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.password-modal {
  background: white;
  border-radius: 16px;
  width: 320px;
  max-width: 90vw;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 16px;
  font-weight: 600;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: #f3f4f6;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 20px 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  display: block;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #4f46e5;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #e5e7eb;
}

.cancel-btn {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
}

.confirm-btn {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: none;
  background: #4f46e5;
  color: white;
  cursor: pointer;
}

.confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>

