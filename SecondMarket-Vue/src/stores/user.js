import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n' // 引入i18n
import { loginApi, registerApi, getUserInfoApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const { t } = useI18n() // 获取翻译函数
  // 状态
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'admin')
  const isUser = computed(() => user.value?.role === 'user')

  // 动作
  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }

  const normalizeUser = (userData) => {
    if (!userData) return null
    const next = { ...userData }
    if (!next.username && next.uid) next.username = next.uid
    if (!next.uid && next.username) next.uid = next.username
    if (!next.nickname && next.name) next.nickname = next.name
    return next
  }

  const setUser = (userData) => {
    if (userData) {
      user.value = normalizeUser(userData)
      localStorage.setItem('user', JSON.stringify(user.value))
    } else {
      user.value = null
      localStorage.removeItem('user')
    }
  }

  const login = async (credentials) => {
    try {
      setUser(null)
      const response = await loginApi(credentials)
      if (response.success) {
        setToken(response.data.token)
        setUser(response.data.user)
        ElMessage.success(t('user.login.success'))
        return { success: true }
      } else {
        ElMessage.error(response.message || t('user.login.fail'))
        return { success: false, message: response.message }
      }
    } catch (error) {
      const backendMessage = error?.response?.data?.message || error?.response?.data?.msg
      const hint = backendMessage || error?.message || t('user.login.error')
      ElMessage.error(hint)
      return { success: false, message: hint }
    }
  }

  const register = async (userData) => {
    try {
      const response = await registerApi(userData)
      if (response.success) {
        ElMessage.success(t('user.register.success'))
        return { success: true }
      } else {
        ElMessage.error(response.message || t('user.register.fail'))
        return { success: false, message: response.message }
      }
    } catch (error) {
      const backendMessage = error?.response?.data?.message || error?.response?.data?.msg
      const hint = backendMessage || error?.message || t('user.register.error')
      ElMessage.error(hint)
      return { success: false, message: hint }
    }
  }

  const logout = () => {
    setToken('')
    setUser(null)
    ElMessage.success(t('user.logout.success'))
  }

  const getUserInfo = async () => {
    try {
      const response = await getUserInfoApi()
      if (response.code === '200' || response.success) {
        const userData = response.data
        setUser(userData)
        return { success: true }
      } else {
        logout()
        return { success: false }
      }
    } catch (error) {
      console.error(t('user.getUserInfo.errorLog'), error)
      logout()
      return { success: false }
    }
  }

  const updateUser = (userData) => {
    if (user.value && userData) {
      user.value = normalizeUser({ ...user.value, ...userData })
      localStorage.setItem('user', JSON.stringify(user.value))
      return
    }
    setUser(userData)
  }

  return {
    // 状态
    user,
    token,
    // 计算属性
    isLoggedIn,
    isAdmin,
    isUser,
    // 动作
    login,
    register,
    logout,
    getUserInfo,
    setUser,
    setToken,
    updateUser
  }
})
