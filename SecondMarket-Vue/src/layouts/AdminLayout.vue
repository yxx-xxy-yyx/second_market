<template>
  <div class="admin-layout">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon 
            class="menu-toggle" 
            size="20" 
            @click="isMobileScreen ? (showMobileMenu = true) : toggleSidebar()"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          
          <div class="admin-title">
            <el-icon class="title-icon" size="24">
              <Setting />
            </el-icon>
            <span>{{ $t('layout.adminTitle') }}</span>
          </div>
          
          <!-- 面包屑导航 -->
          <el-breadcrumb class="breadcrumb hidden md:flex" separator="/">
            <el-breadcrumb-item 
              v-for="item in breadcrumbList" 
              :key="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <div class="lang-switch">
            <LangSwitcher />
          </div>
          <!-- 工具栏 -->
          <div class="header-tools">
            <el-tooltip :content="$t('layout.toolTips.refresh')" placement="bottom">
              <el-icon class="tool-icon" size="18" @click="refreshPage">
                <Refresh />
              </el-icon>
            </el-tooltip>
            
            <el-tooltip :content="$t('layout.toolTips.fullscreen')" placement="bottom">
              <el-icon class="tool-icon" size="18" @click="toggleFullscreen">
                <FullScreen />
              </el-icon>
            </el-tooltip>
          </div>
          
          <!-- 用户信息 -->
          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="avatarUrl">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  {{ $t('layout.dropdown.profile') }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  {{ $t('layout.dropdown.logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-container class="main-container">
        <!-- 侧边栏 -->
        <el-aside v-if="!isMobileScreen" class="admin-sidebar" :width="sidebarWidth">
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            :unique-opened="true"
            class="sidebar-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><Odometer /></el-icon>
              <template #title>{{ $t('layout.menu.dashboard') }}</template>
            </el-menu-item>
            
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <template #title>{{ $t('layout.menu.users') }}</template>
            </el-menu-item>
            
            <el-menu-item index="/admin/product-review">
              <el-icon><Document /></el-icon>
              <template #title>{{ $t('layout.menu.productReview') }}</template>
            </el-menu-item>
            
            <el-menu-item index="/admin/reports">
              <el-icon><Warning /></el-icon>
              <template #title>{{ $t('layout.menu.reports') }}</template>
            </el-menu-item>
            
            <el-menu-item index="/admin/notices">
              <el-icon><Bell /></el-icon>
              <template #title>{{ $t('layout.menu.notices') }}</template>
            </el-menu-item>
            
            <el-menu-item index="/admin/profile">
              <el-icon><User /></el-icon>
              <template #title>{{ $t('layout.menu.profile') }}</template>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-drawer v-model="showMobileMenu" direction="ltr" size="280px">
          <template #header>
            <div class="drawer-header">{{ $t('layout.adminTitle') }}</div>
          </template>
          <el-menu
            :default-active="activeMenu"
            :collapse="false"
            :unique-opened="true"
            class="sidebar-menu"
            @select="(index) => { handleMenuSelect(index); showMobileMenu = false }"
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><Odometer /></el-icon>
              <template #title>{{ $t('layout.menu.dashboard') }}</template>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <template #title>{{ $t('layout.menu.users') }}</template>
            </el-menu-item>
            <el-menu-item index="/admin/product-review">
              <el-icon><Document /></el-icon>
              <template #title>{{ $t('layout.menu.productReview') }}</template>
            </el-menu-item>
            <el-menu-item index="/admin/reports">
              <el-icon><Warning /></el-icon>
              <template #title>{{ $t('layout.menu.reports') }}</template>
            </el-menu-item>
            <el-menu-item index="/admin/notices">
              <el-icon><Bell /></el-icon>
              <template #title>{{ $t('layout.menu.notices') }}</template>
            </el-menu-item>
            <el-menu-item index="/admin/profile">
              <el-icon><User /></el-icon>
              <template #title>{{ $t('layout.menu.profile') }}</template>
            </el-menu-item>
          </el-menu>
        </el-drawer>
        
        <!-- 主内容区 -->
        <el-main class="admin-main">
          <div class="main-content">
            <router-view v-slot="{ Component, route }">
              <transition name="fade" mode="out-in">
                <component :is="Component" :key="route.path" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
      
      <!-- 底部 -->
      <el-footer v-if="!isMobileScreen" class="admin-footer">
        <div class="footer-content">
          <div class="footer-left">
            <span>&copy; 2026 SecondMarket Admin System</span>
            <el-divider direction="vertical" />
            <span>{{ $t('layout.footer.graduationProject') }}</span>
          </div>
          <div class="footer-right">
            <span class="status-indicator">
              <el-icon class="status-icon" color="#67c23a"><CircleCheckFilled /></el-icon>
              {{ $t('layout.footer.systemNormal') }}
            </span>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { ElMessage } from 'element-plus'
import { useDeviceType } from '@/utils/device'
import { 
  Fold, 
  Expand, 
  Setting, 
  Refresh, 
  FullScreen, 
  User, 
  ArrowDown, 
  House, 
  SwitchButton,
  Odometer,
  Document,
  CircleCheckFilled,
  Warning,
  Bell
} from '@element-plus/icons-vue'
import LangSwitcher from '@/components/LangSwitcher.vue'
import { useI18n } from 'vue-i18n'

const { locale, t } = useI18n()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = ref('/admin/dashboard')
const showMobileMenu = ref(false)

const { isMobileScreen, isTabletScreen } = useDeviceType()

watch(isTabletScreen, (val) => {
  if (val) isCollapse.value = true
})

watch(isMobileScreen, (val) => {
  if (val) isCollapse.value = true
}, { immediate: true })

const sidebarWidth = computed(() => isCollapse.value ? '64px' : '220px')

const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

// 面包屑导航数据
const breadcrumbList = computed(() => {
  const pathArray = route.path.split('/').filter(path => path)
  const breadcrumbs = []
  
  let currentPath = ''
  pathArray.forEach((path, index) => {
    currentPath += `/${path}`
    
    if (index === 0) {
      breadcrumbs.push({ title: t('layout.adminTitle'), path: '/admin' })
    } else {
      let title = path
      switch (path) {
        case 'dashboard':
          title = t('layout.menu.dashboard')
          break
        case 'users':
          title = t('layout.menu.users')
          break
        case 'product-review':
          title = t('layout.menu.productReview')
          break
        case 'reports':
          title = t('layout.menu.reports')
          break
        case 'notices':
          title = t('layout.menu.notices')
          break
        case 'profile':
          title = t('layout.menu.profile')
          break
      }
      breadcrumbs.push({ title, path: currentPath })
    }
  })
  
  return breadcrumbs
})

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    activeMenu.value = newPath
  },
  { immediate: true }
)

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 菜单选择
const handleMenuSelect = (index) => {
  if (index !== route.path) {
    router.push(index)
  }
}

// 用户操作
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'logout':
      userStore.logout()
      router.replace('/login')
      ElMessage.success(t('layout.logoutSuccess'))
      break
  }
}

// 刷新页面
const refreshPage = () => {
  location.reload()
}

// 全屏切换
const toggleFullscreen = () => {
  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    document.documentElement.requestFullscreen()
  }
}
</script>

<style scoped>
/* 语言切换样式 */
.lang-switch {
  top: 20px;
  right: 24px;
  z-index: 999;
}

.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.menu-toggle {
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.menu-toggle:hover {
  color: #409eff;
}

.admin-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.title-icon {
  color: #409eff;
}

.breadcrumb {
  margin-left: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 15px;
}

.tool-icon {
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.tool-icon:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 12px;
  height: 40px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: #606266;
}

.dropdown-icon {
  font-size: 12px;
  color: #909399;
}

.main-container {
  height: calc(100vh - 60px - 50px);
}

.admin-sidebar {
  background: #001529;
  transition: width 0.3s;
}

.sidebar-menu {
  border-right: none;
  background: #001529;
  height: 100%;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu .el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.75);
  background: transparent;
}

/* 子菜单项样式 */
.sidebar-menu :deep(.el-sub-menu .el-menu) {
  background: #000c17;
}

.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
  background: transparent;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu .el-sub-menu__title:hover),
.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item:hover) {
  color: #fff;
  background: #1890ff;
}

.sidebar-menu :deep(.el-menu-item.is-active),
.sidebar-menu :deep(.el-sub-menu .el-menu .el-menu-item.is-active) {
  color: #fff;
  background: #1890ff;
}

.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.main-content {
  min-height: calc(100vh - 140px);
}

.admin-footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 50px;
}

.footer-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  font-size: 14px;
  color: #909399;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 5px;
}

.status-icon {
  font-size: 14px;
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-header {
    padding: 0 15px;
  }
  
  .header-left {
    gap: 15px;
  }
  
  .breadcrumb {
    display: none;
  }
  
  .admin-main {
    padding: 15px;
  }
  
  .sidebar-menu {
    width: 200px;
  }
}
</style>
