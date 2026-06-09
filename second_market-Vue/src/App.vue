<template>
  <el-config-provider :locale="elementLocale">
    <div id="app">
      <MobileHeader v-if="showHeader" />
      <router-view v-slot="{ Component, route }">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useDeviceType } from '@/utils/device'
import { useI18n } from 'vue-i18n'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import ko from 'element-plus/es/locale/lang/ko'
import MobileHeader from '@/components/MobileHeader.vue'

const { isMobileScreen } = useDeviceType()
const { locale } = useI18n()
const route = useRoute()

const elementLocale = computed(() => {
  if (locale.value === 'en') return en
  if (locale.value === 'ko') return ko
  return zhCn
})

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

const initApp = () => {
  document.documentElement.lang = locale.value || 'zh'
}

onMounted(initApp)

watch(locale, (newLang) => {
  document.documentElement.lang = newLang || 'zh'
})
</script>

<style>
#app {
  height: 100%;
  width: 100%;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-default);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 移动端优化 */
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

/* 可触摸滚动优化 */
.page-container {
  -webkit-overflow-scrolling: touch;
}
</style>
