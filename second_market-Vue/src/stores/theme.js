import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(false)

  // 初始化主题
  const initTheme = () => {
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      isDark.value = savedTheme === 'dark'
    } else {
      // 如果没有保存的主题，检查系统偏好
      isDark.value = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    applyTheme()
  }

  // 切换主题
  const toggleTheme = () => {
    isDark.value = !isDark.value
    applyTheme()
  }

  // 应用主题
  const applyTheme = () => {
    const html = document.documentElement
    if (isDark.value) {
      html.classList.add('dark')
      localStorage.setItem('theme', 'dark')
    } else {
      html.classList.remove('dark')
      localStorage.setItem('theme', 'light')
    }
  }

  // 监听系统主题变化
  const watchSystemTheme = () => {
    if (window.matchMedia) {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (!localStorage.getItem('theme')) {
          isDark.value = e.matches
          applyTheme()
        }
      })
    }
  }

  return {
    isDark,
    initTheme,
    toggleTheme,
    applyTheme,
    watchSystemTheme
  }
})
