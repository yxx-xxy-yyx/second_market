import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n' // 引入i18n
import { loginApi, registerApi, getUserInfoApi, getMeApi } from '@/api/user'
import { useSchoolStore } from '@/stores/school'

export const useUserStore = defineStore('user', () => {
  const { t } = useI18n() // 获取翻译函数
  // 状态
  // user.value 从 localStorage 读入的是缓存（不可信），仅用于 UI 展示
  // 真正的权限判断必须通过 login() 或 fetchCurrentUser() 从后端返回后才有效
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const token = ref(localStorage.getItem('token') || '')
  // userVerified: user 对象是否经过后端接口验证（login 接口或 /user/me 接口返回）
  // localStorage 中读入的缓存角色不设置此标志，因此 isAdmin/isUser 不会被篡改影响
  const userVerified = ref(false)

  // 计算属性
  // isLoggedIn 只判断 token 存在 + 有 user 缓存对象（不依赖 role）
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  // isAdmin/isUser 必须经过后端验证的 user 对象才返回 true，防止 localStorage 篡改 role
  const isAdmin = computed(() => userVerified.value && user.value?.role === 'admin')
  const isUser = computed(() => userVerified.value && user.value?.role === 'user')

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

  const setUser = (userData, { verified = false } = {}) => {
    if (userData) {
      user.value = normalizeUser(userData)
      localStorage.setItem('user', JSON.stringify(user.value))
    } else {
      user.value = null
      localStorage.removeItem('user')
    }
    if (verified) {
      userVerified.value = true
    }
  }

  const login = async (credentials) => {
    try {
      setUser(null)
      userVerified.value = false
      const response = await loginApi(credentials)
      if (response.success) {
        setToken(response.data.token)
        // login 接口返回的 user 来自后端数据库，可信
        setUser(response.data.user, { verified: true })
        try {
          const schoolStore = useSchoolStore()
          const currentSelected = localStorage.getItem('schoolId') || ''
          const userSchoolId = response.data.user?.schoolId
          if (!currentSelected && userSchoolId) {
            schoolStore.setSchool(String(userSchoolId))
          }
        } catch (e) { }
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

  /**
   * 从后端 /user/me 获取当前登录用户的可信信息
   * 页面刷新后、或路由守卫需要确认角色时调用
   */
  const fetchCurrentUser = async () => {
    if (!token.value) {
      userVerified.value = false
      return { success: false, message: t('common.notLogin') }
    }
    try {
      const response = await getMeApi()
      if (response.code === '200' || response.success) {
        const userData = response.data
        setUser(userData, { verified: true })
        return { success: true }
      } else {
        logout()
        return { success: false }
      }
    } catch (error) {
      console.error(t('user.getUserInfo.errorLog'), error)
      return { success: false, error }
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
    userVerified.value = false
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
    userVerified,
    // 计算属性
    isLoggedIn,
    isAdmin,
    isUser,
    // 动作
    login,
    register,
    logout,
    getUserInfo,
    fetchCurrentUser,
    setUser,
    setToken,
    updateUser
  }
})
