<template>
  <el-header class="header">
    <div class="header-container">
      <!-- Logo区域 -->
      <div class="logo-section">
        <router-link to="/" class="logo-link">
          <el-icon class="logo-icon" size="28">
            <ShoppingCart />
          </el-icon>
          <span class="logo-text">智能二手商城</span>
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-section">
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          :ellipsis="false"
          @select="handleMenuSelect"
        >
          <el-menu-item index="home">
            <router-link to="/">首页</router-link>
          </el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn && userStore.isUser" index="user">
            <router-link to="/user">用户中心</router-link>
          </el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn && userStore.isAdmin" index="admin">
            <router-link to="/admin">管理后台</router-link>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 用户操作区域 -->
      <div class="user-section">
        <LangSwitcher />
        <template v-if="!userStore.isLoggedIn">
          <el-button @click="$router.push('/login')" type="primary" plain>
            登录
          </el-button>
          <el-button @click="$router.push('/register')" type="primary" class="ml-10">
            注册
          </el-button>
        </template>
        <template v-else>
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
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ShoppingCart, User, ArrowDown, Setting, SwitchButton } from '@element-plus/icons-vue'
import { formatAvatarUrl } from '@/utils/url'
import LangSwitcher from '@/components/LangSwitcher.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 格式化头像URL
const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

const activeMenu = ref('home')

// 监听路由变化更新活跃菜单
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/') {
      activeMenu.value = 'home'
    } else if (newPath.startsWith('/user')) {
      activeMenu.value = 'user'
    } else if (newPath.startsWith('/admin')) {
      activeMenu.value = 'admin'
    }
  },
  { immediate: true }
)

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      await userStore.logout()
      router.push('/')
      break
  }
}
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
  height: 60px;
  line-height: 60px;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo-section {
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #409eff;
  font-weight: 600;
  font-size: 20px;
}

.logo-icon {
  margin-right: 8px;
}

.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
  background: transparent;
}

.nav-menu .el-menu-item {
  border-bottom: none;
  height: 60px;
  line-height: 60px;
}

.nav-menu .el-menu-item a {
  text-decoration: none;
  color: inherit;
}

.user-section {
  flex-shrink: 0;
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

/* 响应式设计 */
@media (max-width: 767px) {
  .header-container {
    padding: 0 15px;
  }
  
  .nav-section {
    display: none;
  }
  
  .logo-text {
    font-size: 16px;
  }

  .user-section .el-button {
    min-height: 40px;
    padding: 0 14px;
  }
}

@media (min-width: 768px) and (max-width: 1023px) {
  .header-container {
    padding: 0 18px;
  }

  .logo-text {
    font-size: 18px;
  }

  .nav-menu .el-menu-item {
    padding: 0 12px;
  }
}
</style>
