<template>
  <div class="user-header">
    <div class="header-container">
      <div class="header-left">
        <div class="logo" @click="router.push('/user/dashboard')">
          <BrandLogo :title="t('common.projectName')" class="brand" />
        </div>

        <div class="search-box hidden lg:block" @click="router.push('/user/search')" style="cursor: pointer;">
          <div class="search-input-placeholder">
            <el-icon>
              <Search />
            </el-icon>
            <span class="text-gray-400 text-sm">{{ t('nav.searchProducts') }}</span>
          </div>
        </div>
      </div>

      <div class="header-nav hidden lg:flex">
        <div v-for="item in navMenu" :key="item.path"
          :class="['nav-item', { active: isActive(item.path) }, currentLang]" @click="router.push(item.path)">
          {{ item.label }}
        </div>
      </div>

      <div class="header-right">
        <!-- 学校选择器 → 改成可搜索下拉框 -->
        <el-select v-model="selectedSchool" :placeholder="t('nav.selectSchool')" class="school-select" filterable
          clearable popper-class="school-select-popper" @change="handleSchoolChange">
          <el-option v-for="item in schoolStore.schoolList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>

        <LangSwitcher />

        <el-button type="primary" @click="router.push('/user/publish')" class="publish-btn hidden lg:inline-flex">
          <el-icon>
            <Plus />
          </el-icon>
          {{ t('common.postProduct') }}
        </el-button>

        <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
          <div class="icon-btn" @click="handleMessage">
            <el-icon :size="20">
              <Bell />
            </el-icon>
          </div>
        </el-badge>

        <el-dropdown @command="handleUserCommand" trigger="click">
          <div class="user-info">
            <el-avatar :size="32" :src="avatarUrl">
              <el-icon>
                <User />
              </el-icon>
            </el-avatar>
            <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
            <el-icon class="dropdown-icon" :size="14">
              <ArrowDown />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon>
                  <User />
                </el-icon>
                {{ t('common.profile') }}
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon>
                  <SwitchButton />
                </el-icon>
                {{ t('common.logout') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { formatAvatarUrl } from '@/utils/url'
import { messageApi } from '@/api/message'
import { chatApi } from '@/api/chat'
import { useI18n } from 'vue-i18n'
import { useSchoolStore } from '@/stores/school'
import LangSwitcher from '@/components/LangSwitcher.vue'
import BrandLogo from '@/components/BrandLogo.vue'
import {
  Search,
  Bell,
  User,
  SwitchButton,
  Plus
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { t, locale } = useI18n()
const currentLang = locale.value
const schoolStore = useSchoolStore()

const unreadCount = ref(0)

const navMenu = computed(() => [
  { label: t('nav.home'), path: '/user/dashboard' },
  { label: t('nav.products'), path: '/user/products' },
  { label: t('nav.myProducts'), path: '/user/my-products' },
  { label: t('nav.myOrders'), path: '/user/orders' },
  { label: t('nav.myAccount'), path: '/user/profile' }
])


const selectedSchool = computed({
  get: () => schoolStore.selectedSchool,
  set: (value) => schoolStore.setSchool(value)
})

// 学校切换事件
const handleSchoolChange = (value) => {
  schoolStore.setSchool(value)
}

onMounted(() => {
  if (!schoolStore.schoolList.length) {
    schoolStore.getSchoolList()
  }
})

watch(locale, async () => {
  await schoolStore.getSchoolList()
})

const avatarUrl = computed(() => formatAvatarUrl(userStore.user?.avatar))

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

// 搜索已移至搜索页面 /user/search

const handleMessage = () => {
  router.push('/user/messages')
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'logout':
      userStore.logout()
      router.replace('/login')
      break
  }
}

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const [msgRes, chatRes] = await Promise.all([
      messageApi.getUnreadCount(),
      chatApi.getUnreadCount()
    ])

    let total = 0
    if (msgRes.code === '200') total += (msgRes.data || 0)
    if (chatRes.code === '200') total += (chatRes.data || 0)

    unreadCount.value = total
  } catch (error) {
    console.error('获取未读消息数失败:', error)
  }
}

onMounted(() => {
  fetchUnreadCount()
  setInterval(fetchUnreadCount, 60000)
})
</script>

<style scoped>
.user-header {
  height: 60px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(235, 238, 245, 0.6);
}

.header-container {
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: nowrap;
  overflow: hidden;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;
  min-width: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.3s;
  color: var(--primary-color);
}

.logo:hover {
  opacity: 0.8;
}

.brand {
  display: inline-flex;
  align-items: center;
  min-width: 0;
}

.search-box {
  width: 200px;
}

.search-input-placeholder {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 32px;
  padding: 0 12px;
  border-radius: 20px;
  background-color: #f5f7fa;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.search-input-placeholder:hover {
  border-color: #c0c4cc;
  background-color: #fff;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 0 1 auto;
  justify-content: center;
  min-width: 0;
  overflow-x: auto;
  scrollbar-width: none;
}

.header-nav::-webkit-scrollbar {
  display: none;
}

.nav-item {
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  padding: 8px 8px;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  white-space: nowrap;
}

/* 英文菜单项加大左右 padding */
.nav-item.en {
  font-size: 11.4px;
  padding: 8px 3px;
  /* 左右更宽，让英文词看起来不挤 */
}

/* 韩文菜单项稍微加一点 */
.nav-item.ko {
  padding: 8px 13px;
}

/* 中文保持默认 */
.nav-item.zh {
  padding: 8px 8px;
}

.nav-item:hover {
  color: #409eff;
  background-color: #f0f7ff;
}

.nav-item.active {
  color: #409eff;
  font-weight: 500;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
  border-radius: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

/* 学校选择器样式 */
.school-select {
  width: 100px;
}

.school-select :deep(.el-input__wrapper) {
  height: 32px;
  border-radius: 20px;
  background: rgba(245, 247, 250, 0.8);
  border: 1px solid #ebeef5;
  box-shadow: none;
  transition: all 0.3s;
}

.school-select :deep(.el-input__wrapper):hover {
  background: #fff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.school-select :deep(.el-input__inner) {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  padding: 0 6px;
}

/* 限制下拉框最大高度 */
:deep(.school-select-popper .el-select-dropdown__wrap) {
  max-height: 320px !important;
}

.lang-dropdown {
  margin-right: 5px;
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #303133;
  padding: 4px 10px;
  height: 32px;
  border-radius: 20px;
  background: rgba(245, 247, 250, 0.8);
  border: 1px solid #ebeef5;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.lang-btn:hover {
  background: #fff;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.lang-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  font-size: 16px;
}

.lang-text {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
}

.lang-arrow {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s;
}

.lang-dropdown:hover .lang-arrow {
  transform: rotate(180deg);
  color: #409eff;
}

.lang-menu .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  font-size: 14px;
}

.lang-menu .el-dropdown-menu__item.active {
  color: #409eff;
  background-color: #ecf5ff;
}

.lang-item-icon {
  font-size: 18px;
  margin-right: 6px;
}

.publish-btn {
  border-radius: 20px;
  padding: 10px 5px;
  font-weight: 500;
}

.message-badge {
  cursor: pointer;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #606266;
  transition: all 0.3s;
  cursor: pointer;
}

.icon-btn:hover {
  background-color: #f0f7ff;
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f0f7ff;
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  color: #909399;
  transition: transform 0.3s;
}

.user-info:hover .dropdown-icon {
  transform: rotate(180deg);
}

@media (max-width: 1200px) {
  .header-container {
    gap: 16px;
  }

  .header-left {
    gap: 16px;
  }

  .header-nav {
    gap: 12px;
  }

  .search-box {
    width: 220px;
  }

  .nav-item {
    font-size: 14px;
    padding: 6px 12px;
  }
}

@media (max-width: 992px) {
  .header-container {
    gap: 12px;
  }

  .header-nav {
    gap: 8px;
    max-width: 300px;
  }

  .nav-item {
    font-size: 13px;
    padding: 6px 10px;
  }

  .search-box {
    width: 180px;
  }

  .publish-btn {
    padding: 8px 16px;
  }
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 12px;
    gap: 8px;
  }

  .logo-text {
    display: none;
  }

  .header-nav {
    display: none;
  }

  .search-box {
    flex: 1;
    min-width: 120px;
    max-width: 200px;
  }

  .username {
    display: none;
  }

  .publish-btn {
    padding: 8px 12px;
    min-width: auto;
  }

  .publish-btn .el-icon {
    margin-right: 0;
  }
}

@media (max-width: 576px) {
  .header-container {
    padding: 0 10px;
    gap: 6px;
  }

  .search-box {
    max-width: 150px;
  }

  .header-right {
    gap: 8px;
  }

  .user-info {
    padding: 6px 8px;
  }
}

@media screen and (max-width: 768px) {
  .header-nav {
    display: none;
  }

  .publish-btn {
    display: none;
  }

  .username {
    display: none;
  }
}
</style>
