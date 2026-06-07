/**
 * 骨架屏状态管理 Hook
 * 用于管理加载状态的骨架屏显示
 */
import { ref, computed } from 'vue'

export function useSkeleton() {
  const loading = ref(false)
  const error = ref(null)
  const data = ref(null)

  const isLoading = computed(() => loading.value)
  const hasError = computed(() => !!error.value)
  const hasData = computed(() => !!data.value)

  const startLoading = () => {
    loading.value = true
    error.value = null
  }

  const stopLoading = () => {
    loading.value = false
  }

  const setData = (newData) => {
    data.value = newData
    loading.value = false
    error.value = null
  }

  const setError = (err) => {
    error.value = err
    loading.value = false
    data.value = null
  }

  const reset = () => {
    loading.value = false
    error.value = null
    data.value = null
  }

  return {
    loading,
    error,
    data,
    isLoading,
    hasError,
    hasData,
    startLoading,
    stopLoading,
    setData,
    setError,
    reset
  }
}

// 带异步数据获取的 Hook
export function useAsyncSkeleton(fetchFn) {
  const { 
    loading, 
    error, 
    data, 
    isLoading, 
    hasError, 
    hasData,
    startLoading,
    setData,
    setError
  } = useSkeleton()

  const fetch = async (...args) => {
    startLoading()
    try {
      const result = await fetchFn(...args)
      setData(result)
      return result
    } catch (err) {
      setError(err)
      throw err
    }
  }

  const reload = (...args) => fetch(...args)

  return {
    loading,
    error,
    data,
    isLoading,
    hasError,
    hasData,
    fetch,
    reload
  }
}

export default {
  useSkeleton,
  useAsyncSkeleton
}
