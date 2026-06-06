import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import i18n from './i18n/index'
import './index.css'
import { useThemeStore } from './stores/theme'

// 导入 Vant
import Vant from 'vant'
import 'vant/lib/index.css'

// 导入全局样式
import '@/styles/index.css'
import '@/styles/variables.css'
import '@/assets/styles/global-animations.css'
import 'flag-icons/css/flag-icons.min.css'

const app = createApp(App)
const pinia = createPinia()

// 注册常用的 Element Plus 图标
const icons = [
  'User', 'Edit', 'Bell', 'Document', 'Upload', 'Setting',
  'Calendar', 'Check', 'Warning', 'House', 'ArrowDown',
  'SwitchButton', 'Plus', 'Search', 'Refresh', 'Download',
  'Delete', 'CircleCheck', 'CircleClose', 'Monitor',
  'Lightning', 'Grid', 'Lock', 'ArrowRight', 'ArrowLeft',
  'UserFilled', 'Camera', 'Fold', 'Expand', 'Odometer',
  'FullScreen', 'CircleCheckFilled', 'RefreshLeft'
]

icons.forEach(iconName => {
  if (ElementPlusIconsVue[iconName]) {
    app.component(iconName, ElementPlusIconsVue[iconName])
  }
})

// 使用插件
app.use(pinia)
app.use(router)
app.use(i18n)
app.use(ElementPlus)
app.use(Vant)

app.mount('#app')

// 初始化主题
const themeStore = useThemeStore()
themeStore.initTheme()
themeStore.watchSystemTheme()