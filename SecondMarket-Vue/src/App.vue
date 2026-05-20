<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { useUserStore } from '@/stores/user'

export default {
  name: 'App',
  setup() {
    const userStore = useUserStore()
    
    // 应用初始化
    const initApp = () => {
      // 可以在这里进行一些全局初始化工作
    }
    
    initApp()
    
    return {
      userStore
    }
  }
}
</script>

<style>
/* 全局样式优化 */
:root {
  --el-color-primary: var(--primary-color);
  --el-border-radius-base: var(--card-radius);
  --el-box-shadow-light: var(--card-shadow);
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
  transform: translateY(-2px);
  box-shadow: var(--card-shadow-hover) !important;
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
</style>
