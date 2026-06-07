import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import i18n from './i18n/index'
import { useThemeStore } from './stores/theme'

// 导入新的设计系统样式
import '@/styles/design-tokens.css'
import '@/styles/global.css'
import 'flag-icons/css/flag-icons.min.css'

import ElementPlus from '@/plugins/element-plus-plugin'

// Vant 按需导入（仅导入使用的组件）
import { Button, Field, Cell, CellGroup, Dialog, Toast, Skeleton, List, PullRefresh, Empty, Loading } from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)
const pinia = createPinia()

// 使用插件
app.use(pinia)
app.use(router)
app.use(i18n)

// 安装 Element Plus
app.use(ElementPlus)

// 安装 Vant 组件
app.use(Button)
app.use(Field)
app.use(Cell)
app.use(CellGroup)
app.use(Dialog)
app.use(Toast)
app.use(Skeleton)
app.use(List)
app.use(PullRefresh)
app.use(Empty)
app.use(Loading)

app.mount('#app')

// 初始化主题
const themeStore = useThemeStore()
themeStore.initTheme()
themeStore.watchSystemTheme()
