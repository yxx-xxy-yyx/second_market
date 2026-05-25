<template>
  <el-config-provider :locale="elementLocale">
    <div id="app">
      <MobileHeader v-if="showHeader" />
      <router-view />
    </div>
  </el-config-provider>
</template>

<script>
import MobileHeader from '@/components/MobileHeader.vue'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { computed, watch } from 'vue'
import { useDeviceType } from '@/utils/device'
import { useI18n } from 'vue-i18n'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import ko from 'element-plus/es/locale/lang/ko'
import { Locale as VantLocale } from 'vant'
import vantZhCN from 'vant/es/locale/lang/zh-CN'
import vantEnUS from 'vant/es/locale/lang/en-US'
import vantKoKR from 'vant/es/locale/lang/ko-KR'

export default {
  name: 'App',
  components: { MobileHeader },
  setup() {
    const userStore = useUserStore()
    const route = useRoute()
    const { isMobileScreen } = useDeviceType()
    const { locale } = useI18n()

    const elementLocale = computed(() => {
      if (locale.value === 'en') return en
      if (locale.value === 'ko') return ko
      return zhCn
    })

    const applyLocaleSideEffects = (lang) => {
      document.documentElement.lang = lang || 'zh'
      if (lang === 'en') VantLocale.use('en-US', vantEnUS)
      else if (lang === 'ko') VantLocale.use('ko-KR', vantKoKR)
      else VantLocale.use('zh-CN', vantZhCN)
    }

    applyLocaleSideEffects(locale.value)
    watch(locale, (v) => applyLocaleSideEffects(v))

    const showHeader = computed(() => {
      if (!isMobileScreen.value) return false
      const p = route.path
      return (
        p === '/user/dashboard' ||
        p === '/user/categories' ||
        p === '/user/messageChat' ||
        p === '/user/profile'
      )
    })

    // 应用初始化
    const initApp = () => {
      // 可以在这里进行一些全局初始化工作
    }

    initApp()

    return {
      userStore
      , showHeader
      , elementLocale
    }
  }
}
</script>

<style>
/* 全局样式优化 */
:root {
  --el-color-primary: var(--primary-color);
  --el-border-radius-base: 8px;
  --el-box-shadow-light: 0 4px 12px rgba(0, 0, 0, 0.08);
}

body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  background-color: var(--bg-color);
  -webkit-font-smoothing: antialiased;
  -webkit-text-size-adjust: 100%;
  overscroll-behavior: none;
}

#app {
  height: 100%;
  width: 100%;
}

@media (max-width: 767px) {
  body {
    touch-action: manipulation;
  }

  input,
  textarea,
  select {
    font-size: 16px;
  }

  .el-button {
    min-height: 44px;
  }

  .el-input__wrapper,
  .el-select__wrapper {
    min-height: 44px;
  }

  .el-card:hover {
    transform: none;
  }
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 卡片通用悬浮效果 */
.el-card {
  border-radius: 12px !important;
  border: 1px solid rgba(235, 238, 245, 0.6) !important;
  transition: all 0.3s ease !important;
}

.el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1) !important;
}

/* 按钮圆角优化 */
.el-button {
  border-radius: 8px !important;
  font-weight: 500 !important;
}

.el-button--round {
  border-radius: 20px !important;
}

/* 输入框圆角优化 */
.el-input__wrapper {
  border-radius: 8px !important;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

/* 使页面内的自定义学校选择器样式与 header 中的保持一致 */
.custom-capsule-select {
  min-width: 120px;
}

.custom-capsule-select .el-select__wrap,
.custom-capsule-select .el-input__wrapper {
  min-height: 38px !important;
  padding: 8px 12px !important;
  border-radius: 18px !important;
  background: rgba(245, 247, 250, 0.95) !important;
  border: 1px solid #ebeef5 !important;
}

/* 使 page-container 可触摸滚动更顺滑 */
.page-container {
  -webkit-overflow-scrolling: touch;
}
</style>
