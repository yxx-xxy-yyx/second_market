import axios from 'axios'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import i18n from '@/i18n'

const t = (key, params) => i18n.global.t(key, params)

let activeRequestCount = 0
let loadingInstance = null
let loadingTimer = null

const startGlobalLoading = () => {
  activeRequestCount += 1
  if (activeRequestCount !== 1) return

  loadingTimer = setTimeout(() => {
    loadingInstance = ElLoading.service({
      lock: true,
      text: t('common.loading'),
      background: 'rgba(255, 255, 255, 0.55)'
    })
  }, 200)
}

const stopGlobalLoading = () => {
  if (activeRequestCount <= 0) return
  activeRequestCount -= 1
  if (activeRequestCount !== 0) return

  if (loadingTimer) {
    clearTimeout(loadingTimer)
    loadingTimer = null
  }
  if (loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

/**
 * 获取当前可用的 token
 * 优先从 Pinia store 读取，其次从 localStorage 兜底（刷新页面后 store 为空的场景）
 */
const getAuthToken = () => {
  try {
    const userStore = useUserStore()
    if (userStore && userStore.token) return userStore.token
  } catch (_) {
    // Pinia 未初始化时忽略
  }
  return localStorage.getItem('token') || ''
}

const clearAuthAndRedirect = () => {
  try {
    const userStore = useUserStore()
    userStore.logout()
  } catch (_) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  if (router.currentRoute.value.path !== '/login') {
    ElMessage.error(t('common.authExpired'))
    router.replace('/login')
  }
}

// 创建 axios 实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 跳过不需要 loading 的请求
    if (!config._silent) startGlobalLoading()

    const token = getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    stopGlobalLoading()
    if (!error?.config?._silent) {
      console.error('请求错误:', error)
    }
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    stopGlobalLoading()
    const { data } = response

    // 后端统一返回格式: { code, message, data, success }
    if (data && typeof data === 'object' && (data.code !== undefined || data.success !== undefined)) {
      // 兼容后端新老两种返回字段命名
      const code = String(data.code ?? (data.success ? '200' : '500'))
      const normalized = { ...data, code }

      // 401: 未登录 / token 过期
      if (code === '401') {
        clearAuthAndRedirect()
        return Promise.reject(new Error(data.message || t('common.authExpired')))
      }

      // 业务成功: code === '200' 或 success === true
      const isBizSuccess = code === '200' || data.success === true
      if (!isBizSuccess) {
        ElMessage.error(data.message || t('common.requestFailed'))
      }

      return normalized
    }

    // 非标准返回：直接透传，默认视为成功
    return {
      success: true,
      code: '200',
      message: t('common.success'),
      data: data
    }
  },
  (error) => {
    stopGlobalLoading()
    const { response, message } = error
    const isSilent = error?.config?._silent

    if (response) {
      const { status, data } = response

      // 处理 401 登录失效
      if (status === 401) {
        clearAuthAndRedirect()
        return Promise.reject(error)
      }

      if (!isSilent) {
        const errorMsg = (data && (data.message || data.msg)) ||
          (status === 403 && t('common.noPermission')) ||
          (status === 404 && t('common.resourceNotFound')) ||
          (status === 500 && t('common.serverError')) ||
          t('common.requestFailedWithStatus', { status })
        ElMessage.error(errorMsg)
      }
    } else if (message && message.toLowerCase().includes('timeout')) {
      if (!isSilent) ElMessage.error(t('common.requestTimeout'))
    } else if (message && message.toLowerCase().includes('network error')) {
      if (!isSilent) ElMessage.error(t('common.networkError'))
    } else {
      if (!isSilent) ElMessage.error(message || t('common.requestFailed'))
    }

    return Promise.reject(error)
  }
)

/**
 * 通用 GET 请求
 * @param {string} url
 * @param {object} params
 * @param {object} options - axios 额外配置（如 _silent: true）
 */
export const getRequest = (url, params = {}, options = {}) => {
  return request({ url, method: 'GET', params, ...options })
}

/**
 * 通用 POST 请求
 */
export const postRequest = (url, data = {}, options = {}) => {
  return request({ url, method: 'POST', data, ...options })
}

/**
 * 通用 PUT 请求
 */
export const putRequest = (url, data = {}, options = {}) => {
  return request({ url, method: 'PUT', data, ...options })
}

/**
 * 通用 DELETE 请求
 */
export const deleteRequest = (url, params = {}, options = {}) => {
  return request({ url, method: 'DELETE', params, ...options })
}

export default request
