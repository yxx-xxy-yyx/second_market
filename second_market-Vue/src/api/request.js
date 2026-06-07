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
  }, 150)
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

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    if (!config.noLoading) {
      startGlobalLoading()
    }
    // 从 pinia store 获取 token
    try {
      const userStore = useUserStore()
      if (userStore.token) {
        config.headers.Authorization = `Bearer ${userStore.token}`
      }
    } catch (e) {
      // 防止在 store 初始化前调用报错
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
    if (!response.config?.noLoading) {
      stopGlobalLoading()
    }
    const { data } = response

    // 统一处理响应数据结构 - 后端返回格式：{code, message, data, success}
    if (data.success !== undefined) {
      const normalized = { ...data }
      if (normalized.code !== undefined) {
        normalized.code = String(normalized.code)
      } else {
        normalized.code = normalized.success ? '200' : '500'
      }

      if (normalized.code == '401') {
        const userStore = useUserStore()
        userStore.logout()
        if (router.currentRoute.value.path !== '/login') {
          ElMessage.error(t('common.authExpired'))
          router.replace('/login')
        }
        return Promise.reject(new Error(normalized.message || '登录失效'))
      }

      return normalized
    }

    // 兼容旧格式：如果有code字段，转换为success格式
    if (data.code !== undefined) {
      if (data.code === '401') {
        const userStore = useUserStore()
        userStore.logout()
        if (router.currentRoute.value.path !== '/login') {
          ElMessage.error(t('common.authExpired'))
          router.replace('/login')
        }
        return Promise.reject(new Error(data.message || '登录失效'))
      }

      return {
        success: data.code === '200',
        message: data.message || t('common.success'),
        data: data.data,
        code: data.code
      }
    }

    // 如果没有标准字段，默认认为请求成功
    return {
      success: true,
      data: data,
      message: t('common.success')
    }
  },
  (error) => {
    if (!error?.config?.noLoading) {
      stopGlobalLoading()
    }
    const { response, message } = error
    const isSilent = error?.config?._silent

    if (response) {
      const { status, data } = response

      // 处理 401 登录失效
      if (status === 401) {
        try {
          const userStore = useUserStore()
          userStore.logout()
          if (router.currentRoute.value.path !== '/login') {
            ElMessage.error(t('common.authExpired'))
            router.replace('/login')
          }
        } catch (e) { }
        return Promise.reject(error)
      }

      if (!isSilent) {
        switch (status) {
          case 403:
            ElMessage.error(t('common.noPermission'))
            break
          case 404:
            ElMessage.error(t('common.resourceNotFound'))
            break
          case 500:
            ElMessage.error(data?.message || t('common.serverError'))
            break
          default:
            ElMessage.error(data?.message || t('common.requestFailedWithStatus', { status }))
        }
      }
    } else if (message.includes('timeout')) {
      if (!isSilent) ElMessage.error(t('common.requestTimeout'))
    } else if (message.includes('Network Error')) {
      if (!isSilent) ElMessage.error(t('common.networkError'))
    } else {
      if (!isSilent) ElMessage.error(t('common.requestFailed'))
    }

    return Promise.reject(error)
  }
)

export default request
